package com.example.myitime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_ADD = 0;
    public static final int REQUEST_VIEW = 1;
    public static final String CACHE_FILE_NAME = "days.txt";
    public static final int[] defaultPicturesId=new int[]{R.drawable.default1,R.drawable.default2,R.drawable.default3,R.drawable.default4};
    public static final Drawable[] defaultPictures=new Drawable[defaultPicturesId.length];

    private ArrayList<Day> days;
    private DaysAdapter daysListAdapter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //顶部导航栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My iTime");
        setSupportActionBar(toolbar);
        //下方浮动按钮
        FloatingActionButton buttonAdd = findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddActivity.class);
                intent.putExtra("from","MainActivity");
                intent.putExtra("position",days.size());
                startActivityForResult(intent,REQUEST_ADD);
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
        days=new ArrayList<>();
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(MainActivity.this.openFileInput(CACHE_FILE_NAME));
            days=(ArrayList<Day>)objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            }
        });
    }

    //在onRestart()和onDestroy()都做数据保存是为了避免某些用户像我喜欢直接按手机菜单键杀掉进程，这样App都来不及执行onDestroy就结束了，这样做能较大程度的减小用户数据丢失。本来其实在每次AddActivity按下buttonOK时做一次保存最好，因为基本上数据都是在AddActivity修改的，但是本项目的设计是只有MainActivity拥有管理数据的权力，所以如果要在AddActivity那一步就保存数据，需通知MainActivity，但这样保存的数据也不是完整的，因为活动的迁移顺序可能是Main->View->Add->View->Main，当从AddActivity回到ViewActivity时，ViewActivity还没有将最新的数据传回给MainActivity

    @Override
    protected void onRestart() {
        save();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        save();
        save();
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
            // Handle the camera action
        } else if (id == R.id.nav_label) {

        } else if (id == R.id.nav_component) {

        } else if (id == R.id.nav_color) {

        } else if (id == R.id.nav_advanced) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_info){

        } else if (id == R.id.nav_help){

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                }
                break;
            case REQUEST_VIEW:
                if (resultCode==ViewActivity.RESULT_DELETE){
                    assert intent != null;
                    days.remove(intent.getIntExtra("position",-1));
                    daysListAdapter.notifyDataSetChanged();
                }
                else if (resultCode==ViewActivity.RESULT_BACK){
                    assert intent != null;
                    days.set(intent.getIntExtra("position",-1),((Day)intent.getSerializableExtra("new_day")));
                    daysListAdapter.notifyDataSetChanged();
                }
            default:
                break;
        }
    }

    private void save(){
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(MainActivity.this.openFileOutput(CACHE_FILE_NAME, Context.MODE_PRIVATE));//调用Context的openFileOutput方法打开App内部文件
            objectOutputStream.writeObject(days);//将序列化对象days通过对象输出流写入内部文件
            objectOutputStream.close();//记得关闭输出流！！
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
