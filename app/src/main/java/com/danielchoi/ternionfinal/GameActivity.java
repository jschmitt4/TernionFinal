package com.danielchoi.ternionfinal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

        int sizeOfCell = Math.round(ScreenWidth()/(maxN+1));
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
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();


        return dm.widthPixels;
    }
    private void loadResources(){
        drawCell[0] = R.drawable.blank;

    }
}
