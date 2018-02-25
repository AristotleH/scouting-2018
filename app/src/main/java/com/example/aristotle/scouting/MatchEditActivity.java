package com.example.aristotle.scouting;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MatchEditActivity extends AppCompatActivity {

    private boolean editable;
    private MatchInfo newMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchedit);

        Button create = findViewById(R.id.createButton);
        Bundle extras = getIntent().getExtras();
        if (extras.getBoolean("NEW_MATCH")) {
            setTitle("New Match");
            editable = true;
            setFields(null);
            changeTextFieldStatus(editable);
        } else {
            setTitle("View Match");
            create.setVisibility(View.GONE);
            editable = false;
            setFields(MatchViewActivity.matches.get(extras.getInt("MATCH_POS")));
            changeTextFieldStatus(editable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bundle extras = getIntent().getExtras();
        if (extras.getBoolean("NEW_MATCH") == false) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.matchedit_toggle, menu);
            return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_toggle) {
            if (!editable) {
                setTitle("Edit Match");
                editable = true;
                changeTextFieldStatus(editable);
                MenuItem editFieldsButton = item;
                editFieldsButton.setIcon(getResources().getDrawable(R.drawable.ic_save));
            } else {
                setTitle("View Match");
                editable = false;
                changeTextFieldStatus(editable);
                MenuItem editFieldsButton = item;
                editFieldsButton.setIcon(getResources().getDrawable(R.drawable.ic_edit));
                Bundle extras = getIntent().getExtras();
                int matchPosEdited = extras.getInt("MATCH_POS");

                textFieldToMatch(true, MatchViewActivity.matches.get(matchPosEdited).getDateAndTimeCreated());
                MatchInfo editMatch = newMatch;

                MatchViewActivity.matches.set(matchPosEdited, editMatch);
                Snackbar.make(findViewById(R.id.blueTeam), "Match edited", Snackbar.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId() == android.R.id.home) {
            MatchViewActivity.updateMatchView(true);
            MatchEditActivity.this.finish();
        }
        return true;
    }

    public void createMatch(View v) {
        if (textFieldToMatch(false, null)) {
            MatchViewActivity.matches.add(newMatch);
            MatchViewActivity.updateMatchView(true);
            MatchEditActivity.this.finish();
        }
        else {
            Snackbar.make(findViewById(R.id.blueTeam), "Fill in all values", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void changeTextFieldStatus(boolean editable) {
        EditText[] allFields = {findViewById(R.id.blueTeam), findViewById(R.id.redTeam),
                findViewById(R.id.cubeBlueNearest), findViewById(R.id.cubeBlueMid),
                findViewById(R.id.cubeBlueFarthest), findViewById(R.id.cubeRedNearest),
                findViewById(R.id.cubeRedMid), findViewById(R.id.cubeRedFarthest)};
        for (EditText et : allFields) {
            et.setEnabled(editable);
        }
    }

    private boolean textFieldToMatch(boolean keepTime, String oldTime) {
        try {
            int[] cubeTotals = new int[6];
            EditText[] cubeTotalsFields = {findViewById(R.id.cubeBlueNearest), findViewById(R.id.cubeBlueMid),
                    findViewById(R.id.cubeBlueFarthest), findViewById(R.id.cubeRedNearest),
                    findViewById(R.id.cubeRedMid), findViewById(R.id.cubeRedFarthest)};
            for (int i = 0; i < 6; i++) {
                EditText cubeData = cubeTotalsFields[i];
                cubeTotals[i] = Integer.parseInt(cubeData.getText().toString());
            }
            EditText teamData = findViewById(R.id.blueTeam);
            int bNumber = Integer.parseInt(teamData.getText().toString());
            teamData = findViewById(R.id.redTeam);
            int rNumber = Integer.parseInt(teamData.getText().toString());
            if (keepTime) {
                newMatch = new MatchInfo(bNumber, rNumber, cubeTotals, oldTime);
                return true;
            } else {
                newMatch = new MatchInfo(bNumber, rNumber, cubeTotals, null);
                return true;
            }
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean setFields(MatchInfo match) {
        try {
            EditText[] allFields = {findViewById(R.id.blueTeam), findViewById(R.id.redTeam),
                    findViewById(R.id.cubeBlueNearest), findViewById(R.id.cubeBlueMid),
                    findViewById(R.id.cubeBlueFarthest), findViewById(R.id.cubeRedNearest),
                    findViewById(R.id.cubeRedMid), findViewById(R.id.cubeRedFarthest)};
            EditText[] cubeTotalsFields = {findViewById(R.id.cubeBlueNearest), findViewById(R.id.cubeBlueMid),
                    findViewById(R.id.cubeBlueFarthest), findViewById(R.id.cubeRedNearest),
                    findViewById(R.id.cubeRedMid), findViewById(R.id.cubeRedFarthest)};
            if (match == null) {
                for (EditText et : allFields) {
                    if (et == findViewById(R.id.blueTeam) || et == findViewById(R.id.redTeam))
                        et.setText("");
                    else
                        et.setText("0");
                }
            } else {
                EditText et = findViewById(R.id.blueTeam);
                et.setText(Integer.toString(match.getBlueTeamNumber()));
                et = findViewById(R.id.redTeam);
                et.setText(Integer.toString(match.getRedTeamNumber()));
                int[] cT = match.getCubeTotals();
                for (int i = 0; i < 6; i++) {
                    et = cubeTotalsFields[i];
                    et.setText(Integer.toString(cT[i]));
                }
            }
            return true;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }
}
