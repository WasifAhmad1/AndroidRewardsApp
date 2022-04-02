package com.example.newrewardsproject.volley;

import android.app.DownloadManager;
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
import com.example.newrewardsproject.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginAPIVolley {
    private static String endPoint = "Profile/Login";

    public static void tryLogin (MainActivity mainActivity, String apiKey, String login, String password) {
        //here we will get the login info and either get a false or true back based on whether it works
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        String urlToUse = makeURL(login, password);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mainActivity.runOnUiThread(() ->
                        mainActivity.handleCreateOrLoginSucceeded(login, password));

            }
            };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.networkResponse == null ? error.getClass().getName() : new String(error.networkResponse.data);
                mainActivity.runOnUiThread(() ->
                        mainActivity.handleLoginFailure(errorMsg));

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
