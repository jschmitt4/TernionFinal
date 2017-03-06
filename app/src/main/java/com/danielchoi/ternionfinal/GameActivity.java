package com.danielchoi.ternionfinal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GameActivity extends AppCompatActivity {

    final static int maxN = 10;
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    private Context context;
    private int[] drawCell = new int[1];// hit, player, enemy, background, miss

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        context = getApplicationContext();
        loadResources();
        designBoardGame();
    }

    private  void designBoardGame(){
        //layoutparams to optimize size of cell

        LinearLayout linBoardGame = (LinearLayout) findViewById(R.id.boardLayout);

        int sizeOfCell = Math.round(ScreenWidth()/(maxN+(1)));
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeOfCell*maxN, sizeOfCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeOfCell, sizeOfCell);

        for(int x = 0; x < maxN; x++){
            LinearLayout linRow = new LinearLayout(this);
            for(int y = 0; y < maxN; y++){

                ivCell[x][y] = new ImageView(this);
                ivCell[x][y].setBackgroundResource(R.drawable.grid);
                linRow.addView(ivCell[x][y], lpCell);
            }
            linBoardGame.addView(linRow, lpRow);
        }

    }
    private float ScreenWidth(){
        RelativeLayout linBoardGame = (RelativeLayout) findViewById(R.id.activity_game);
        Resources r = linBoardGame.getResources();
        DisplayMetrics d = r.getDisplayMetrics();

        return d.widthPixels;
    }
    private void loadResources(){
        drawCell[0] = R.drawable.blank;

    }
}
