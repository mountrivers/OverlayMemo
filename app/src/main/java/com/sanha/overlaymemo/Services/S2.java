package com.sanha.overlaymemo.Services;

import android.content.ClipboardManager;

import androidx.room.Room;

import com.sanha.overlaymemo.DB.AppDatabase;
import com.sanha.overlaymemo.DB.Memo;
import com.sanha.overlaymemo.R;

public class S2 extends MyService {
    public static S2 s2 ; // 정의 추가
    private int serviceId = 2;
    @Override
    public void setBasic() {
        s2 = this;
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    protected void setDB() {
        db = Room.databaseBuilder(this,
                AppDatabase.class, "memo-db")
                .allowMainThreadQueries()
                .build();
        while(db.todoDao().getC() <= 3) {
            db.todoDao().insert(new Memo(""));
        }
        memo = db.todoDao().getA(serviceId);

        spPref = getSharedPreferences("spPref", MODE_PRIVATE);
        spEditor = spPref.edit();
        textSize = spPref.getInt("size"+ serviceId, 14);
        textWidth = spPref.getInt("width"+ serviceId, 150);
        backColor = spPref.getInt("color"+ serviceId, 0xFFFFFFFF);

    }

    @Override
    protected void alterSize() {
        changeSize(textSize);
        changeWidth(getPixel());
        changeColor(backColor);
    }
    @Override
    protected void attachView(){
        mView = inflate.inflate(R.layout.view_in_service2, null);
    }

    @Override
    protected  void setXY(){
        params.x = 0;
        params.y = 30;
        wm.updateViewLayout(mView,params);
    }
}
