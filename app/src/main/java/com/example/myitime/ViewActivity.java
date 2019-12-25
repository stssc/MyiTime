package com.example.myitime;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int RESULT_DELETE = -123;
    public static final int REQUEST_EDIT = 1;
    public static final int RESULT_BACK = 123;
    private ConstraintLayout titleLayout;
    private ImageButton buttonBack,buttonDelete,buttonEdit;
    private TextView title,date,timing;
    private int position;
    private Day day;

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
    }

    @SuppressLint("DefaultLocale")
    private void init(Day day) {
        //背景图片
        assert day != null;
        Drawable drawable=ImageTransformation.bitmapToDrawable(ImageTransformation.byteToBitmap(day.getPicture()));
        drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);//设置灰色滤镜，降低图片亮度，使得文字清楚明显
        titleLayout.setBackground(drawable);
        //标题、日期
        title.setText(day.getTitle());
        date.setText(String.format("%tY年%<tm月%<td日 %<ta",day.getTime().getTime()));
        //倒计时
    }

    @Override
    public void onClick(View v) {
        if (v==buttonBack){
            Intent intent=getIntent();
            intent.putExtra("new_day",day);
            setResult(RESULT_BACK);
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

}
