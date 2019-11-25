package com.example.myitime;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.myitime.ImageTransformation.*;

public class MainActivity extends AppCompatActivity{
    public static final int REQUEST_ADD = 0;
    public static final int[] defaultPicturesId=new int[]{R.drawable.b1,R.drawable.b2,R.drawable.b3,R.drawable.b4};
    public static final Drawable[] defaultPictures=new Drawable[defaultPicturesId.length];

    private ImageView cover;
    private ListView daysListView;
    private ImageButton buttonAdd;

    private ArrayList<Day> days=new ArrayList<>();
    private DaysAdapter daysListAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar=getSupportActionBar();

        cover=findViewById(R.id.cover);
        daysListView=findViewById(R.id.day_list);
        buttonAdd=findViewById(R.id.button_add);
        buttonAdd.bringToFront();

        days=new ArrayList<>();
        daysListAdapter=new DaysAdapter(MainActivity.this,R.layout.day_item,days);
        daysListView.setAdapter(daysListAdapter);
        for (int i=0;i<defaultPicturesId.length;i++){
            defaultPictures[i]=getResources().getDrawable(defaultPicturesId[i]);
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AddActivity.class);
                intent.putExtra("position",days.size());
                startActivityForResult(intent, REQUEST_ADD);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (days.size()>0){
            actionBar.hide();
            Drawable coverDrawable=bitmapToDrawable(byteToBitmap(days.get(0).getPicture()));
            coverDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);//设置灰色滤镜，降低图片亮度，使得文字清楚明显
            cover.setBackground(coverDrawable);
        }
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
