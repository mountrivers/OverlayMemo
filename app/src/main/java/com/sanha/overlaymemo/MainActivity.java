package com.sanha.overlaymemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.sanha.overlaymemo.IDManager.IDManger;

import static com.sanha.overlaymemo.MyService.ms;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;

    private InterstitialAd mInterstitialAd;

    private  Button sizeDown, sizeUp, widthDown, widthUp, resetSize , bt_start, bt_stop;

    public SharedPreferences spPref; public SharedPreferences.Editor spEditor;

    public int textSize ,textWidth;
    public Resources r;
    AdRequest adrequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterstitialAd = IDManger.SetPopUpAd(this);

        setSizes();

        ssButton();

       loadSizes();

    }

    public void checkPermission() {
        Intent serviceIntent = new Intent(MainActivity.this,MyService.class);

        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, textWidth,r.getDisplayMetrics()));
        serviceIntent.putExtra("textsize", textSize);
        serviceIntent.putExtra("textwidth", px);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                startService(serviceIntent);
            }
        } else {
            startService(serviceIntent);
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
                startService(new Intent(MainActivity.this, MyService.class));
            }
        }
    }



    void setSizes(){
        sizeDown = (Button) findViewById(R.id.main_fontsize_down);
        sizeUp =(Button) findViewById(R.id.main_fontsize_up);
        widthDown = (Button) findViewById(R.id.main_width_down);
        widthUp = (Button) findViewById(R.id.main_width_up);
        resetSize = (Button) findViewById(R.id.main_size_reset);

        sizeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textSize > 0)
                    textSize--;
                commitSize();
            }
        });

        sizeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSize++;
                commitSize();
            }
        });

        widthDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textWidth>20)
                    textWidth-=10;
                commitWidth();
            }
        });

        widthUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWidth+=10;
                commitWidth();
            }
        });

        resetSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSize = 14;
                textWidth= 150;
                commitSize();
                commitWidth();
            }
        });
    }

    public void commitSize(){
        if(ms!=null)
            ms.changeSize(textSize);
        spEditor.putInt("size",textSize);
        spEditor.commit();
    }

    public void commitWidth(){
        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, textWidth,r.getDisplayMetrics()));
        if(ms!=null)
            ms.changeWidth(px);
        spEditor.putInt("width",textWidth);
        spEditor.commit();
    }

    public void ssButton(){
        bt_start = (Button) findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    mInterstitialAd.setAdListener(new AdListener(){
                        @Override
                        public void onAdClosed() {
                            checkPermission();
                        }
                    });

                }
                else
                    checkPermission();
            }
        });
        bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void loadSizes(){
        spPref = getSharedPreferences("spPref",MODE_PRIVATE);
        spEditor = spPref.edit();
        r = getResources();
        textSize = spPref.getInt("size",14);
        textWidth = spPref.getInt("width",150);
    }

}