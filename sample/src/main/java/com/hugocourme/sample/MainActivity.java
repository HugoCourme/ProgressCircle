package com.hugocourme.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hugocourme.progresscircle.ProgressCircle;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressCircle progressCircle = (ProgressCircle) findViewById(R.id.progress_circle);
        Button incButton = (Button) findViewById(R.id.increment);
        incButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressCircle.incrementProgress(new Random().nextInt(40));
            }
        });

        Button goToButton = (Button) findViewById(R.id.go_to_25);
        goToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressCircle.setProgress(25);
            }
        });
    }
}
