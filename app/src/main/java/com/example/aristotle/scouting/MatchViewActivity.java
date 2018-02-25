package com.example.aristotle.scouting;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.UploadErrorException;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MatchViewActivity extends AppCompatActivity {

    public static ArrayList<String> matchViewList;
    private static ArrayAdapter<String> matchListAdapter;
    private ListView matchesView;
    public static ArrayList<MatchInfo> matches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchview);

        matches = new ArrayList<>();

        setTitle("Match List");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        GetDropboxFiles task = new GetDropboxFiles(getApplicationContext());
        task.execute();

        /*
        need to find a better way of waiting until the download AsyncTask is done;
        application is using a preexisting matches.txt instead of using a newly downloaded version
        because the application reads from matches.txt before the new one has finished downloading
        */
        try {
            task.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "matches.txt");//metadata.getName());

        ArrayList<String> textToApp = new ArrayList<>();

        try {
            //@SuppressWarnings("resource")
            System.out.println(file);
            Scanner readSaved = new Scanner(file);
            System.out.println(readSaved.hasNextLine());
            while (readSaved.hasNextLine()) {
                System.out.println("we go");
                textToApp.add(readSaved.nextLine());
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }


        //any MatchInfo in dropbox's matches.txt that is outdated/missing values will not be loaded
        //and will be thrown away when matchview is updated
        System.out.println(textToApp.size());

        for (int i = 0; i < textToApp.size(); i++) {
            System.out.println(stringToMatch(textToApp.get(i)));
            if (stringToMatch(textToApp.get(i)) != null) {
                matches.add(stringToMatch(textToApp.get(i)));
            }
        }

        matchesView = findViewById(R.id.listMatches);
        matchViewList = new ArrayList<String>();
        matchListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matchViewList);
        matchesView.setAdapter(matchListAdapter);

        updateMatchView(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.newMatch);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newMatch = true;
                //int[] teamNumbers = {0};
                Intent intent = new Intent(getApplicationContext(), MatchEditActivity.class);
                intent.putExtra("NEW_MATCH", newMatch);
                //intent.putExtra("TEAM_NUMBERS", teamNumbers);
                startActivity(intent);
            }
        });

        matchesView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> m, View v, int pos, long i) {
                boolean newMatch = false;
                int[] teamNumbers = {matches.get(pos).getBlueTeamNumber(), matches.get(pos).getRedTeamNumber()};
                String timeAndDate = matches.get(pos).getDateAndTimeCreated();
                Intent intent = new Intent(getApplicationContext(), MatchEditActivity.class);
                int matchClicked = pos;
                intent.putExtra("NEW_MATCH", newMatch);
                intent.putExtra("TEAM_NUMBERS", teamNumbers);
                intent.putExtra("TIME_DATE", timeAndDate);
                intent.putExtra("MATCH_POS", matchClicked);
                startActivity(intent);
            }
        });

        matchesView.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> m, View v, int pos, long i) {
                matches.remove(pos);
                Snackbar.make(v, "Match deleted", Snackbar.LENGTH_SHORT).show();
                updateMatchView(true);
                return true;
            }
        });

    }

    public void addNewMatch (String s) {
        matchListAdapter.add(s);
        try {
            saveData();
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }


    public static void updateMatchView (boolean saveAndUpload) {
        matchListAdapter.clear();
        for (int i = 0; i < matches.size(); i++) {
            matchListAdapter.add(matches.get(i).getBlueTeamNumber() + " vs. " +  matches.get(i).getRedTeamNumber() + " at " + matches.get(i).getDateAndTimeCreated());
        }
        if (saveAndUpload) {
            try {
                saveData();
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    private void readMatchData() {
        try {
            Scanner readSaved = new Scanner(new File("matches.txt"));
            while (readSaved.hasNextLine()) {
                matchListAdapter.add(readSaved.nextLine());
            }
        } catch (IOException ioe) {
            // new file created at close of program
        }
    }
    */

    public static void saveData() throws UploadErrorException, DbxException {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "matches.txt");

        try {
            PrintWriter save = new PrintWriter(file);
            for (int i = 0; i < matches.size(); i++) {
                save.println(matchToString(matches.get(i)));
            }
            save.close();
            UploadDropboxFiles task = new UploadDropboxFiles("matches.txt", "matches.txt");//getApplicationContext());
            task.execute();
        } catch (IOException e) {
            // do something
        }
    }

    public static MatchInfo stringToMatch(String s) {
        try {
            System.out.println("go please");
            String data = s;
            System.out.println(s);
            int[] cubeTotals = new int[6];
            System.out.println(data.substring(0, data.indexOf('|')));
            int bNumber = Integer.parseInt(data.substring(0, data.indexOf('|')));
            System.out.println(bNumber + " blue");
            data = data.substring(data.indexOf('|')+1);
            int rNumber = Integer.parseInt(data.substring(0, data.indexOf('|')));
            System.out.println(rNumber);
            data = data.substring(data.indexOf('|')+1);
            String timeDateCreated = data.substring(0, data.indexOf('|'));
            data = data.substring(data.indexOf('|')+1);
            for (int i = 0; i < 5; i++) {
                cubeTotals[i] = Integer.parseInt(data.substring(0, data.indexOf('|')));
                data = data.substring(data.indexOf('|') + 1);
            }
            cubeTotals[5] = Integer.parseInt(data); //the last cubeTotal ends at the end of the string, so instead of
                                                    //checking for a |, we just use the rest of "data" that remains
             return new MatchInfo(bNumber, rNumber, cubeTotals, timeDateCreated);
        }
        catch (IndexOutOfBoundsException | NullPointerException ex) {
            return null;
        }

    }

    public static String matchToString(MatchInfo m) {
        MatchInfo match = m;
        String bNumber = Integer.toString(match.getBlueTeamNumber()) + "|";
        String rNumber = Integer.toString(match.getRedTeamNumber()) + "|";
        String timeAndDateCreated = match.getDateAndTimeCreated() + "|";
        String cubeTotals = "";
        int[] cubeArray = match.getCubeTotals();
        for (int i = 0; i < 6; i++) {
            cubeTotals = cubeTotals + cubeArray[i];
            if (i < 5)
                cubeTotals = cubeTotals + "|";
        }
        return bNumber + rNumber + timeAndDateCreated + cubeTotals;
    }

}
