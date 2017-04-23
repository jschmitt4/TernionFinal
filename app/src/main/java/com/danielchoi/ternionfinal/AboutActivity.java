package com.danielchoi.ternionfinal;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ((TextView) findViewById(R.id.developers)).setTypeface(Typeface.createFromAsset(getAssets(),  "fonts/britanicbold.TTF"));
        ((TextView) findViewById(R.id.artWorkTV)).setTypeface(Typeface.createFromAsset(getAssets(),  "fonts/britanicbold.TTF"));
    }
}
