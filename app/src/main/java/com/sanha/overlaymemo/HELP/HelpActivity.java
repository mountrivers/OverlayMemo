package com.sanha.overlaymemo.HELP;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sanha.overlaymemo.R;

public class HelpActivity extends AppCompatActivity {

    Button nextHelpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setButton();

    }

    protected void setButton(){
        nextHelpButton = (Button) findViewById(R.id.nexthelp_button);

        nextHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this,Help2Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
