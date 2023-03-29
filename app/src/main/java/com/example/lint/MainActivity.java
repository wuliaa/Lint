package com.example.lint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String HTT = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void testUsage() {

        Log.d("111", "test");

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        Intent intent = new Intent();
        intent.getExtras();
        Color.parseColor("#ffffff");
        System.out.println(1);

        boolean a = getLocalClassName().equals(HTT);

        boolean b = getLocalClassName().equals(HTT);

        boolean c = getLocalClassName().equals(HTT);
    }
}