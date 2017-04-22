package com.danielchoi.ternionfinal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
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
    // Variables
    public static final int activityRef = 2000;
    private int score=0, count=0;

    public GridBoard playerGrid, enemyGrid;
    public Vibrator vb;
    public boolean playerGridShow = false;
    TransitionDrawable transition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        ImageButton ib = (ImageButton) findViewById(R.id.imageButton2);
        transition = (TransitionDrawable) findViewById(R.id.activity_game).getBackground();
        ib.setOnClickListener(this);
      
        // Test High Score
        ImageButton ibs = (ImageButton) findViewById(R.id.imageButton);
        ibs.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
    /* Set High Scores for this game    NOT WORKING ---------
    class SetHighScore implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent scoreIntent = new Intent(getApplicationContext(), ScoreActivity.class);
            scoreIntent.putExtra("score", score);
            scoreIntent.putExtra("calling-Activity", activityRef);
            Log.i("Adding High Score", "----------");
            startActivity(scoreIntent);


        }
    }*/
    @Override
    public void onClick(View view) {
        vb.vibrate(10);
      
        Log.i("Test High Score", "-----");
        if(view.getId() == R.id.imageButton){
            Intent scoreIntent = new Intent(getApplicationContext(), ScoreActivity.class);
            scoreIntent.putExtra("score", score);
            scoreIntent.putExtra("calling-Activity", activityRef);
            Log.i("Adding High Score", "----------");
            startActivity(scoreIntent);
        }else if(view.getId()==R.id.imageButton2){
          if(playerGrid == null){
                playerGrid = new GridBoard(this, R.id.playerGrid);
               playerGridShow = true;
            }else {
                if (playerGridShow) {
                    //playerGrid.hideGrid();
                    transition.reverseTransition(750);
                    playerGridShow = false;
                }else if (!playerGridShow) {
                    //playerGrid.showGrid();
                    transition.reverseTransition(750);
                    playerGridShow = true;
                }
            }
        }

 }
}