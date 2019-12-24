package com.example.myitime;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_ADD = 0;
    public static final int[] defaultPicturesId=new int[]{R.drawable.b1,R.drawable.b2,R.drawable.b3,R.drawable.b4};
    public static final Drawable[] defaultPictures=new Drawable[defaultPicturesId.length];

    private ArrayList<Day> days;
    private DaysAdapter daysListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //顶部导航栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //下方浮动按钮
        FloatingActionButton buttonAdd = findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddActivity.class);
                intent.putExtra("position",days.size());
                startActivityForResult(intent, REQUEST_ADD);
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
        days=new ArrayList<>();
        ListView daysListView = findViewById(R.id.day_list);
        daysListAdapter = new DaysAdapter(MainActivity.this, R.layout.day_item, days);
        daysListView.setAdapter(daysListAdapter);
        for (int i=0;i<defaultPicturesId.length;i++){
            defaultPictures[i]=getResources().getDrawable(defaultPicturesId[i]);
        }

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
            default:
                break;
        }
    }

}
