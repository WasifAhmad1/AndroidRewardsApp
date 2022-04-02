package com.example.newrewardsproject.volley;

import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.newrewardsproject.EditProfile;
import com.example.newrewardsproject.YourProfile;
import com.example.newrewardsproject.recycler.RewardNote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetProfileInfoVolley {
    public static final String endPoint = "Profile/Login";
    private static ArrayList<RewardNote> rewardNotes = new ArrayList<RewardNote>();

    public static void getProfile(YourProfile yourProfile, String username, String password, String apiKey) {
        RequestQueue queue = Volley.newRequestQueue(yourProfile);
        String urlToUse = makeURL(username, password);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //in the positive response we will make the call to the activity to give it back the information
                try {
                    String fName = response.getString("firstName");
                    String lName = response.getString("lastName");
                    String department = response.getString("department");
                    String story = response.getString("story");
                    String position = response.getString("position");
                    String remainingPoints = response.getString("remainingPointsToAward");
                    String location = response.getString("location");
                    String imageBytes = response.getString("imageBytes");
                    JSONArray jsonArray = response.getJSONArray("rewardRecordViews");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject record = jsonArray.getJSONObject(i);
                        String giverName = record.getString("giverName");
                        String amount = record.getString("amount");
                        String note = record.getString("note");
                        String awardDate = record.getString("awardDate");
                        RewardNote rewardNote = new RewardNote(giverName, amount, note, awardDate);
                        rewardNotes.add(rewardNote);
                    }
                    yourProfile.getData(fName, lName, location, department, position,
                            remainingPoints, story, imageBytes, rewardNotes);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };

        JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject>(
                Request.Method.GET, urlToUse, null, listener, error) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // This method is always the same!
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Accept", "application/json");
                headers.put("ApiKey", apiKey);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    public static void editProfile(EditProfile editProfile, String username, String password, String apiKey) {
        RequestQueue queue = Volley.newRequestQueue(editProfile);
        String urlToUse = makeURL(username, password);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //in the positive response we will make the call to the activity to give it back the information
                try {
                    String fName = response.getString("firstName");
                    String lName = response.getString("lastName");
                    String department = response.getString("department");
                    String story = response.getString("story");
                    String position = response.getString("position");
                    String remainingPoints = response.getString("remainingPointsToAward");
                    String location = response.getString("location");
                    String imageBytes = response.getString("imageBytes");
                    editProfile.editData(fName, lName, department, position, story, imageBytes);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };

        JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject>(
                Request.Method.GET, urlToUse, null, listener, error) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // This method is always the same!
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Accept", "application/json");
                headers.put("ApiKey", apiKey);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    private static String makeURL(String username, String password){
        String urlString = "http://christopherhield.org/api/" + endPoint;
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        buildURL.appendQueryParameter("userName", username);
        buildURL.appendQueryParameter("password", password);
        String newString = buildURL.build().toString();
        return newString;

    }
}
