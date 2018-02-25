package com.example.aristotle.scouting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class MatchEditActivity extends AppCompatActivity {

    private boolean initalCreation = true;
    private boolean editable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchedit);

        Bundle extras = getIntent().getExtras();
        if (extras.getBoolean("NEW_MATCH")) {
            setTitle("New Match");
            editable = true;
            changeTextFieldStatus(editable);
        } else {
            setTitle("View Match");
            editable = false;
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
                MatchInfo editMatch = textFieldToMatch(true, MatchViewActivity.matches.get(matchPosEdited).getDateAndTimeCreated());
                MatchViewActivity.matches.set(matchPosEdited, editMatch);
            }
        }
        else if (item.getItemId() == android.R.id.home) {
            MatchViewActivity.updateMatchView(true);
            MatchEditActivity.this.finish();
        }
        return true;
    }

    public void submitMatch(View v) {
        if (initalCreation)
            createNewMatch(v);
        MatchEditActivity.this.finish();
    }

    private void createNewMatch(View v) {
        MatchViewActivity.matches.add(textFieldToMatch(false, null));
        MatchViewActivity.updateMatchView(true);
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

    private MatchInfo textFieldToMatch(boolean keepTime, String oldTime) {
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
        if (keepTime)
            return new MatchInfo(bNumber, rNumber, cubeTotals, oldTime);
        else
            return new MatchInfo(bNumber, rNumber, cubeTotals, null);
    }
}
