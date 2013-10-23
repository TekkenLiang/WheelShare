/*
 * Victor Kretov
 */
package com.example.wheelshare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RideRequest
{

    protected String username;
    protected String id;

    private RideRequest(JSONObject json) throws JSONException
    {
        this.username = json.getString("username");
        this.id = json.getString("id");
    }

    public static RideRequest[] jsonToRequest(String json) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(json);
        RideRequest[] requests = new RideRequest[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
        {
            requests[i] = new RideRequest(jsonArray.getJSONObject(i));
        }
        return requests;
    }
}
