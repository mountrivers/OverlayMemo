package com.sanha.overlaymemo.HELP;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sanha.overlaymemo.MainActivity;
import com.sanha.overlaymemo.R;

public class Help2Activity extends AppCompatActivity {
    Button endHelpButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help2);

        setButton();
    }

    protected void setButton(){
        endHelpButton = (Button) findViewById(R.id.endhelp_button);

        endHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
