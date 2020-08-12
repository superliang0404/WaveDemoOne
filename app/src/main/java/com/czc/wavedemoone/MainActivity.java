package com.czc.wavedemoone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private WaveView wave;
    private Button btn_wave;
    private EditText edit_wave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btn_wave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wave.setProgressWithAnim(Float.valueOf(edit_wave.getText().toString().trim()));
            }
        });
    }

    public void init(){
        wave = findViewById(R.id.wave);
        btn_wave = findViewById(R.id.btn_wave);
        edit_wave = findViewById(R.id.edit_wave);
    }
}