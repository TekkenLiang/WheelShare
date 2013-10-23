package com.example.wheelshare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Ride
{

    protected String startCity;
    protected String destCity;
    protected String owner;
    protected String date;
    protected String time;
    protected String id;
    protected int price;
    protected int seats;
    protected Participant[] participants;

    protected Ride(JSONObject json) throws JSONException
    {
        id = json.getString("id");
        startCity = json.getString("startcity");
        destCity = json.getString("destcity");
        owner = json.getString("owner");
        date = json.getString("date");
        time = json.getString("time");
        price = json.getInt("price");
        seats = json.getInt("availSeatNum");

        JSONArray json_participants = json.getJSONArray("participants");
        participants = Participant.jsonToPar(json_participants);
    }

    public static Ride[] jsonToRides(String json) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(json);
        Ride[] rides = new Ride[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
        {
            rides[i] = new Ride(jsonArray.getJSONObject(i));
        }
        return rides;
    }

    public Participant getParticipant(String username)
    {
        for (Participant p : participants)
        {
            if (p.username.equals(username))
                return p;
        }
        return null;
    }
}

class Participant
{

    boolean isPaid;
    boolean isAccepted;
    boolean isRejected;
    String username;

    public static Participant[] jsonToPar(JSONArray jsonArray) throws JSONException
    {
        Participant[] participant = new Participant[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
        {
            participant[i] = new Participant(jsonArray.getJSONObject(i));
        }
        return participant;
    }

    private Participant(JSONObject json) throws JSONException
    {
        isPaid = json.getBoolean("isPaid");
        isAccepted = json.getBoolean("isAccepted");
        isRejected = json.getBoolean("isRejected");
        username = json.getString("rsu_username");
    }
}