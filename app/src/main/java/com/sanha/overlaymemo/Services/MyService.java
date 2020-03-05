package com.sanha.overlaymemo.Services;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.room.Room;

import com.sanha.overlaymemo.DB.AppDatabase;
import com.sanha.overlaymemo.DB.Memo;
import com.sanha.overlaymemo.R;

public class MyService extends Service {

    public static MyService ms;

    /* values */
    private float mStartingX, mStartingY;
    float mWidgetStartingX, mWidgetStartingY;
    int startIndex, endIndex;
    public String temp;

    /* window mangers */
    protected WindowManager wm;
    protected View mView;
    public WindowManager.LayoutParams params;
    protected LayoutInflater inflate;
    public LinearLayout backGround;

    /*  db */
    protected AppDatabase db;
    protected Memo memo;

    /* design */
    private EditText floatingText;
    private ImageButton buttonZoomingMinus, buttonZoomingPlus, buttonExit, buttonSave, buttonPaste;

    /* clipboard */
    public android.content.ClipboardManager clipboardManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* ms , clipboardmanager, inflate, wm, params, mview , inflate */
        setBasic();

        assginView();

        setButton();

        loadText();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wm != null) {
            if (mView != null) {
                wm.removeView(mView);
                mView = null;
            }
            wm = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        changeSize(intent.getExtras().getInt("textsize", 14));
        changeWidth(intent.getIntExtra("textwidth", 150));
        colorChange(intent.getIntExtra("color",0xFFFFFFFF));
        return super.onStartCommand(intent, flags, startId);
    }


    public void changeWidth(int width) {
        floatingText.setLayoutParams(new LinearLayout.LayoutParams(width, -2));
    }

    public void changeSize(int size) {
        floatingText.setTextSize(size);
    }



    public void loadText() {
        setDB();

        floatingText = (EditText) mView.findViewById(R.id.textView);
        floatingText.setText(memo.toString());
        floatingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((params.flags & WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) != 0)
                    params.flags -= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                wm.updateViewLayout(mView, params);
            }
        });

        /*      텍스트 롱 클릭시 클립보드 ( 복사, 붙여넣기, 지우기 열기 ) */
        floatingText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);//v는 클릭된 뷰를 의미
                popup.getMenuInflater().inflate(R.menu.contextual_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.paste:
                                temp = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                                startIndex = Math.max(floatingText.getSelectionStart(), 0);
                                endIndex = Math.max(floatingText.getSelectionEnd(), 0);
                                floatingText.getText().replace(Math.min(startIndex, endIndex), Math.max(startIndex, endIndex), temp);
                                break;

                            case R.id.copy:
                                temp = floatingText.getText().toString();
                                startIndex = floatingText.getSelectionStart();
                                endIndex = floatingText.getSelectionEnd();
                                temp = temp.substring(startIndex, endIndex);
                                ClipData clip = ClipData.newPlainText("clip", temp);
                                clipboardManager.setPrimaryClip(clip);
                                break;

                            case R.id.delete:
                                startIndex = Math.max(floatingText.getSelectionStart(), 0);
                                endIndex = Math.max(floatingText.getSelectionEnd(), 0);
                                floatingText.getText().replace(Math.min(startIndex, endIndex), Math.max(startIndex, endIndex), "");
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popup.show();//Popup Menu 보이기
                return false;
            }
        });
    }

    public void setButton() {
        buttonZoomingMinus = (ImageButton) mView.findViewById(R.id.button_zooming_minus); // 확대 축소 버튼
        buttonZoomingPlus = (ImageButton) mView.findViewById(R.id.button_zooming_plus);
        buttonExit = (ImageButton) mView.findViewById(R.id.button_exit); // 종료 버튼
        buttonSave = (ImageButton) mView.findViewById(R.id.button_save); // 저장 버튼
        buttonPaste = (ImageButton) mView.findViewById(R.id.button_paste);

        buttonZoomingMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingText.setVisibility(v.GONE);
                buttonExit.setVisibility(v.GONE);
                buttonSave.setVisibility(v.GONE);
                buttonPaste.setVisibility(View.GONE);
                buttonZoomingMinus.setVisibility(View.GONE);
                buttonZoomingPlus.setVisibility(View.VISIBLE);
            }
        });
        buttonZoomingPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingText.setVisibility(v.VISIBLE);
                buttonExit.setVisibility(v.VISIBLE);
                buttonSave.setVisibility(v.VISIBLE);
                buttonPaste.setVisibility(View.VISIBLE);
                buttonZoomingMinus.setVisibility(View.VISIBLE);
                buttonZoomingPlus.setVisibility(View.GONE);
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });
        buttonPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                startIndex = Math.max(floatingText.getSelectionStart(), 0);
                endIndex = Math.max(floatingText.getSelectionEnd(), 0);
                floatingText.getText().replace(Math.min(startIndex, endIndex), Math.max(startIndex, endIndex), temp);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  db.todoDao().update(new Memo(floatingText.getText().toString()));
                memo.setContent(floatingText.getText().toString());
                db.todoDao().update(memo);
            }
        });
    }



    protected  void assginView(){
        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                /*ViewGroup.LayoutParams.MATCH_PARENT*/ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT < 26 ?
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT :
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        attachView();
        setXY();
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_OUTSIDE) {

                    if ((params.flags & WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) == 0)
                        params.flags += WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    wm.updateViewLayout(mView, params);
                } else {
                    switch (ev.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mStartingX = ev.getRawX();
                            mStartingY = ev.getRawY();

                            mWidgetStartingX = params.x;
                            mWidgetStartingY = params.y;
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            float deltaX = mStartingX - ev.getRawX();
                            float deltaY = mStartingY - ev.getRawY();
                            params.x = (int) (mWidgetStartingX - deltaX);
                            params.y = (int) (mWidgetStartingY - deltaY);
                            wm.updateViewLayout(mView, params);
                            return true;
                    }

                }

                return false;
            }
        });

        wm.addView(mView, params);
        backGround = (LinearLayout) mView.findViewById(R.id.service_background);
    }
    public void colorChange(int color){
        backGround.setBackgroundColor(color);
    }
    public int getColor(){
        ColorDrawable color = (ColorDrawable) backGround.getBackground();

        return color.getColor();
    }

    protected   void stopService(){
        stopService(new Intent(this,this.getClass()));
        this.onDestroy();
    }
    /* 오버라이드 할 것 */
    public void setBasic() {
        ms = this;
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

    }
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
    protected void attachView(){
        mView = inflate.inflate(R.layout.view_in_service, null);
    }

    protected void setXY(){

    }
}

