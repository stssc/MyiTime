package com.example.myitime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.qiujuer.genius.blur.StackBlur;

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
        TextView img=item.findViewById(R.id.day_picture);
        TextView title=item.findViewById(R.id.day_title);
        TextView time=item.findViewById(R.id.day_time);
        TextView remark=item.findViewById(R.id.day_remark);
        //对布局里的控件进行更新操作
        Day day= this.getItem(position);
        assert day != null;
        Bitmap bitmap=ImageTransformation.byteToBitmap(day.getPicture());
        bitmap= StackBlur.blur(bitmap,5,false);//设置图片模糊
        Drawable drawable=ImageTransformation.bitmapToDrawable(bitmap);
        drawable.setColorFilter(getContext().getResources().getColor(R.color.grey_primary), PorterDuff.Mode.MULTIPLY);//设置灰色滤镜，降低图片亮度，使得文字清楚明显
        img.setBackground(drawable);
        title.setText(day.getTitle());
        time.setText(String.format("%tY年%<tm月%<td日 %<tR",day.getTime()));
        remark.setText(day.getRemark());
        //返回更新好的布局
        return item;
    }
}
