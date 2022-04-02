package com.example.newrewardsproject.volley;

import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.newrewardsproject.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetApiKeyVolley {
    private static final String endPoint = "Profile/GetStudentApiKey";

    public static void getApiKey (MainActivity activity, String fName, String lName, String email, String id){
        RequestQueue queue = Volley.newRequestQueue(activity);
        String urlToUse = makeUrl(activity, fName, lName, id, email);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String apiKey = response.getString("apiKey");
                    activity.runOnUiThread(() ->
                            activity.handleKey(apiKey));
                } catch (JSONException e) {
                    activity.runOnUiThread(() ->
                            activity.handleError(e.getMessage()));
                }

            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("We are in the error");
                String errorMsg = error.networkResponse == null ? error.getClass().getName() : new String(error.networkResponse.data);
                activity.runOnUiThread(() ->
                        activity.handleError(errorMsg));
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
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);


    }

    private static String makeUrl(MainActivity activity,
                                  String fName, String lName, String id, String email) {

        String urlString = "http://christopherhield.org/api/" + endPoint;
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        buildURL.appendQueryParameter("firstName", fName);
        buildURL.appendQueryParameter("lastName", lName);
        buildURL.appendQueryParameter("studentId", id);
        String newString = buildURL.build().toString();
        newString = newString + "&email=" + email;
        //buildURL.appendQueryParameter("email", "foo@depaul.edu");
        //buildURL.appendQueryParameter("email", email);

        return newString;
    }
}
