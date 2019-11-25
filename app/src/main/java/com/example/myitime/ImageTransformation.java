package com.example.myitime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

public class ImageTransformation {

    public static Bitmap byteToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap drawableToBitmap(Drawable drawable){
        return ((BitmapDrawable)drawable).getBitmap();
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap){
        return new BitmapDrawable(bitmap);
    }

    public static int nameToId(Context context,String name){
        return context.getResources().getIdentifier(name,"drawable",context.getPackageName());
    }

    public static Drawable idToDrawable(Context context, int resourceId){
        return context.getResources().getDrawable(resourceId);
    }

}
