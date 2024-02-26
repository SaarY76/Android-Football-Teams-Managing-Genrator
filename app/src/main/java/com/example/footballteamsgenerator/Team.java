package com.example.footballteamsgenerator;

import java.util.ArrayList;
import java.util.List;

public class Team
{
    private List<Player> players;
    private int totalRating;

    public Team ()
    {
        this.players = new ArrayList<>();
        this.totalRating = 0;
    }

    public void addPlayer(Player player)
    {
        players.add(player);
        totalRating += player.getRating();
    }

    public void removePlayer(Player player)
    {
        players.remove(player);
        totalRating -= player.getRating();
    }

    public int getTotalRating()
    {
        return totalRating;
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<Player> players)
    {
        this.players = players;
    }

    @Override
    public String toString()
    {
        return "Team{" +
                "players=" + players +
                ", totalRating=" + totalRating +
                "}\n";
    }
}
