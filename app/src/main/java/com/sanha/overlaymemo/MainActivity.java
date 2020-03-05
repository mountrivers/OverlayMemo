package com.sanha.overlaymemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.sanha.overlaymemo.DB.AppDatabase;
import com.sanha.overlaymemo.DB.Memo;
import com.sanha.overlaymemo.HELP.HelpActivity;
import com.sanha.overlaymemo.IDManager.IDManger;
import com.sanha.overlaymemo.Services.MyService;
import com.sanha.overlaymemo.Services.S1;
import com.sanha.overlaymemo.Services.S2;
import com.sanha.overlaymemo.Services.S3;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.sanha.overlaymemo.Services.S1.s1;
import static com.sanha.overlaymemo.Services.S2.s2;
import static com.sanha.overlaymemo.Services.S3.s3;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;

    private InterstitialAd mInterstitialAd;

    private Button sizeDown, sizeUp, widthDown, widthUp, bt_start, help_button, bt_colorChange;
    public RadioGroup radiogroup;
    private Intent serviceIntent;
    public LinearLayout buttonSet;
    public Manager m1,m2,m3;
    protected AppDatabase db;
    protected Memo memo1, memo2, memo3;
    public TextView showParentMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 전면광고 부착 */
        mInterstitialAd = IDManger.SetPopUpAd(this);
        /* 사이즈 조절 버튼 */
        controlSizeButton();
        /* 시작, 도움말 버튼 */
        setButton();
        /* DB SET. 미리보기 화면을 위해 사용 */
        setDB();
        /* selecter. 3개의 메모를 따로 관리 해 줄 도구 */
        setSelecter();

    }
    /* 사이즈 조절 버튼 */
    void controlSizeButton() {
        sizeDown = (Button) findViewById(R.id.main_fontsize_down);
        sizeUp = (Button) findViewById(R.id.main_fontsize_up);
        widthDown = (Button) findViewById(R.id.main_width_down);
        widthUp = (Button) findViewById(R.id.main_width_up);
        sizeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedManager().resizeSize(1);
                commitSize();
            }
        });
        sizeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedManager().resizeSize(0);
                commitSize();
            }
        });
        widthDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedManager().resizeWidth(1);
                commitWidth();
            }
        });
        widthUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedManager().resizeWidth(0);
                commitWidth();
            }
        });
    }
    /* 시작, 도움말 버튼 */
    public void setButton() {
        bt_start = (Button) findViewById(R.id.bt_start);
        buttonSet = (LinearLayout) findViewById(R.id.main_buttonSet);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSet.setVisibility(View.VISIBLE);
                checkPermission();
            }
        });
        help_button = (Button) findViewById(R.id.main_help_button);
        help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });
        bt_colorChange = (Button) findViewById(R.id.main_colorpicker);

        bt_colorChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorChange();
            }
        });
    }
    /* DB SET. 미리보기 화면을 위해 사용 */
    void setDB() {
        db = Room.databaseBuilder(this,
                AppDatabase.class, "memo-db")
                .allowMainThreadQueries()
                .build();
        while(db.todoDao().getC() <= 3) {
            db.todoDao().insert(new Memo(""));
        }
        memo1 = db.todoDao().getA(1);
        memo2 = db.todoDao().getA(2);
        memo3 = db.todoDao().getA(3);
    }
    /* 3개의 메모 관리 하는 역할 */
    public void setSelecter(){
        m1 = new Manager(1);
        m2 = new Manager(2);
        m3 = new Manager(3);

        m1.loadSizes(this);
        m2.loadSizes(this);
        m3.loadSizes(this);

        radiogroup = (RadioGroup)findViewById(R.id.radioset);
        showParentMemo = (TextView)findViewById(R.id.main_seeMemo);
        showParentMemo.setText("MEMO #1 \n" + memo1.toString());
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rd = (RadioButton) findViewById(checkedId);
                switch (rd.getText().toString()){
                    case "1":
                        showParentMemo.setText("MEMO #1 \n" +memo1.toString());
                        break;
                    case "2":
                        showParentMemo.setText("MEMO #2 \n" +memo2.toString());
                        break;
                    case "3":
                        showParentMemo.setText("MEMO #3 \n" +memo3.toString());
                        break;
                }
            }
        });
    }

    /* 시작하는 과정
    마시멜로 미만 버전 -> 권한 없이 실행 가능   ( checkpermission -> startact )
    마시멜로 이상 버전 -> 권한 있어야 실행
      - 권한 없음 -> 권한 받기 (페이지 띄우기) -> onActivityResult 로 권한 받아왔으면 실행 (checkpermission -> onactivityresult -> startact)
      - 권한 있음 -> 바로 실행  (checkpermission -> startact)
     */
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                startAct();
            }
        } else {
            startAct();
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리
            } else {
                startAct();
            }
        }
    }
    private void startAct() {
        switch (getSelectedManager().getId()){
            case 1:
                serviceIntent = new Intent(MainActivity.this, S1.class);
                break;
            case 2:
                serviceIntent = new Intent(MainActivity.this, S2.class);
                break;
            case 3:
                serviceIntent = new Intent(MainActivity.this, S3.class);
                break;
            default:
                break;
        }

        serviceIntent.putExtra("textsize", getSelectedManager().getmSize());
        serviceIntent.putExtra("textwidth", getSelectedManager().getPixel());

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    startService(serviceIntent);
                }
            });
        }
        else
            startService(serviceIntent);
    }


    /* 메뉴 사용 하기 위해 사용. - oncreateoptionmenu ,  onOptionitemSelected <- 여기에 버튼 정의 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_exit_button) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /* service 에 사이즈 전달 및 커밋 */
    public void commitSize() {
        if (getSelectedService() != null)
            getSelectedService().changeSize(getSelectedManager().getmSize());
        getSelectedManager().setMSize();
    }
    public void commitWidth() {
        if (getSelectedService() != null)
            getSelectedService().changeWidth(getSelectedManager().getPixel());
        getSelectedManager().setMWidth();
    }

    /* 라디오 그룹 버튼으로 어느 버튼이 선택되어있는지 확인 -> service, manager 적당히 리턴 */
    public Manager getSelectedManager(){
        RadioButton rd = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
        switch (rd.getText().toString()){
            case "1":
                return m1;
            case "2":
                return m2;
            case "3":
                return m3;
        }
        return m1;
    }
    public MyService getSelectedService(){
        RadioButton rd = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
        switch (rd.getText().toString()){
            case "1":
                return s1;
            case "2":
                return s2;
            case "3":
                return s3;
        }
        return s1;
    }

    private void colorChange(){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, 0xFFFFFFFF,true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // color is the color selected by the user.
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }

        });
        dialog.show();
    }
}