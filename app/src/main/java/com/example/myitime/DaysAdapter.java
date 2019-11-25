package com.example.myitime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DaysAdapter extends ArrayAdapter<Day> {
    private int resourceId;

    public DaysAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Day> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //获取id为resourceId的布局
        LayoutInflater mInflater= LayoutInflater.from(this.getContext());
        @SuppressLint("ViewHolder")
        View item = mInflater.inflate(this.resourceId,null);
        //获取布局里的控件
        ImageView img=item.findViewById(R.id.day_picture);
        TextView title=item.findViewById(R.id.day_title);
        TextView time=item.findViewById(R.id.day_time);
        TextView remark=item.findViewById(R.id.day_remark);
        //对布局里的控件进行更新操作
        Day day= this.getItem(position);
        assert day != null;
        img.setImageDrawable(ImageTransformation.bitmapToDrawable(ImageTransformation.byteToBitmap(day.getPicture())));
        title.setText(day.getTitle());
        time.setText(String.format("%tY年%<tm月%<td日 %<tR",day.getTime()));
        remark.setText(day.getRemark());
        //返回更新好的布局
        return item;
    }
}
