package com.danielchoi.ternionfinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

    }
    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        SharedPreferences phaseSP = getSharedPreferences("PHASE",Context.MODE_PRIVATE);
        String gamePhaseString = (phaseSP.getString("PHASE",GAMEPHASE.INTRO_PHASE.name()));

        loadSounds();
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

    /**
     * This loads the all the sounds that  is going to be used for this game
     */
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

    /**
     * This method will be used to load up previous gamestates
     */
    private void loadSaveState(){


    }

    /**
     * This directs the game to the proper phase when returning to the game
     */
    private void enterPhase(String gamePhaseString){
        defaultGlobalVariables();
        loadSaveState();

        switch(gamePhaseString){
            case "INTRO_PHASE":
                gamephase = GAMEPHASE.INTRO_PHASE;
                introPhase();
                break;
            case "SETUP_PHASE":
                gamephase = GAMEPHASE.SETUP_PHASE;
                clearIntroPhase();
                setUpPhase();
                break;
            case "PLAYER_PHASE":
                gamephase = GAMEPHASE.PLAYER_PHASE;
                //Bad code below, but without it, it is causing issues
                //Need to go through and clear and set phases better
                //It gives a grid alignment issue
                introPhase();
                clearIntroPhase();
                setUpPhase();
                clearSetUpPhase();
                playerPhase();
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


    /**
     * Loads the default variables need for each phase
     */
    private void defaultGlobalVariables(){
        alertView = (ImageView) findViewById(R.id.alertView);
        battleButton = (ImageView) findViewById(R.id.battleIB);
        layout = findViewById(R.id.activity_game);
        shipName = findViewById(R.id.textView);
        findViewById(R.id.testButton).setOnClickListener(this); // going to be the menu button

    }

    /**
     * This is the intro animiation phase
     * Exit this phase by onTouch
     */
    private void introPhase() {
        gamephase = GAMEPHASE.SETUP_PHASE;
        shipName.setVisibility(View.INVISIBLE);
        battleButton.setVisibility(View.INVISIBLE);

        Animation an = AnimationUtils.loadAnimation(this, R.anim.blink);
        alertView.startAnimation(an);
        invasion = new InvasionThread(this, Math.round(ScreenHeight()));
        invasion.execute();
        playSounds(0);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clearIntroPhase();
                setUpPhase();
                return false;
            }
        });

    }

    /**
     * Calls clear data
     * Hides the invasion
     * Remove the touchlistener to the layout
     * Stops animation.
     */
    private void clearIntroPhase(){
        clearData();
        if(layout != null)layout.setOnTouchListener(null);
        if(alertView != null) {
            alertView.clearAnimation();
            alertView.setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.invasion).setVisibility(View.INVISIBLE);
    }


    /**
     * Setups the correcting padding.
     * Transitions the background
     * Shows battle button and shipname
     * Creates player grid
     */
    private void setUpPhase(){
        gamephase = GAMEPHASE.SETUP_PHASE;
        int i = layout.getPaddingTop();
        layout.setPadding(0,i,0,i);
        transition = (TransitionDrawable) findViewById(R.id.activity_game).getBackground();
        transition.reverseTransition(750);
        shipName.setVisibility(View.VISIBLE);//Temp reference
        battleButton.setVisibility(View.VISIBLE);
        findViewById(R.id.battleIB).setOnClickListener(this);
        if(playerGrid == null)playerGrid = new GridBoard(this, R.id.playerGrid, true);

    }

    /**
     * Clears up setup phase to prep for Player phase
     */
    private void clearSetUpPhase(){
        battleButton.setVisibility(View.INVISIBLE);
        shipName.setVisibility(View.INVISIBLE);
        if(playerGrid != null)playerGrid.hideGrid();

    }


    /**
     * This is the player where we would pick the enemies coordinates to attack
     */
    private void playerPhase(){
        gamephase = GAMEPHASE.PLAYER_PHASE;
        playersTurn = true;
        if(enemyGrid == null)enemyGrid = new GridBoard(this, R.id.enemyGrid, false);
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
            transition.reverseTransition(750);
            clearSetUpPhase();
            playerPhase();


        }

    }

    /**
     * This takes in the index valie of the soundID to play
     * @param i
     */

    private void playSounds(int i){
        final int index = i;
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    soundsLoaded.add(sampleId);
                    Log.i("SOUND", "Sound loaded = " + sampleId);
                    if (soundsLoaded.contains(soundID[index])) soundPool.play(soundID[index], .1f, .1f, 0, -1, .81f);
                } else {
                    Log.i("SOUND", "Error cannot load sound status = " + status);
                }
            }
        });

    }

    /**
     * clears all threads and sounds
     */
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

    /**
     * returns screen height for padding and alignment
     * @return
     */
    private float ScreenHeight() {
        RelativeLayout linBoardGame = (RelativeLayout) findViewById(R.id.activity_game);
        Resources r = linBoardGame.getResources();
        DisplayMetrics d = r.getDisplayMetrics();
        return d.heightPixels;
    }
}