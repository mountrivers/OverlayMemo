package com.sanha.overlaymemo;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.room.Room;

import java.util.List;

import static java.sql.Types.NULL;

public class MyService extends Service {

    public static MyService ms;

    /* values */
    private float mStartingX , mStartingY;
    float mWidgetStartingX, mWidgetStartingY;
    int sizer, startIndex, endIndex;
    public String temp;

    /* window mangers */
    WindowManager wm;
    View mView;
    public WindowManager.LayoutParams params;
    LayoutInflater inflate;

    /*  db */
    AppDatabase db;
    Todo todo;

    /* design */
    private EditText floatingText;
    private ImageButton buttonZooming , buttonExit, buttonSave;

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
        return super.onStartCommand(intent, flags, startId);

    }


    public void changeWidth(int width) {
        floatingText.setLayoutParams(new LinearLayout.LayoutParams(width, -2));
    }

    public void changeSize(int size) {
        floatingText.setTextSize(size);
    }

    public  void setBasic(){
        ms = this;
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        sizer = 1;
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

        mView = inflate.inflate(R.layout.view_in_service, null);
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
    }

    public void loadText(){
        db = Room.databaseBuilder(this,
                AppDatabase.class, "todo-db")
                .allowMainThreadQueries()
                .build();
        if (db.todoDao().getC() == 0) {
            db.todoDao().insert(new Todo(""));
        }
        todo = db.todoDao().getA(1);
        floatingText = (EditText) mView.findViewById(R.id.textView);
        floatingText.setText(todo.toString());

        floatingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((params.flags & WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) != 0)
                    params.flags -= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                wm.updateViewLayout(mView, params);
            }
        });

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
                                Toast.makeText(getApplication(), "메뉴1", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.copy:
                                temp = floatingText.getText().toString();
                                startIndex = floatingText.getSelectionStart();
                                endIndex = floatingText.getSelectionEnd();
                                temp = temp.substring(startIndex, endIndex);
                                ClipData clip = ClipData.newPlainText("clip", temp);
                                clipboardManager.setPrimaryClip(clip);

                                Toast.makeText(getApplication(), "메뉴1", Toast.LENGTH_SHORT).show();
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

    public void setButton(){
        buttonZooming = (ImageButton) mView.findViewById(R.id.button_zooming); // 확대 축소 버튼
        buttonExit = (ImageButton) mView.findViewById(R.id.button_exit); // 종료 버튼
        buttonSave = (ImageButton) mView.findViewById(R.id.button_save); // 종료 버튼

        buttonZooming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sizer == 1) {
                    floatingText.setVisibility(v.GONE);
                    buttonExit.setVisibility(v.GONE);
                    buttonSave.setVisibility(v.GONE);
                    sizer = 0;
                } else {
                    floatingText.setVisibility(v.VISIBLE);
                    buttonExit.setVisibility(v.VISIBLE);
                    buttonSave.setVisibility(v.VISIBLE);
                    sizer = 1;
                }
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MyService.this, MyService.class));
                MyService.this.onDestroy();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  db.todoDao().update(new Todo(floatingText.getText().toString()));
                todo.setContent(floatingText.getText().toString());
                db.todoDao().update(todo);
            }
        });
    }



}

