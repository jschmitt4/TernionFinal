package com.danielchoi.ternionfinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    // Variables
    public Vibrator vb;
    public static final int activityRef = 2000;
    private int score=0, count=0, soundID[];
    private GridBoard playerGrid, enemyGrid;
    private boolean playersTurn;
    private Set<Integer> soundsLoaded;
    private TransitionDrawable transition;
    private enum GAMEPHASE{INTRO_PHASE, SETUP_PHASE, PLAYER_PHASE, ENEMY_PHASE, GAMEOVER_PHASE}
    public GAMEPHASE gamephase;
    View layout, shipName;
    ImageView battleButton, alertView;
    InvasionThread invasion;
    SoundPool soundPool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        loadSounds();

    }
    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        SharedPreferences phaseSP = getSharedPreferences("PHASE",Context.MODE_PRIVATE);
        String gamePhaseString = (phaseSP.getString("PHASE",GAMEPHASE.INTRO_PHASE.name()));

        enterPhase(gamePhaseString);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearData();
        SharedPreferences phaseSP = getSharedPreferences("PHASE",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = phaseSP.edit();
        editor.putString("PHASE",gamephase.name());
        editor.apply();
    }

    private void loadSounds(){

        soundsLoaded = new HashSet<>();
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setUsage(AudioAttributes.USAGE_GAME);

        final SoundPool.Builder spBuilder = new SoundPool.Builder();
        spBuilder.setAudioAttributes(attrBuilder.build());
        spBuilder.setMaxStreams(4);
        soundPool = spBuilder.build();
        soundID = new int[]{soundPool.load(this, R.raw.alarm, 1)};

    }

    private void enterPhase(String gamePhaseString){
        switch(gamePhaseString){
            case "INTRO_PHASE":
                gamephase = GAMEPHASE.INTRO_PHASE;
                defaultGlobalVariables();
                introPhase();
                break;
            case "SETUP_PHASE":
                gamephase = GAMEPHASE.SETUP_PHASE;
                defaultGlobalVariables();
                setUpPhase();
                break;
            case "PLAYER_PHASE":
                gamephase = GAMEPHASE.PLAYER_PHASE;
                break;
            case "ENEMY_PHASE":
                gamephase = GAMEPHASE.ENEMY_PHASE;
                break;
            case "GAMEOVER_PHASE":
                gamephase = GAMEPHASE.GAMEOVER_PHASE;
                break;
            default:
                gamephase = GAMEPHASE.INTRO_PHASE;
                break;

        }

    }

    private void defaultGlobalVariables(){
        alertView = (ImageView) findViewById(R.id.alertView);
        battleButton = (ImageView) findViewById(R.id.battleIB);
        layout = findViewById(R.id.activity_game);
        shipName = findViewById(R.id.textView);

    }

    private void introPhase() {
        gamephase = GAMEPHASE.SETUP_PHASE;
        shipName.setVisibility(View.GONE);
        battleButton.setVisibility(View.GONE);

        Animation an = AnimationUtils.loadAnimation(this, R.anim.blink);
        alertView.startAnimation(an);

        invasion = new InvasionThread(this, Math.round(ScreenHeight()));
        invasion.execute();
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clearData();
                setUpPhase();
                return false;
            }
        });

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    soundsLoaded.add(sampleId);
                    Log.i("SOUND", "Sound loaded = " + sampleId);
                    if (soundsLoaded.contains(soundID[0])) soundPool.play(soundID[0], .1f, .1f, 0, -1, .81f);
                } else {
                    Log.i("SOUND", "Error cannot load sound status = " + status);
                }
            }
        });



    }

    private void clearIntroPhase(){
        layout.setOnTouchListener(null);
        findViewById(R.id.invasion).setVisibility(View.GONE);
        alertView.clearAnimation();
        alertView.setVisibility(View.INVISIBLE);
    }

    private void setUpPhase(){
        gamephase = GAMEPHASE.SETUP_PHASE;
        clearIntroPhase();
        int i = layout.getPaddingTop();
        layout.setPadding(0,i,0,i);
        transition = (TransitionDrawable) findViewById(R.id.activity_game).getBackground();
        transition.reverseTransition(750);
        shipName.setVisibility(View.VISIBLE);//Temp reference
        battleButton.setVisibility(View.VISIBLE);
        findViewById(R.id.battleIB).setOnClickListener(this);
        findViewById(R.id.testButton).setOnClickListener(this);
        playerGrid = new GridBoard(this, R.id.playerGrid, true);
        playersTurn = true;
    }


    @Override
    public void onClick(View view) {
        vb.vibrate(10);


        if(view.getId() == R.id.testButton){
            gamephase = GAMEPHASE.INTRO_PHASE; //Temp to reset game. It should be in the GAMEOVER_PHASE
            if(playerGrid !=null) {
                if(playerGrid.getHit()) score = score +2000;
                    Log.i("Test High Score", "-----");
                    Intent scoreIntent = new Intent(getApplicationContext(), ScoreActivity.class);
                    scoreIntent.putExtra("score", score);
                    scoreIntent.putExtra("calling-Activity", activityRef);
                    Log.i("Adding High Score", "----------");
                    startActivity(scoreIntent);
            }
        }else if(view.getId()== R.id.battleIB){
            gamephase = GAMEPHASE.PLAYER_PHASE;
            transition.reverseTransition(750);
            view.setVisibility(View.INVISIBLE);
            shipName.setVisibility(View.INVISIBLE);
            playerGrid.hideGrid();
            enemyGrid = new GridBoard(this, R.id.enemyGrid, false);
            playersTurn = false;

            //Now we have to set the a random location for the enemy ships.
            //And hide it as well.

        }

    }

    private void clearData(){

        if (invasion != null) {
            invasion.cancel(true);
            invasion = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            soundsLoaded.clear();
        }
    }

    private float ScreenHeight() {
        RelativeLayout linBoardGame = (RelativeLayout) findViewById(R.id.activity_game);
        Resources r = linBoardGame.getResources();
        DisplayMetrics d = r.getDisplayMetrics();
        return d.heightPixels;
    }
}