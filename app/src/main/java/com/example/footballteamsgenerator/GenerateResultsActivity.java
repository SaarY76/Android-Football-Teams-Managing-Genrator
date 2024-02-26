package com.example.footballteamsgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GenerateResultsActivity extends AppCompatActivity
{
    private int numberOfTeams;
    private int numberOfPlayersPerTeam;
    private LinearLayout thisLinearLayout;
    private List<Team> teams;
    public static List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_results);
        commonSetup();

        thisLinearLayout = findViewById(R.id.thisLinearLayout2);

        // Retrieve the number of teams and players per team from the intent
        Intent intent = getIntent();
        numberOfTeams = intent.getIntExtra("EXTRA_NUMBER_OF_TEAMS", -1); // Default to -1 if not found
        numberOfPlayersPerTeam = intent.getIntExtra("EXTRA_PLAYERS_PER_TEAM", -1); // Default to -1 if not found

        fillTeams();
        displayTeams();
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
        MainActivity.savePlayersToPreferences(this, MainActivity.allPlayers);
    }

    // the function returns true if there is more then one player with the current highest rating
    private static boolean isThereAreCurrentBestPlayersWithTheSameRating (List<Player> allPlayers)
    {
        if (allPlayers.size() < 2)
        {
            return false;
        }
        int currentHighestRating = allPlayers.get(0).getRating();
        if (allPlayers.get(1).getRating() == currentHighestRating)
            return true;
        else
            return false;
    }

    // the function returns the current highest player or randomize if there are more then one with the same rating
    private static Player randomizePlayerWithSameRating (List<Player> allPlayers)
    {
        if (allPlayers.isEmpty())
            return null;
        if (allPlayers.size() == 1)
        {
            Player player = allPlayers.get(0);
            allPlayers.remove(0);
            return player;
        }
        int currentBestPlayerRating = allPlayers.get(0).getRating();
        List<Player> playersWithCurrentBestRating = new ArrayList<>();
        for (int i = 0; i < allPlayers.size(); i++)
        {
            if (allPlayers.get(i).getRating() == currentBestPlayerRating)
                playersWithCurrentBestRating.add(allPlayers.get(i));
            else
                break;
        }
        Random rand = new Random();
        int randomInt = rand.nextInt(playersWithCurrentBestRating.size());
        Player player = playersWithCurrentBestRating.get(randomInt);
        allPlayers.remove(player);
        return player;
    }

    private Team findTeamWithLowestRating(List<Team> teams)
    {
        // Initialize with the first team
        Team lowestRatedTeam = teams.get(0);
        for (Team team : teams)
        {
            if (team.getTotalRating() < lowestRatedTeam.getTotalRating())
            {
                lowestRatedTeam = team;
            }
        }
        return lowestRatedTeam;
    }

    private void balanceTeams()
    {
        boolean improved;
        do
        {
            improved = false;
            for (int i = 0; i < teams.size(); i++)
            {
                for (int j = i + 1; j < teams.size(); j++)
                {
                    Team team1 = teams.get(i);
                    Team team2 = teams.get(j);
                    for (Player player1 : new ArrayList<>(team1.getPlayers()))
                    {
                        for (Player player2 : new ArrayList<>(team2.getPlayers()))
                        {
                            // Calculating the new ratings for team1 if player1 is replaced by player2
                            int team1Rating = team1.getTotalRating() - player1.getRating() + player2.getRating();

                            // Calculating the new ratings for team2 if player2 is replaced by player1
                            int team2Rating = team2.getTotalRating() - player2.getRating() + player1.getRating();

                            // Check if swapping players will result in a closer balance between the two teams
                            if (Math.abs(team1Rating - team2Rating) < Math.abs(team1.getTotalRating() - team2.getTotalRating()))
                            {
                                // If yes, performing the swap:

                                // Removing player1 from team1 and adding player2 to team1
                                team1.removePlayer(player1);
                                team1.addPlayer(player2);

                                // Removing player2 from team2 and adding player1 to team2
                                team2.removePlayer(player2);
                                team2.addPlayer(player1);

                                // Mark that an improvement has been made in this iteration
                                improved = true;
                            }
                        }
                    }
                }
            }
        } while (improved);
    }

//    private void fillTeams ()
//    {
//        players = new ArrayList<>();
//        List<Integer> indexes = new ArrayList<>();
//        for (int i=0; i<MainActivity.allPlayers.size(); i++)
//        {
//            Random random = new Random();
//            int index = random.nextInt(MainActivity.allPlayers.size());
//            while (indexes.contains(index))
//                index = random.nextInt(MainActivity.allPlayers.size());
//            players.add(new Player(MainActivity.allPlayers.get(index).getName(),MainActivity.allPlayers.get(index).getRating()));
//            indexes.add(index);
//        }
//
//        // Sort players by rating
//        Collections.sort(players, (p1, p2) -> p2.getRating() - p1.getRating());
//
//        teams = new ArrayList<>();
//        List <Team> teamsTemp = new ArrayList<>();
//        for (int i=0; i<numberOfTeams; i++)
//            teams.add(new Team());
//
//        while (!players.isEmpty())
//        {
//            for (Team team : teams)
//            {
//                team.addPlayer(players.remove(0));
//            }
//        }
//
//        balanceTeams();
//    }

    private void fillTeams ()
    {
        players = new ArrayList<>();

        List<Integer> indexes = new ArrayList<>();
        for (int i=0; i<MainActivity.allPlayers.size(); i++)
        {
            Random random = new Random();
            int index = random.nextInt(MainActivity.allPlayers.size());
            while (indexes.contains(index))
                index = random.nextInt(MainActivity.allPlayers.size());
            players.add(new Player(MainActivity.allPlayers.get(index).getName(),MainActivity.allPlayers.get(index).getRating()));
            indexes.add(index);
        }

        // Sort players by rating
        Collections.sort(players, (p1, p2) -> p2.getRating() - p1.getRating());

        teams = new ArrayList<>();
        List <Team> teamsTemp = new ArrayList<>();
        for (int i=0; i<numberOfTeams; i++)
            teamsTemp.add(new Team());

        while (!players.isEmpty())
        {
            Player playerToAdd;

            Team teamToAddTo = findTeamWithLowestRating(teamsTemp);

            if (isThereAreCurrentBestPlayersWithTheSameRating(players))
                playerToAdd = randomizePlayerWithSameRating(players);
            else
            {
                playerToAdd = players.remove(0);
            }
            teamToAddTo.addPlayer(playerToAdd);

            if (teamToAddTo.getPlayers().size() == numberOfPlayersPerTeam)
            {
                teams.add(teamToAddTo);
                teamsTemp.remove(teamToAddTo);
            }
        }
        Collections.reverse(teams);
        balanceTeams();

        // sorting the teams
        for (Team team : teams)
        {
            // Making a modifiable copy of the players list
            List<Player> playersCopy = new ArrayList<>(team.getPlayers());

            // Sorting the copy
            Collections.sort(playersCopy, (p1, p2) -> p2.getRating() - p1.getRating());

            // Updating the team's players list with the sorted list
            team.setPlayers(playersCopy);
        }
    }

    private void displayTeams()
    {
        // Clear any existing views if necessary
        thisLinearLayout.removeAllViews();
        for (int i = 0; i < teams.size(); i++)
        {
            Team team = teams.get(i);

            // Create a TextView for the team title
            TextView teamTitle = new TextView(this);
            teamTitle.setText("Team " + (i + 1) + " (Rating "+ team.getTotalRating() +"):");
            teamTitle.setTypeface(null, Typeface.BOLD);
            teamTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25); // Set the text size to 18sp
            teamTitle.setTextColor(Color.BLACK);
            teamTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Define the layout parameters for the player item
            LinearLayout.LayoutParams teamLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Add bottom margin to each player item
            int bottomMarginInPx = (int) (2 * getResources().getDisplayMetrics().density);
            teamLayoutParams.bottomMargin = bottomMarginInPx;

            // Set the layout parameters to the player item layout
            teamTitle.setLayoutParams(teamLayoutParams);

            // Add the team title TextView to the LinearLayout
            thisLinearLayout.addView(teamTitle);

            LayoutInflater inflater = LayoutInflater.from(this);
            // Iterate over the players and inflate the custom layout for each player
            for (Player player : team.getPlayers())
            {
                // Inflate the custom layout for the player
                View playerItemLayout = inflater.inflate(R.layout.players_and_ratings_layout, thisLinearLayout, false);

                // Set the player's name and rating in the inflated layout
                TextView playerNameTextView = playerItemLayout.findViewById(R.id.playerName);
                TextView playerRatingTextView = playerItemLayout.findViewById(R.id.playerRating);
                playerNameTextView.setText(player.getName());
                playerRatingTextView.setText(String.valueOf(player.getRating()));

                // Define the layout parameters for the player item
                LinearLayout.LayoutParams playerLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                // Add bottom margin to each player item
                bottomMarginInPx = (int) (6 * getResources().getDisplayMetrics().density);
                playerLayoutParams.bottomMargin = bottomMarginInPx;

                // Set the layout parameters to the player item layout
                playerItemLayout.setLayoutParams(playerLayoutParams);

                // Add the player layout to the LinearLayout
                thisLinearLayout.addView(playerItemLayout);
            }
            // Add a margin after each team if needed
            if (i < teams.size() - 1)
            { // Add margin for all except the last team
                View separator = new View(this);
                LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (16 * getResources().getDisplayMetrics().density)); // 16dp in px
                separator.setLayoutParams(separatorParams);
                thisLinearLayout.addView(separator);
            }
        }
    }

    public void generateTeams(View view)
    {
        fillTeams();
        displayTeams();
    }
}