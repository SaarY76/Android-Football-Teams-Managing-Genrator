package com.example.footballteamsgenerator;

public class Player
{
    private String name;
    private int rating;// from 1 to 5 when 5 is the best and 1 is the worst

    public Player(String name, int rating)
    {
        this.name = name;
        this.rating = rating;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getRating()
    {
        return rating;
    }

    public void setRating(int rating)
    {
        this.rating = rating;
    }

    @Override
    public String toString()
    {
        return "Player [Name: " + name + ", Rating: " + rating + "]";
    }
}
