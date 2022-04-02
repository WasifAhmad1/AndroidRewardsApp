package com.example.newrewardsproject.volley;

import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileVolley {
    public static final String endPoint = "Profile/UpdateProfile";
    private static String sampleApiKey;
    private static final String TAG = "CreateProfileVolley";

    public static void updateProfile(EditProfile editProfile, String fName, String lName, String username, String dept, String story,
                                     String position, String password, String location, String imgString, String api) {

        RequestQueue queue = Volley.newRequestQueue(editProfile);
        String urlToUse = makeURL(fName, lName, username, dept, story, position, password, location);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //now that we are getting a proper json response we need to do something here.
            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                String errorMsg = error.networkResponse == null ? error.getClass().getName() : new String(error.networkResponse.data);
                System.out.println("the error is " + errorMsg);

            }
        };

        JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject>(
                Request.Method.PUT, urlToUse, imgString, listener, error) {
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
                headers.put("ApiKey", api);
                return headers;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);

    }

    private static String makeURL(String fName, String lName, String username, String dept, String story,
                                  String position, String password, String location) {
        String urlString = "http://christopherhield.org/api/" + endPoint;
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        buildURL.appendQueryParameter("firstName", fName);
        buildURL.appendQueryParameter("lastName", lName);
        buildURL.appendQueryParameter("userName", username);
        buildURL.appendQueryParameter("department", dept);
        buildURL.appendQueryParameter("story", story);
        buildURL.appendQueryParameter("position", position);
        buildURL.appendQueryParameter("password", password);
        buildURL.appendQueryParameter("location", location);
        String newString = buildURL.build().toString();
        return newString;



    }

}
