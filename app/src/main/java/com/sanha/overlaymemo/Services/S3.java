package com.sanha.overlaymemo.Services;

import android.content.ClipboardManager;

import androidx.room.Room;

import com.sanha.overlaymemo.DB.AppDatabase;
import com.sanha.overlaymemo.DB.Memo;
import com.sanha.overlaymemo.MyService;
import com.sanha.overlaymemo.R;

public class S3 extends MyService {
    public static S3 s3 ; // 정의 추가

    @Override
    public void setBasic() {
        s3 = this;
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
        memo = db.todoDao().getA(3);
    }

    @Override
    protected void attachView(){
        mView = inflate.inflate(R.layout.view_in_service3, null);
    }
}
