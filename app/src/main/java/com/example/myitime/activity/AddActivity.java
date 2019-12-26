package com.example.myitime.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myitime.R;
import com.example.myitime.function.ImageTransformation;
import com.example.myitime.model.Data;
import com.example.myitime.model.Day;
import com.example.myitime.model.Label;
import com.example.myitime.view.WordWrapLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AddActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    public static final int GROUP_1 = 1;
    public static final int MENU_ITEM_WEEK = 7;
    public static final int MENU_ITEM_MONTH = 30;
    public static final int MENU_ITEM_YEAR = 365;
    public static final int MENU_ITEM_CUSTOMIZE = -1;
    public static final int REQUEST_PICTURE = 0;
    public static final int REQUEST_RESIZE = 1;
    public static final int MENU_ITEM_NULL = 0;

    private Intent intent;
    private ImageButton buttonBack, buttonOK;
    private TextView titleText, remarkText, timeTitle, timeText, periodTitle, periodText, picTitle, labelTitle, labelText;
    private EditText titleEdit, remarkEdit;
    private ConstraintLayout titleLayout, timeLayout, periodLayout, picLayout, labelLayout;

    private Calendar time = null;
    private int period = 0;
    private byte[] picture = null;
    private HashMap<String,Boolean> dayLabels = new HashMap<>();
    private ArrayList<Label> labels=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        buttonBack = findViewById(R.id.button_back);
        buttonOK = findViewById(R.id.button_ok);
        titleText = findViewById(R.id.title_text_view);
        remarkText = findViewById(R.id.remark_text_view);
        timeTitle = findViewById(R.id.time_title);
        timeText = findViewById(R.id.time);
        periodTitle = findViewById(R.id.period_title);
        periodText = findViewById(R.id.period);
        picTitle = findViewById(R.id.pic_title);
        labelTitle = findViewById(R.id.label_title);
        labelText = findViewById(R.id.label);
        titleEdit = findViewById(R.id.title_edit_text);
        remarkEdit = findViewById(R.id.remark_edit_text);
        titleLayout = findViewById(R.id.title_layout);
        timeLayout = findViewById(R.id.time_layout);
        periodLayout = findViewById(R.id.period_layout);
        picLayout = findViewById(R.id.pic_layout);
        labelLayout = findViewById(R.id.label_layout);

        buttonBack.setOnClickListener(this);
        buttonOK.setOnClickListener(this);
        timeLayout.setOnClickListener(this);
        periodLayout.setOnClickListener(this);
        picLayout.setOnClickListener(this);
        labelLayout.setOnClickListener(this);

        timeLayout.setOnLongClickListener(this);
        this.registerForContextMenu(periodLayout);//注册上下文菜单，不然点击了没反应
        periodLayout.setOnLongClickListener(this);//但是又不想让它长按了也创建上下文菜单，只能写一个空的事件把它长按的响应事件重写掉了

        labels=Data.getLabels(AddActivity.this);//不管从哪来都要加载labels的
        intent = getIntent();
        if (Objects.equals(intent.getStringExtra("from"), "ViewActivity")){//从ViewActivity来的，说明是要改数据，需要加载Day对象（包括里面的dayLabels）
            initDay();
        }
        else if (Objects.equals(intent.getStringExtra("from"),"MainActivity")){//从MainActivity来的，说明是要加数据，需要加载标签集Data.labels以及默认背景颜色
            titleLayout.setBackgroundColor(Data.getThemeColor(AddActivity.this));
            initLabels();
        }

    }

    private void initLabels() {
        for (Label label:labels){
            dayLabels.put(label.getName(),false);//默认不选
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @NonNull
    private void initDay() {
        Intent intent=getIntent();
        Day day=(Day)intent.getSerializableExtra("day");
        assert day != null;
        //记得同步数据
        time=day.getTime();
        period=day.getPeriod();
        picture=day.getPicture();
        dayLabels=day.getLabels();
        //背景图片，标题
        Drawable drawable= ImageTransformation.bitmapToDrawable(ImageTransformation.byteToBitmap(day.getPicture()));
        drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);//设置灰色滤镜，降低图片亮度，使得文字清楚明显
        titleLayout.setBackground(drawable);
        titleEdit.setText(day.getTitle());
        remarkEdit.setText(day.getRemark());
        //日期、周期、标签
        timeText.setText(String.format("%tY年%<tm月%<td日 %<tR", time.getTime()));
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
            default:
                periodText.setText(period+"天");
                break;
        }
        StringBuilder showLabels=new StringBuilder("已选：");
        for (Label label:labels){
            if (dayLabels.get(label.getName())){//标签选中
                showLabels.append(label.getName()).append(",");
            }
        }
        if (!new String(showLabels).equals("已选")){//说明已有选中的标签
            showLabels.deleteCharAt(showLabels.length()-1);//去掉最后一个逗号
            labelText.setText(showLabels);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {//在将要显示视图v的上下文菜单menu时调用（menu：正在构建的上下文菜单；v：为其构建上下文菜单的视图；menuInfo：menu的附加信息）
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("周期");
        //调用MenuItem add(int groupId,int itemId,int order,charSequence title);方法为上下文菜单添加菜单项，其中，order为常量NONE（0）表示无所谓菜单项的顺序
        menu.add(GROUP_1, MENU_ITEM_WEEK, Menu.NONE, "每周");
        menu.add(GROUP_1, MENU_ITEM_MONTH, Menu.NONE, "每月");
        menu.add(GROUP_1, MENU_ITEM_YEAR, Menu.NONE, "每年");
        menu.add(GROUP_1, MENU_ITEM_CUSTOMIZE, Menu.NONE, "自定义");
        menu.add(GROUP_1, MENU_ITEM_NULL, Menu.NONE, "无");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {//在上下文菜单的一个菜单项被选中时调用，menuItem为当前被选中的菜单项
        period = item.getItemId();
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
                final EditText editText = new EditText(AddActivity.this);
                editText.setHint("输入周期（天）");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);//设置只允许输入整数
                editText.setGravity(Gravity.CENTER);
                //对话框
                new AlertDialog.Builder(AddActivity.this)
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
                                if (editText.getText().length() != 0) {//注意判断是否输入非空！！
                                    period = Integer.parseInt(editText.getText().toString());
                                    periodText.setText(period + "天");
                                }
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @SuppressLint({"DefaultLocale", "InflateParams"})
    @Override
    public void onClick(View v) {

        if (v == buttonBack) {
            finish();
        }

        else if (v == buttonOK) {
            String title = titleEdit.getText().toString();
            if (title.length() == 0)
                Toast.makeText(AddActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
            else {
                String remark = remarkEdit.getText().toString();
                //把所有属性封装到序列化的Day类对象里传值给intent
                if (time == null)
                    time = Calendar.getInstance();//获取设置完成时的当前时间
                if (picture == null)
                    //用户没有选择图片则设置为默认图片
                    picture = ImageTransformation.bitmapToByte(ImageTransformation.drawableToBitmap(MainActivity.defaultPictures[intent.getIntExtra("position", 0) % MainActivity.defaultPictures.length]));
                Day day = new Day(picture, title, remark, time, period, dayLabels);
                intent.putExtra("day", day);
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        else if (v == timeLayout) {
            //获取默认值：当前时间
            final Calendar nowTime = Calendar.getInstance();
            time = Calendar.getInstance();
            //日历控件
            DatePickerDialog date = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    time.set(year, monthOfYear, dayOfMonth);

                    //钟表控件
                    TimePickerDialog clock = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        //@RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            time.set(Calendar.MINUTE, minute);
                            //注意，这句setText必须放在最里面的点击时间里面！不然放外面的话用户还没选好时间这句就执行了！出来的就是用户选时间之前的默认时间！
                            timeText.setText(String.format("%tY年%<tm月%<td日 %<tR", time.getTime()));
                        }
                    }, nowTime.get(Calendar.HOUR_OF_DAY), nowTime.get(Calendar.MINUTE), true);//钟表默认时间
                    clock.show();

                }
            }, nowTime.get(Calendar.YEAR), nowTime.get(Calendar.MONTH), nowTime.get(Calendar.DAY_OF_MONTH));//日历默认日期
            date.show();

        }

        else if (v == periodLayout) {
            v.showContextMenu();//弹出上下文菜单
        }

        else if (v == picLayout) {
            //调用手机相册
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PICTURE);
        }

        else if (v == labelLayout) {
            //读取标签集数据
            labels=Data.getLabels(AddActivity.this);
            //标签选择对话框
            WordWrapLayout dialogView = new WordWrapLayout(AddActivity.this);
            //先用假数据，到时候做设置页的时候建了数据库的时候再改
            for (Label label:labels){
                LabelView labelView=new LabelView(AddActivity.this, label.getName());
                if (dayLabels.get(label.getName())!=null)
                    labelView.setChecked(dayLabels.get(label.getName()));
                else
                    labelView.setChecked(false);
                dialogView.addView(labelView);
            }
            new AlertDialog.Builder(AddActivity.this)
                    .setView(dialogView)
                    .setNeutralButton("添加新标签", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //新增标签对话框
                            final EditText editText = new EditText(AddActivity.this);
                            editText.setHint("10个字符以内");
                            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)}); //限制最多输入10个字符
                            editText.setGravity(Gravity.CENTER);//居中显示
                            new AlertDialog.Builder(AddActivity.this)
                                    .setTitle("添加标签")
                                    .setView(editText)//这样有个缺点，就是EditText会顶着对话框的两边，因为直接把EditText作为整个View然后setView了
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (editText.getText().length() != 0) {//注意判断是否输入非空！！
                                                //先用假数据，到时候做设置页的时候建了数据库的时候再改
                                                labels.add(new Label(editText.getText().toString()));
                                                dayLabels.put(editText.getText().toString(),false);//默认不选
                                                Data.setLabels(AddActivity.this,labels);
                                            }
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //先用假数据，到时候做设置页的时候建了数据库的时候再改
                            StringBuilder showLabels=new StringBuilder("已选：");
                            for (Label label:labels){
                                if (dayLabels.get(label.getName())){
                                    showLabels.append(label.getName()).append(",");
                                }
                            }
                            dialog.dismiss();
                            if (new String(showLabels).equals("已选："))
                                showLabels=new StringBuilder();//什么都没选就什么都不显示
                            else
                                showLabels.deleteCharAt(showLabels.length()-1);//去掉最后一个逗号
                            labelText.setText(showLabels);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }

    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onLongClick(View v) {
        if (v == timeLayout) {
            //之所以这么诡异的变量声明是因为……final变量才能被内部匿名类访问，但final变量又不能被其他类修改，所以只能用长度为1的final数组了
            final AlertDialog[] dialog = {null};
            //设置默认值
            final Calendar now = Calendar.getInstance();
            final Calendar[] after = {null};
            final Calendar[] before = {null};
            //对话框布局
            @SuppressLint("InflateParams")
            View dialogView = getLayoutInflater().inflate(R.layout.date_calculator, null);
            //获取布局控件
            final TextView nowShow = dialogView.findViewById(R.id.show_now);
            final TextView afterShow = dialogView.findViewById(R.id.show_after);
            final TextView beforeShow = dialogView.findViewById(R.id.show_before);
            EditText afterEdit = dialogView.findViewById(R.id.edit_after);
            EditText beforeEdit = dialogView.findViewById(R.id.edit_before);
            ConstraintLayout nowSelect = dialogView.findViewById(R.id.select_now);
            TextView afterSelect = dialogView.findViewById(R.id.select_after);
            TextView beforeSelect = dialogView.findViewById(R.id.select_before);
            //默认先显示当前时间
            nowShow.setText(String.format("%tY年%<tm月%<td日", now.getTime()));
            afterShow.setText(String.format("天之后：%tY年%<tm月%<td日", now.getTime()));
            beforeShow.setText(String.format("天之前：%tY年%<tm月%<td日", now.getTime()));
            //限制输入框只能输入四位整数
            afterEdit.setInputType(InputType.TYPE_CLASS_NUMBER);//限制只能输入整数
            afterEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)}); //限制只能输入四位数
            beforeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
            beforeEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            //输入监听事件，与输入框同步计算日期
            afterEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int beforeStart, int count) {
                    //不能这样！相当于把now的引用给了after[0]！after[0]一变now就跟着变了！Java跟C++不一样！易错！这样做的后果是：我打一个12再按一下退格变成2，我本来想要从+12变成+1的效果的，结果变成+12之后再+1就变成+13了
//                    after[0]=now;
//                    if (s.length()!=0)
//                        after[0].add(Calendar.DATE,Integer.parseInt(s.toString()));
                    after[0] = Calendar.getInstance();
                    after[0].set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));//输入框为空默认值为now
                    if (s.length() != 0)//输入框不为空才调用日期计算
                        after[0].add(Calendar.DATE, Integer.parseInt(s.toString()));
                    afterShow.setText(String.format("天之后：%tY年%<tm月%<td日", after[0].getTime()));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            beforeEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int beforeStart, int count) {
                    before[0] = Calendar.getInstance();
                    before[0].set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    if (s.length() != 0)
                        before[0].add(Calendar.DATE, -Integer.parseInt(s.toString()));
                    beforeShow.setText(String.format("天之前：%tY年%<tm月%<td日", before[0].getTime()));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            //点击事件
            nowSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //日历控件
                    new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            now.set(year, monthOfYear, dayOfMonth);
                            nowShow.setText(String.format("%tY年%<tm月%<td日", now.getTime()));
                            afterShow.setText(String.format("天之后：%tY年%<tm月%<td日", now.getTime()));
                            beforeShow.setText(String.format("天之前：%tY年%<tm月%<td日", now.getTime()));
                        }
                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();//日历默认日期
                }
            });
            afterSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            time = after[0] == null ? now : after[0];
                            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            time.set(Calendar.MINUTE, minute);
                            dialog[0].dismiss();//需要手动关闭对话框
                            timeText.setText(String.format("%tY年%<tm月%<td日 %<tR", time.getTime()));
                        }
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();//钟表默认时间
                }
            });
            beforeSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            time = before[0] == null ? now : before[0];
                            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            time.set(Calendar.MINUTE, minute);
                            dialog[0].cancel();//需要手动关闭对话框
                            timeText.setText(String.format("%tY年%<tm月%<td日 %<tR", time.getTime()));
                        }
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();//钟表默认时间
                }
            });
            //自定义对话框
            dialog[0] = new AlertDialog.Builder(AddActivity.this)
                    .setView(dialogView)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            //显示对话框
            dialog[0].show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
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
        startActivityForResult(intent, REQUEST_RESIZE);
    }

    private void showResizeImage(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            Drawable drawable = ImageTransformation.bitmapToDrawable(bitmap);
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);//设置灰色滤镜，降低图片亮度，使得文字清楚明显
            titleLayout.setBackground(drawable);
            assert bitmap != null;
            picture = ImageTransformation.bitmapToByte(bitmap);
        }
    }

    private class LabelView extends ToggleButton {

        public LabelView(Context context, final String text) {
            super(context);
            setAllCaps(false);//取消按钮上文字自动大写
            setText(text);
            setTextOn("√" + text);
            setTextOff(text);
            setBackgroundResource(R.drawable.label_background);
            setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String content=buttonView.getText().toString();
                    if (isChecked) {
                        dayLabels.remove(content);
                        dayLabels.put(content,true);
                    }
                    else{
                        String name=content.substring(1,content.length());//这有个坑：输出日志发现这里的name是开关触发前的name，也就是说，选中变成"√xxx"的时候，获取到的name是xxx，取消变成"xxx"的时候，获取到的name是"√xxx"，我无语了
                        dayLabels.remove(content);
                        dayLabels.put(name,false);
                    }
                }
            });
        }

    }
}