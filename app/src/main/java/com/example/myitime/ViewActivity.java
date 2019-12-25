package com.example.myitime;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Calendar;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int RESULT_DELETE = -123;
    public static final int REQUEST_EDIT = 1;
    public static final int RESULT_BACK = 123;
    public static final int TIME_CHANGE = 0;

    private ConstraintLayout titleLayout;
    private ImageButton buttonBack,buttonDelete,buttonEdit;
    private TextView title,date,timing;
    private int position;
    private Day day;

    private Handler handler;
    private boolean isAlive=true;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        //隐藏标题栏
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }

        titleLayout=findViewById(R.id.title_layout);
        buttonBack=findViewById(R.id.button_back);
        buttonDelete=findViewById(R.id.button_delete);
        buttonEdit=findViewById(R.id.button_edit);
        title=findViewById(R.id.title);
        date=findViewById(R.id.date);
        timing=findViewById(R.id.timing);

        Intent intent=getIntent();
        position=intent.getIntExtra("position",-1);
        day=(Day)intent.getSerializableExtra("day");
        init(day);
        buttonBack.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);

        handler=new Handler(){
          @Override
          public void handleMessage(Message msg){
              if (msg.what==TIME_CHANGE)
                timing.setText(String.valueOf(msg.obj));
          }
        };
    }

    @Override
    protected void onRestart(){
        isAlive=true;
        new Timer().start();
        super.onRestart();
    }

    @Override
    protected void onStop() {//被隐藏的时候就应该停止计时线程，否则跳转到AddActivity后它依然会一直消耗内存下去
        isAlive=false;
        super.onStop();
    }

    @SuppressLint("DefaultLocale")
    private void init(final Day day) {
        //背景图片
        assert day != null;
        Drawable drawable=ImageTransformation.bitmapToDrawable(ImageTransformation.byteToBitmap(day.getPicture()));
        drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);//设置灰色滤镜，降低图片亮度，使得文字清楚明显
        titleLayout.setBackground(drawable);
        //标题、日期
        title.setText(day.getTitle());
        date.setText(String.format("%tY年%<tm月%<td日 %<ta",day.getTime().getTime()));
        //倒计时
        new Timer().start();

    }

    @Override
    public void onClick(View v) {
        if (v==buttonBack){
            Intent intent=getIntent();
            intent.putExtra("new_day",day);
            setResult(RESULT_BACK,intent);
            finish();
        }
        else if (v==buttonDelete){
            new AlertDialog.Builder(ViewActivity.this)
                    .setTitle("是否删除该计时？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setResult(RESULT_DELETE,getIntent());
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        else if (v==buttonEdit){
            Intent intent=new Intent(ViewActivity.this,AddActivity.class);
            intent.putExtra("from","ViewActivity");
            intent.putExtra("day",day);
            startActivityForResult(intent,REQUEST_EDIT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode){
            case REQUEST_EDIT:
                if (resultCode==RESULT_OK){
                    assert intent != null;
                    day=(Day)intent.getSerializableExtra("day");
                    init(day);
                }
                break;
            default:
                break;
        }
    }

    private class Timer extends Thread{
        @Override
        public void run(){
            while (isAlive){
                StringBuilder stringDelta=new StringBuilder();
                Calendar now=Calendar.getInstance();
                long deltaYear=now.get(Calendar.YEAR)-day.getTime().get(Calendar.YEAR);//先计算相差多少年
                System.out.println(deltaYear);
                if (deltaYear!=0){
                    now.set(Calendar.YEAR,day.getTime().get(Calendar.YEAR));//设为同年后再计算相差多少天
                    long delta=now.getTimeInMillis()-day.getTime().getTimeInMillis();
                    if (deltaYear*delta<0){//实际相差不到deltaYear年
                        if (deltaYear>0){
                            deltaYear--;
                            now.set(Calendar.YEAR,now.get(Calendar.YEAR)+1);
                        }
                        else{
                            deltaYear++;
                            now.set(Calendar.YEAR,now.get(Calendar.YEAR)-1);
                        }
                    }
                }
                deltaYear=Math.abs(deltaYear);
                System.out.println(now.getTime());
                long sameYearDelta=Math.abs(now.getTimeInMillis()-day.getTime().getTimeInMillis());
                System.out.println(sameYearDelta);
                long deltaDay=sameYearDelta/(1000*60*60*24);
                long deltaHour=sameYearDelta%(1000*60*60*24)/(1000*60*60);
                long deltaMinute=sameYearDelta%(1000*60*60)/(1000*60);
                long deltaSecond=sameYearDelta%(1000*60)/1000;
                if (deltaYear!=0)
                    stringDelta.append(deltaYear).append("年");
                if (deltaDay!=0)
                    stringDelta.append(deltaDay).append("天");
                if (deltaHour!=0)
                    stringDelta.append(deltaHour).append("小时");
                if (deltaMinute!=0)
                    stringDelta.append(deltaMinute).append("分钟");
                stringDelta.append(deltaSecond).append("秒");
                Message msg=handler.obtainMessage();
                msg.what=TIME_CHANGE;
                msg.obj=stringDelta;
                handler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
