package com.sanha.overlaymemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;

import static android.content.Context.MODE_PRIVATE;

public class Manager {
    public int mSize =14;


    public int mWidth = 140;

    public int getId() {
        return id;
    }

    public int id;
    public SharedPreferences spPref;
    public SharedPreferences.Editor spEditor;
    public Resources r;
    public Manager(int a){
        id = a;
    }

    public void loadSizes(Context context) {
        spPref = context.getSharedPreferences("spPref", MODE_PRIVATE);
        spEditor = spPref.edit();
        r = context.getResources();
        mSize = spPref.getInt("size"+id, 14);
        mWidth = spPref.getInt("width"+id, 150);
    }
    public void setMSize(){
        spEditor.putInt("size"+id,mSize);
        spEditor.commit();
    }
    public void setMWidth(){
        spEditor.putInt("width"+id,mWidth);
        spEditor.commit();
    }

    public int getmSize() {
        return mSize;
    }

    public void setmSize(int mSize) {
        this.mSize = mSize;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getPixel(){
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mWidth, r.getDisplayMetrics()));
    }
    public void resizeSize(int a){
        if(a==0)
            mSize++;
        else if( a== 1 && mSize > 1)
            mSize--;
    }
    public void resizeWidth(int a){
        if( a == 0)
            mWidth+=10;
        else if( a == 1 && mWidth > 140)
            mWidth-= 10;
    }
}
