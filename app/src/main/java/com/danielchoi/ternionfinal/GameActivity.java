package com.danielchoi.ternionfinal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    public GridBoard playerGrid;
    public Vibrator vb;
    public boolean playerGridShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        ImageButton ib = (ImageButton) findViewById(R.id.imageButton2);
        ib.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.imageButton2){
            if(playerGrid == null){
                playerGrid = new GridBoard(this);
                playerGrid.setBoard();
                playerGridShow = true;
            }else if(playerGridShow){
                playerGrid.hideGrid();
                playerGridShow = false;
            }else if (!playerGridShow){
                playerGrid.showGrid();
                playerGridShow = true;
            }

        }
    }
}