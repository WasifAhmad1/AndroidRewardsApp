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
import com.example.newrewardsproject.SendPoints;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendPointsVolley {
    private static final String endPoint = "Rewards/AddRewardRecord";
    private static final String TAG = "CreateProfileVolley";

    public static void sendPoints (SendPoints sendPoints, String api, String receiverUser, String givingUser,
                                   String giverName, String amount,
                                   String note) {
        RequestQueue queue = Volley.newRequestQueue(sendPoints);
        String urlToUse = makeUrl(receiverUser, givingUser, giverName, amount, note);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //now that we are getting a proper json response we need to do something here
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
                Request.Method.POST, urlToUse, null, listener, error) {
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

    private static String makeUrl(String receiverUser, String givingUser, String giverName, String amount, String note) {
        String urlString = "http://christopherhield.org/api/" + endPoint;
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        buildURL.appendQueryParameter("receiverUser", receiverUser);
        buildURL.appendQueryParameter("giverUser", givingUser);
        buildURL.appendQueryParameter("giverName", giverName);
        buildURL.appendQueryParameter("amount", amount);
        buildURL.appendQueryParameter("note", note);
        String newString = buildURL.build().toString();
        //buildURL.appendQueryParameter("email", "foo@depaul.edu");
        //buildURL.appendQueryParameter("email", email);

        return newString;


    }
}
