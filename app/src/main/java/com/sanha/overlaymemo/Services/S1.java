package com.sanha.overlaymemo.Services;

import android.content.ClipboardManager;

import androidx.room.Room;

import com.sanha.overlaymemo.DB.AppDatabase;
import com.sanha.overlaymemo.DB.Memo;
import com.sanha.overlaymemo.R;

public class S1 extends MyService {
    public static S1 s1 ; // 정의 추가

    @Override
    public void setBasic() {
        s1 = this;
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
        memo = db.todoDao().getA(1);
    }

    @Override
    protected void attachView(){
        mView = inflate.inflate(R.layout.view_in_service, null);
    }

    @Override
    protected  void setXY(){
        params.x = -100;
        params.y = -20;
        wm.updateViewLayout(mView,params);
    }
}
