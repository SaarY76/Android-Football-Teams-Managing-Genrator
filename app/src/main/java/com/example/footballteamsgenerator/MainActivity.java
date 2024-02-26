package com.example.footballteamsgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    public static List<Player> allPlayers;
    private LinearLayout thisLinearLayout;
    private TextView textViewNumberOfPlayers;
    private int currentNumberOfPlayers;
    private String textNumberOfPlayers = "Number of Players : ";
    private Map<Integer, Integer> teamDistributionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commonSetup();

        thisLinearLayout = findViewById(R.id.thisLinearLayout);

        teamDistributionMap = new HashMap<>();
        currentNumberOfPlayers = 0;
        textViewNumberOfPlayers = findViewById(R.id.textViewNumberOfPlayers);
        textViewNumberOfPlayers.setText(textNumberOfPlayers + currentNumberOfPlayers);

        loadPlayersFromPreferences();
    }

    private void commonSetup()
    {
        // setting the Activity screen to not turn to wide screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // setting the Activity to not have an action bar
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // Log statement to debug onStop calls
        Log.d("Debug", "onStop called");
        fillPlayersFromViews ();
        savePlayersToPreferences(this, allPlayers);
    }

    public static void savePlayersToPreferences(Context context, List <Player> allPlayers)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(allPlayers);

        editor.putString("players", json);
        editor.apply();
    }

    private void loadPlayersFromPreferences()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("players", null);

        if (json != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Player>>() {}.getType();
            allPlayers = gson.fromJson(json, type);

            recreatePlayerViews();
            currentNumberOfPlayers = allPlayers.size();
            textViewNumberOfPlayers.setText(textNumberOfPlayers + currentNumberOfPlayers);

            if (allPlayers.isEmpty())
            {
                allPlayers = new ArrayList<>();
                addLinearLayoutForAddingPlayer();
            }
        }
        else
        {
            allPlayers = new ArrayList<>();
            addLinearLayoutForAddingPlayer();
        }
    }

    private void recreatePlayerViews()
    {
        thisLinearLayout.removeAllViews();

        for (int i = 0; i < allPlayers.size(); i++)
        {
            Player player = allPlayers.get(i);
            // Add the LinearLayout for a player
            addLinearLayoutForAddingPlayer(allPlayers.get(i));
        }
    }

    private void addLinearLayoutForAddingPlayer ()
    {
        // Inflate the adding_player_view.xml layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout playerEntryLayout = (LinearLayout) inflater.inflate(R.layout.adding_player_view, thisLinearLayout, false);

        Spinner spinnerPlayerRating = (Spinner) playerEntryLayout.findViewById(R.id.SpinnerPlayerRating);
        EditText editTextPlayerName = (EditText) playerEntryLayout.findViewById(R.id.editTextPlayerName);
        editTextPlayerName.setText("");
        addTextWatcherToEditText(editTextPlayerName);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_layout, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_layout);

        // Apply the adapter to the spinner
        spinnerPlayerRating.setAdapter(adapter);

        // Add the new linear layout to the parent layout
        thisLinearLayout.addView(playerEntryLayout);
    }

    private void addLinearLayoutForAddingPlayer (Player player)
    {
        // Inflate the adding_player_view.xml layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout playerEntryLayout = (LinearLayout) inflater.inflate(R.layout.adding_player_view, thisLinearLayout, false);

        Spinner spinnerPlayerRating = (Spinner) playerEntryLayout.findViewById(R.id.SpinnerPlayerRating);
        EditText editTextPlayerName = (EditText) playerEntryLayout.findViewById(R.id.editTextPlayerName);
        addTextWatcherToEditText(editTextPlayerName);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_layout, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_layout);

        // Apply the adapter to the spinner
        spinnerPlayerRating.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(player.getRating());
        spinnerPlayerRating.setSelection(spinnerPosition);

        editTextPlayerName.setText(player.getName());

        // Add the new linear layout to the parent layout
        thisLinearLayout.addView(playerEntryLayout);
    }

    private void addTextWatcherToEditText(EditText editText)
    {
        TextWatcher textWatcher = new TextWatcher()
        {
            private boolean wasEmpty = true; // Initialize wasEmpty

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No need to implement
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No need to implement
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                boolean isEmpty = s.toString().isEmpty();
                // Checking if the EditText changed from non-empty to empty or vice versa
                if (wasEmpty && !isEmpty)
                {
                    addingPlayerNumberToTextView();
                }
                else if (!wasEmpty && isEmpty)
                {
                    removingPlayerNumberToTextView();
                }
                wasEmpty = isEmpty; // Update the wasEmpty for next calls
            }
        };

        editText.addTextChangedListener(textWatcher);
    }



    private void addingPlayerNumberToTextView ()
    {
        currentNumberOfPlayers += 1;
        textViewNumberOfPlayers.setText(textNumberOfPlayers + currentNumberOfPlayers);
    }

    private void removingPlayerNumberToTextView ()
    {
        if (currentNumberOfPlayers > 0)
        {
            currentNumberOfPlayers -= 1;
            textViewNumberOfPlayers.setText(textNumberOfPlayers + currentNumberOfPlayers);
        }
    }

    private void fillPlayersFromViews ()
    {
        allPlayers.clear();
        for (int i = 0; i<thisLinearLayout.getChildCount(); i++)
        {
            View child = thisLinearLayout.getChildAt(i);
            // Ensuring the child is an instance of LinearLayout and has the expected child views
            if (child instanceof LinearLayout)
            {
                EditText playerNameEditText = (EditText) child.findViewById(R.id.editTextPlayerName);
                String playerName = playerNameEditText.getText().toString();
                if (playerName != null && !playerName.equals(""))
                {
                    Spinner playerRatingSpinner = (Spinner) child.findViewById(R.id.SpinnerPlayerRating);
                    Integer selectedNumber = (Integer)playerRatingSpinner.getSelectedItem();
                    int playerRating = selectedNumber.intValue();

                    Player player = new Player(playerName, playerRating);
                    allPlayers.add(player);
                }
            }
        }
    }

    private boolean isPrime(int number)
    {
        for (int i = 2; i <= Math.sqrt(number); i++)
        {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    private void showTeamOptionsDialog()
    {
        fillPlayersFromViews();
        int numberOfPlayers = allPlayers.size();
        // Clear previous entries in the map
        teamDistributionMap.clear();

        if (numberOfPlayers <= 3)
        {
            Toast.makeText(this, "The current number of players is less then 4.", Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            // Checking if the number is a prime number
            if (isPrime(numberOfPlayers))
            {
                Toast.makeText(this, "The current number of players is a prime number.", Toast.LENGTH_LONG).show();
                return;
            }

            // Calculate team options excluding 1 team
            List<String> teamOptions = new ArrayList<>();
            for (int i = 2; i <= numberOfPlayers / 2; i++)
            {
                if (numberOfPlayers % i == 0)
                {
                    teamOptions.add(i + " teams of " + (numberOfPlayers / i));
                    // Add to the map
                    teamDistributionMap.put(i, numberOfPlayers / i);
                }
            }

            // Convert the list to an array for the AlertDialog
            final String[] optionsArray = teamOptions.toArray(new String[0]);

            // Temporary holder for the currently selected number of teams and players per team
            final int[] selectedTeamDistribution = {0, 0};

            // Create and show the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("The number of players is: " + numberOfPlayers + ", choose the number of teams:");

            builder.setSingleChoiceItems(optionsArray, -1, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // The 'which' parameter is the index of the selected item
                    // We can retrieve the team distribution from the map using the item text
                    String selectedItem = optionsArray[which];
                    String[] parts = selectedItem.split(" teams of ");
                    int teams = Integer.parseInt(parts[0]);
                    int playersPerTeam = teamDistributionMap.get(teams);

                    // Updating the temporary holder with the selected distribution
                    selectedTeamDistribution[0] = teams;// Number of teams
                    selectedTeamDistribution[1] = playersPerTeam;// Players per team
                }
            });

            AlertDialog dialog = builder.create();

            // setting the positive button without a listener to prevent it from dismissing
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", (DialogInterface.OnClickListener) null);

            // Force LTR layout direction
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

            dialog.show();

            // Now that the dialog is shown, get the positive button and set a custom click listener
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    // Use the selectedTeamDistribution here
                    int numberOfTeams = selectedTeamDistribution[0];
                    int numberOfPlayersPerTeam = selectedTeamDistribution[1];

                    // Check if no selection was made
                    if (numberOfTeams == 0 && numberOfPlayersPerTeam == 0)
                    {
                        Toast.makeText(getApplicationContext(), "No selected item", Toast.LENGTH_LONG).show();
                        // Do not dismiss the dialog
                    }
                    else
                    {
                        // Create the intent to start GenerateResultsActivity
                        Intent intent = new Intent(MainActivity.this, GenerateResultsActivity.class);

                        // Put the number of teams and players per team as extras
                        intent.putExtra("EXTRA_NUMBER_OF_TEAMS", numberOfTeams);
                        intent.putExtra("EXTRA_PLAYERS_PER_TEAM", numberOfPlayersPerTeam);

                        // Start the activity
                        startActivity(intent);
                        dialog.dismiss(); // Manually dismiss the dialog now
                    }
                }
            });
        }
    }

    public void addPlayer(View view)
    {
        addLinearLayoutForAddingPlayer();
    }

    public void removePlayer(View view)
    {
        for (int i = thisLinearLayout.getChildCount() - 1; i >= 0; i--)
        {
            View child = thisLinearLayout.getChildAt(i);
            // Ensuring the child is an instance of LinearLayout and has the expected child views
            if (child instanceof LinearLayout)
            {
                CheckBox checkBox = child.findViewById(R.id.checkBox);
                EditText editTextName = child.findViewById(R.id.editTextPlayerName);
                // If the CheckBox is checked, we will remove the corresponding LinearLayout
                if (checkBox != null && checkBox.isChecked())
                {
                    thisLinearLayout.removeViewAt(i);
                    if (editTextName != null && !editTextName.getText().toString().equals(""))
                        removingPlayerNumberToTextView();
                }
            }
        }
    }

    public void generateTeams(View view)
    {
        showTeamOptionsDialog();
    }
}