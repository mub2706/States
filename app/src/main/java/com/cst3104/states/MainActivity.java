package com.cst3104.states;

/**
 * Full Name: Mubarak Hassan
 * Student ID: hass0469
 * Course: CST3104
 * Term:  Fall 2023
 * Assignment: Team Project
 * Date : December 3rd
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int counter = 0;
    private TextView counterTextView;
    private ImageView flagImageView;
    private static final String sFileName = "usa.json";
    private ArrayAdapter<State> mAdapter;
    private ListView mlistView;
    private ArrayList<State> mStatesList;
    private ImageView mFlagImageView;
    private State mCurrentState;
    private boolean gameActive = true;
    private int correctGuesses = 0;
    private int incorrectGuesses = 0;

    // Handle clicks on the toolbar menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_restart) {
            showRestartConfirmationDialog();
            return true;
        } else if (item.getItemId() == R.id.menu_wikipedia) {
            openWikipedia();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // Create options menu (toolbar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("Toolbar", "onCreateOptionsMenu called");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void showRestartConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restart Game");
        builder.setMessage("Are you sure you want to restart the game?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            restartGame();
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStatesList = State.readData(this, sFileName);

        // Limit the list to the first 8 items
        if (mStatesList.size() > 8) {
            mStatesList = new ArrayList<>(mStatesList.subList(0, 8));
        }

        counterTextView = findViewById(R.id.counterTextView);

        mlistView = findViewById(R.id.listView);

        mFlagImageView = findViewById(R.id.flagImageView);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mStatesList);
        mlistView.setAdapter(mAdapter);

        showRandomFlag();

        // Set up item click listener for ListView
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                State selectedState = mAdapter.getItem(position);
                handleGuess(selectedState);
            }
        });
    }

    private void showRandomFlag() {
        // Get a random state from the list
        mCurrentState = getRandomState();
        // Display the flag for the selected state
        mCurrentState.flagInImageView(mFlagImageView);
        // Update the choices in the ListView
        List<State> shuffledStates = State.getShuffledStatesList(mStatesList, mCurrentState);
        mAdapter.clear();
        mAdapter.addAll(shuffledStates.subList(0, Math.min(shuffledStates.size(), 8)));
        mAdapter.notifyDataSetChanged();
    }

    private State getRandomState() {
        Random random = new Random();
        int randomIndex = random.nextInt(mStatesList.size());
        return mStatesList.get(randomIndex);
    }

    // Handle user's guess
    private void handleGuess(State selectedState) {

        // Compare the selected state with the correct answer (You might want to randomize the order)
        if  (selectedState.getName().equalsIgnoreCase(mCurrentState.getName())) {
            Toast.makeText(this, "Correct Guess!", Toast.LENGTH_SHORT).show();
            counter--;
            counterTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            showRandomFlag();
        } else {
            // Incorrect guess
            Toast.makeText(this, "Incorrect Guess!", Toast.LENGTH_SHORT).show();
            counter++;
            counterTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        counterTextView.setText(" " + counter);
        gameActive = false;

        // Update the counter display or any other UI elements you want
        List<State> shuffledStates = State.getShuffledStatesList(mStatesList, mCurrentState);
        mAdapter.addAll(shuffledStates);
        mAdapter.notifyDataSetChanged();

        updateListView();
    }

    private void updateListView() {
        List<State> shuffledStates = State.getShuffledStatesList(mStatesList, mCurrentState);
        mAdapter.clear();
        mAdapter.addAll(shuffledStates.subList(0, Math.min(shuffledStates.size(), 8)));
        mAdapter.notifyDataSetChanged();
    }

    // Get a random state for the user to guess
    private State getCorrectAnswer() {
        // Implement logic to get a random state
        // For simplicity, let's assume mStatesList is not empty
        int randomIndex = (int) (Math.random() * mStatesList.size());
        return mStatesList.get(randomIndex);
    }

    // Restart the game
    private void restartGame() {
        // Generate a new set of states
        List<State> allStates = State.readData(this, sFileName);
        List<State> shuffledStates = State.getShuffledStatesList(allStates, mCurrentState);

        // Select a random position for the correct answer
        int correctAnswerPosition = new Random().nextInt(shuffledStates.size());
        State correctAnswer = shuffledStates.get(correctAnswerPosition);

        // Update the UI with the new round information
        updateUIWithNewRound(correctAnswer, shuffledStates);

        counter = 0;
        counterTextView.setText(" " + counter);
        counterTextView.setTextColor(getResources().getColor(android.R.color.primary_text_light));

        updateListView();
    }

    private void updateUIWithNewRound(State correctAnswer, List<State> choices) {
        // Update your UI elements, set the flag, update the ListView, etc.
        // For example, if you have a method to set the flag in an ImageView:
        correctAnswer.flagInImageView(mFlagImageView);

        // If you have a ListView, update its adapter with the choices
        mAdapter.clear();
        mAdapter.addAll(choices);
        mAdapter.notifyDataSetChanged();
    }

    // Open Wikipedia page for the current correct answer
    private void openWikipedia() {
        State selectedState = getCorrectAnswer(); // Replace with the actual method to get the correct state

        if (selectedState != null && selectedState.getWikiUrl() != null) {
            // Create an Intent with the Wikipedia URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedState.getWikiUrl()));

            // Check if there's a browser available to handle the Intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Handle the case where there's no browser available
                Toast.makeText(this, "No web browser found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where the Wikipedia URL is not available
            Toast.makeText(this, "Wikipedia URL not available", Toast.LENGTH_SHORT).show();
        }
    }
}