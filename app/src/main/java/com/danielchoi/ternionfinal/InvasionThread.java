package com.danielchoi.ternionfinal;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by Daniel on 4/24/2017.
 */

public class InvasionThread extends AsyncTask<Void, Integer, Void>{

        private ImageView invasion;
        private float stopPoint;
        private int screenHeight;
        private Context context;

        InvasionThread(Context context, int screenHeight) {
            this.context = context;
            this.screenHeight = screenHeight;
            stopPoint = this.screenHeight - (this.screenHeight/3);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            invasion = (ImageView) ((Activity)context).findViewById(R.id.invasion);

        }
        @Override
        protected Void doInBackground(Void... voids) {

            try{
                int x = 0;
                while(x < stopPoint) {
                    x = x+10;
                    publishProgress(x);
                    Thread.sleep(50);
                }
            }catch(InterruptedException e){

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            invasion.setTranslationY(screenHeight-values[0]);
        }



}
