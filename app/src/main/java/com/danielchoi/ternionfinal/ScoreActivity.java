package com.danielchoi.ternionfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ScoreActivity extends AppCompatActivity {
    ListView lv;
    ScoreAdapter scoreAdapter;
    private List<Scores> scoresList;
    private final String SCORE_FILENAME = "simonScores.txt";
    private final String DELIMITER = "<õ@Scores@õ>";
    private String name,score, place;
    private final String ACTIVITYKEY = "calling-Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        setTypeface();
        setListView();
        checkCallingActivity();
    }


    //****************************************************Read & Write Files*
    /**
     * This reads the data from files and places them
     * in a List object.
     * Called by setListView()
     * @return the List of scores
     */
    private List<Scores> readData(){

        List<Scores> scoresList = new ArrayList<>();

        try {
            FileInputStream fis = openFileInput(SCORE_FILENAME);
            Scanner scanner = new Scanner(fis).useDelimiter(DELIMITER);
            while(scanner.hasNext()){
                String place = scanner.next().trim();
                String name = scanner.next().trim();
                String score = scanner.next().trim();

                Log.i("ON try: Place: ", place);
                Log.i("ON try: Name: ", name);
                Log.i("ON try: Score: ", score);

                Scores scoreScan = new Scores (place, name, score);
                scoresList.add(scoreScan);
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            //file does not exist
            Log.i("ON IN catch", "======");
        } catch (NoSuchElementException e){
            Log.i("No Such Element", "======");
        }


        return scoresList;

    }

    /**
     * This writes the list of scores to a file.
     * It is written when a new user name is inputted
     * Called by set scores
     */
    private void writeData(){
        try {
            FileOutputStream fos = openFileOutput(SCORE_FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);

            for(int i = 0; i < scoreAdapter.getCount(); i++){
                Scores scores = (Scores) scoreAdapter.getItem(i);
                pw.println(DELIMITER
                        + scores.getPlace() + DELIMITER
                        + scores.getName()+ DELIMITER
                        + scores.getScore());
            }

            pw.close();

        } catch (FileNotFoundException e) {
            Log.e("WRITE_ERR", "Cannot save data: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    //****************************************************Handle new scores*

    /**
     * Creates a view object to reference the Custom alertDialog layout.
     * Accepts the user input by EditText
     * Calls setScore
     * Called by CheckCallingActivity
     */
    private void enterUserName(){
        //TODO Make a default text if left blank

        View view = (LayoutInflater.from(this)).inflate(R.layout.enter_name, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText et= (EditText) view.findViewById(R.id.editText);
        builder.setView(view)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        name = et.getText().toString();
                        Log.i("Name: ",name );
                        setScores();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("Canceled ","");
                    }
                });
        builder.create();
        builder.show();

    }

    /**
     * Gets score from GameActivity Intent
     * Gets name from enterUserName which is a global Variable
     * Calls sortScore to find new place in highscore
     * Gets place from sortScore global variable.
     * Create new Score object and added to the list
     * Calls write Data to save to file
     */
    private void setScores(){
        int temp = getIntent().getIntExtra("score", 0);
        score = Integer.toString(temp);
        place = Integer.toString(1);
        sortScores();
        Scores newScore = new Scores(place,name,score);
        scoresList.add(newScore);
        Log.i("Score: ",""+score);
        writeData();
    }

    /**
     * Compare all score values from List
     * Reposition the list.
     * Number them and reinsert the List to file.
     * Note: place, score, and name are all strings
     * Called my setScores
     */
    private void sortScores(){


    }

    //************************************************Functionaliy & Utility*
    /**
     * This simply sets the type face.
     * Called by onCreate()
     */
    private void setTypeface(){
        ((TextView) findViewById(R.id.highScoreTv)).setTypeface(Typeface.createFromAsset(getAssets(),  "fonts/britanicbold.TTF"));
    }

    /**
     * onBackPressed()
     * So that it returns to home page instead of game
     */
    @Override
    public void onBackPressed() {
        Intent aboutIntent = new Intent(this, HomeActivity.class);
        startActivity(aboutIntent);
    }

    /**
     * This checks if the calling activity is the GameActivity
     * If so it calls the enterUserName()
     * Called by onCreate
     */
    public void checkCallingActivity(){
        int callingActivity = getIntent().getIntExtra(ACTIVITYKEY, 0);

        if(callingActivity == 2000 ){//Home
            enterUserName();
        }

    }

    /**
     * This sets the view on startup
     * It gets the id of the listview
     * Calls readData() which returns a List from file
     * Calls the new adapter which sets the List objects to it's view
     * Sets the adapter to the view
     */
    private void setListView(){
        lv = (ListView) findViewById(R.id.scoreListView);
        scoresList = readData();
        scoreAdapter = new ScoreAdapter(getApplicationContext(), R.layout.score_row, scoresList);
        lv.setAdapter(scoreAdapter);
    }
    //*******************************************************Custom Adapter*
    /**
     * A class that extends ArrayAdapter
     * This takes each List objects(scores) and places them
     * in the correct Textview of the Custom row layout(score_row.xml)
     */
    public class ScoreAdapter extends ArrayAdapter{

        private List<Scores> sL;
        private LayoutInflater inflater;
        private int resource;

        ScoreAdapter(Context context, int resource, List objects){
            super(context, resource, objects);

            this.resource = resource;
            sL = objects;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(resource, null);
                TextView placeTV = (TextView) convertView.findViewById(R.id.placeTv);
                TextView nameTV = (TextView) convertView.findViewById(R.id.nameTv);
                TextView scoreTV = (TextView) convertView.findViewById(R.id.scoreTv);

                placeTV.setText(sL.get(position).getPlace());
                nameTV.setText(sL.get(position).getName());
                scoreTV.setText(sL.get(position).getScore());
            }

            return convertView;
        }
    }

}
