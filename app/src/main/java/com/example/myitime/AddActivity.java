package com.example.myitime;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import static com.example.myitime.ImageTransformation.bitmapToByte;
import static com.example.myitime.ImageTransformation.drawableToBitmap;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int GROUP_1=1;
    public static final int MENU_ITEM_WEEK=7;
    public static final int MENU_ITEM_MONTH=30;
    public static final int MENU_ITEM_YEAR=365;
    public static final int MENU_ITEM_CUSTOMIZE =-1;
    public static final int REQUEST_PICTURE=0;
    public static final int REQUEST_RESIZE=1;
    public static final int MENU_ITEM_NULL=0;

    private Intent intent;
    private ImageButton buttonBack,buttonOK;
    private TextView titleText, remarkText,timeTitle, timeText,periodTitle, periodText,picTitle,labelTitle, labelText;
    private EditText titleEdit, remarkEdit;
    private ConstraintLayout titleLayout,timeLayout,periodLayout,picLayout,labelLayout;

    private Date time=null;
    private int period=0;
    private byte[] picture=null;
    private ArrayList<String> labels=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        intent=getIntent();
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }

        buttonBack=findViewById(R.id.button_back);
        buttonOK=findViewById(R.id.button_ok);
        titleText =findViewById(R.id.title_text_view);
        remarkText =findViewById(R.id.remark_text_view);
        timeTitle=findViewById(R.id.time_title);
        timeText =findViewById(R.id.time);
        periodTitle=findViewById(R.id.period_title);
        periodText =findViewById(R.id.period);
        picTitle=findViewById(R.id.pic_title);
        labelTitle=findViewById(R.id.label_title);
        labelText =findViewById(R.id.label);
        titleEdit =findViewById(R.id.title_edit_text);
        remarkEdit =findViewById(R.id.remark_edit_text);
        titleLayout=findViewById(R.id.title_layout);
        timeLayout=findViewById(R.id.time_layout);
        periodLayout=findViewById(R.id.period_layout);
        picLayout=findViewById(R.id.pic_layout);
        labelLayout=findViewById(R.id.label_layout);

        buttonBack.setOnClickListener(this);
        buttonOK.setOnClickListener(this);
        timeLayout.setOnClickListener(this);
        periodLayout.setOnClickListener(this);
        picLayout.setOnClickListener(this);
        labelLayout.setOnClickListener(this);

        this.registerForContextMenu(periodLayout);//注册上下文菜单，不然点击了没反应
        periodLayout.setOnLongClickListener(new View.OnLongClickListener() {//但是又不想让它长按了也创建上下文菜单，只能把它长按的响应事件重写掉了
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {//在将要显示视图v的上下文菜单menu时调用（menu：正在构建的上下文菜单；v：为其构建上下文菜单的视图；menuInfo：menu的附加信息）
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("周期");
        //调用MenuItem add(int groupId,int itemId,int order,charSequence title);方法为上下文菜单添加菜单项，其中，order为常量NONE（0）表示无所谓菜单项的顺序
        menu.add(GROUP_1, MENU_ITEM_WEEK, Menu.NONE, "每周");
        menu.add(GROUP_1, MENU_ITEM_MONTH,Menu.NONE,"每月");
        menu.add(GROUP_1, MENU_ITEM_YEAR, Menu.NONE, "每年");
        menu.add(GROUP_1, MENU_ITEM_CUSTOMIZE, Menu.NONE, "自定义");
        menu.add(GROUP_1, MENU_ITEM_NULL, Menu.NONE, "无");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {//在上下文菜单的一个菜单项被选中时调用，menuItem为当前被选中的菜单项
        period=item.getItemId();
        switch (period) {
            case MENU_ITEM_NULL:
                periodText.setText("无");
                break;
            case MENU_ITEM_WEEK:
                periodText.setText("每周");
                break;
            case MENU_ITEM_MONTH:
                periodText.setText("每月");
                break;
            case MENU_ITEM_YEAR:
                periodText.setText("每年");
                break;
            case MENU_ITEM_CUSTOMIZE:
                final EditText editText=new EditText(AddActivity.this);
                editText.setHint("输入周期（天）");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);//设置只允许输入整数
                editText.setGravity(Gravity.CENTER);
                //对话框
                AlertDialog dialog = new AlertDialog.Builder(AddActivity.this)
                        .setTitle("周期")
                        .setView(editText)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                period=Integer.parseInt(editText.getText().toString());
                                periodText.setText(period+"天");
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v==buttonBack){
            finish();
        }

        else if (v==buttonOK){
            String title = titleEdit.getText().toString();
            if (title.length()==0)
                Toast.makeText(AddActivity.this,"标题不能为空",Toast.LENGTH_SHORT).show();
            else{
                String remark = remarkEdit.getText().toString();
                //把所有属性封装到序列化的Day类对象里传值给intent
                if (time==null)
                    time=new Date();
                if (picture==null)
                    //用户没有选择图片则设置为默认图片
                    picture= ImageTransformation.bitmapToByte(ImageTransformation.drawableToBitmap(MainActivity.defaultPictures[intent.getIntExtra("position",0)%MainActivity.defaultPictures.length]));
                Day day=new Day(picture,title,remark,time,period,labels);
                intent.putExtra("day",day);
                setResult(RESULT_OK,intent);
                finish();
            }
        }

        else if (v==timeLayout){

        }

        else if (v==periodLayout){
            v.showContextMenu();//弹出上下文菜单
        }

        else if (v==picLayout){
            //调用手机相册
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PICTURE);
        }

        else if (v==labelLayout){

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode==RESULT_OK){
            switch (requestCode) {
                case REQUEST_PICTURE:
                    //要获取图片得先裁剪
                    resizeImage(intent.getData());
                    break;

                case REQUEST_RESIZE:
                    //获得裁剪后的图片
                    if (intent != null) {
                        showResizeImage(intent);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    //裁剪图片
    private void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        //裁剪比例
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 2);
        //裁剪大小
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 400);
        intent.putExtra("return-data", true);
        startActivityForResult(intent,REQUEST_RESIZE);
    }

    private void showResizeImage(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            Drawable drawable=ImageTransformation.bitmapToDrawable(bitmap);
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);//设置灰色滤镜，降低图片亮度，使得文字清楚明显
            titleLayout.setBackground(drawable);
            assert bitmap != null;
            picture=ImageTransformation.bitmapToByte(bitmap);
        }
    }

}
