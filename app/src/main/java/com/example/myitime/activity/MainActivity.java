package com.example.myitime.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myitime.R;
import com.example.myitime.model.Data;
import com.example.myitime.model.Day;
import com.example.myitime.view.DaysAdapter;
import com.example.myitime.model.Label;
import com.example.myitime.view.ColorPickerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_ADD = 0;
    public static final int REQUEST_VIEW = 1;
    public static final int[] defaultPicturesId=new int[]{R.drawable.default1,R.drawable.default2,R.drawable.default3,R.drawable.default4};
    public static final Drawable[] defaultPictures=new Drawable[defaultPicturesId.length];

    private int themeColor;
    private ArrayList<Day> days=new ArrayList<>();
    private ArrayList<Label> labels=new ArrayList<>();
    private DaysAdapter daysListAdapter;

    private Toolbar toolbar;
    private FloatingActionButton buttonAdd;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //顶部导航栏
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My iTime");
        setSupportActionBar(toolbar);
        //下方浮动按钮
        buttonAdd = findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddActivity.class);
                intent.putExtra("from","MainActivity");
                intent.putExtra("position",days.size());
                startActivityForResult(intent,REQUEST_ADD);
                overridePendingTransition(R.anim.top_defaults_view_color_picker_popup_show,R.anim.top_defaults_view_color_picker_popup_hide);//设置Activity启动动画
            }
        });
        //左侧导航抽屉
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //主页内容
        for (int i=0;i<defaultPicturesId.length;i++){
            defaultPictures[i]=getResources().getDrawable(defaultPicturesId[i]);
        }
        //数据同步（注意数据同步要放在adapter之前！不然读到了数据也不显示！）
        themeColor=Data.getThemeColor(MainActivity.this);
        showColor(themeColor);
        days=Data.getDays(MainActivity.this);
        labels=Data.getLabels(MainActivity.this);
        daysListAdapter = new DaysAdapter(MainActivity.this,R.layout.day_item,days);
        ListView daysListView = findViewById(R.id.day_list);
        daysListView.setAdapter(daysListAdapter);
        daysListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,ViewActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("day",days.get(position));
                startActivityForResult(intent,REQUEST_VIEW);
                overridePendingTransition(R.anim.top_defaults_view_color_picker_popup_show,R.anim.top_defaults_view_color_picker_popup_hide);//设置Activity启动动画
            }
        });
    }

    //在onRestart()和onDestroy()都做数据保存是为了避免某些用户像我喜欢直接按手机菜单键杀掉进程，这样App都来不及执行onDestroy就结束了，这样做能较大程度的减小用户数据丢失。本来其实在每次AddActivity按下buttonOK时做一次保存最好，因为基本上数据都是在AddActivity修改的，但是本项目的设计是只有MainActivity拥有管理数据的权力，所以如果要在AddActivity那一步就保存数据，需通知MainActivity，但这样保存的数据也不是完整的，因为活动的迁移顺序可能是Main->View->Add->View->Main，当从AddActivity回到ViewActivity时，ViewActivity还没有将最新的数据传回给MainActivity

    @Override
    protected void onRestart() {//本来想在AddActivity按下buttonOK的时候就做这个satDays和setLabels的发现不行，finish里面调用onDestroy时会出错，据说是因为finish先释放了所有的资源从栈顶移除Activity？所以就改成MainActivity onRestart的时候setDays，AddActivity 添加标签对话框按下确定的时候setLabels
        Data.setDays(MainActivity.this,days);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Data.setDays(MainActivity.this,days);
        Data.setLabels(MainActivity.this,labels);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //右上角菜单栏，暂时不启用
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_label) {

        } else if (id == R.id.nav_component) {

        } else if (id == R.id.nav_color) {//设置主题色
            //先保存原来的颜色，以免用户撤销
            final int oldThemeColor=themeColor;
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("选择颜色")
                    .setView(getColorPickerView())
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Data.setThemeColor(MainActivity.this,themeColor);//保存设置的主题颜色
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //恢复状态栏、顶部导航栏、浮动按钮颜色
                            themeColor=oldThemeColor;
                            showColor(themeColor);
                        }
                    }).create().show();
        } else if (id == R.id.nav_advanced) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_info){

        } else if (id == R.id.nav_help){

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ColorPickerView getColorPickerView() {
        ColorPickerView picker=new ColorPickerView(MainActivity.this);
        picker.setOrientation(ColorPickerView.Orientation.HORIZONTAL);//水平条
        picker.setIndicatorEnable(true);//使用指示点
        picker.setIndicatorColor(Color.argb(125,0,255,255));//指示点默认半透明，颜色是中间的青色
        picker.setOnColorPickerChangeListener(new ColorPickerView.OnColorPickerChangeListener() {
            @Override
            public void onColorChanged(ColorPickerView picker, int color) {//颜色改变时回调
                picker.setIndicatorColor(color);//指示点颜色随之改变
                showColor(color);
            }

            @Override
            public void onStartTrackingTouch(ColorPickerView picker) {

            }

            @Override
            public void onStopTrackingTouch(ColorPickerView picker) {

            }
        });
        return picker;
    }

    private void showColor(int color) {
        toolbar.setBackgroundColor(color);//顶部导航栏颜色随之改变
        ColorStateList colorStateList = new ColorStateList(new int[1][1],new int[]{color});
        buttonAdd.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
        buttonAdd.setBackgroundTintList(colorStateList);//浮动按钮颜色随之改变
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);//状态栏颜色随之改变
        themeColor=color;//主题颜色随时保存
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode){
            case REQUEST_ADD:
                if (resultCode==RESULT_OK){
                    assert intent != null;
                    days.add((Day)intent.getSerializableExtra("day"));
                    daysListAdapter.notifyDataSetChanged();
                    labels=Data.getLabels(MainActivity.this);
                }
                break;
            case REQUEST_VIEW:
                if (resultCode==ViewActivity.RESULT_DELETE){
                    assert intent != null;
                    days.remove(intent.getIntExtra("position",-1));
                    daysListAdapter.notifyDataSetChanged();
                    labels=Data.getLabels(MainActivity.this);
                }
                else if (resultCode==ViewActivity.RESULT_BACK){
                    assert intent != null;
                    days.set(intent.getIntExtra("position",-1),((Day)intent.getSerializableExtra("new_day")));
                    daysListAdapter.notifyDataSetChanged();
                    labels=Data.getLabels(MainActivity.this);
                }
            default:
                break;
        }
    }



}
