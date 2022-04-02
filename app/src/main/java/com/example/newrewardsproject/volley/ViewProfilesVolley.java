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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.newrewardsproject.ViewProfiles;
import com.example.newrewardsproject.YourProfile;
import com.example.newrewardsproject.recycler.Employee;
import com.example.newrewardsproject.recycler.RewardNote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ViewProfilesVolley {
    public static final String endPoint = "Profile/GetAllProfiles";
    private static final String TAG = "GetProfilesVolley";
    //private static ArrayList<Employee> employees = new ArrayList<Employee>();
    private static ArrayList<RewardNote> rewardNotes;

    public static void getProfiles (ViewProfiles viewProfiles, String api) {
        //employees = new ArrayList<Employee>();
        RequestQueue queue = Volley.newRequestQueue(viewProfiles);
        String urlToUse = makeUrl();
        //we do the api call, get some input and then send this input to the ViewProfiles activity

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //now that we are getting a proper json response we need to do something here.
                System.out.println("break");
                //based on the debugger, the results come in as a JSON Array of json objects. The objects in turn have the following
                //fields. These are firstName, lastName, userName, department,story position, imagebytes, and the reward history
                ArrayList<Employee> employees = new ArrayList<Employee>();

                for (int i = 0 ; i<response.length(); i++) {
                    try {
                        int count = 0;
                        JSONObject jsonObject = response.getJSONObject(i);
                        String fName = jsonObject.getString("firstName");
                        String lName = jsonObject.getString("lastName");
                        String userName = jsonObject.getString("userName");
                        String department = jsonObject.getString("department");
                        String story = jsonObject.getString("story");
                        String position = jsonObject.getString("position");
                        String imageBytes = jsonObject.getString("imageBytes");
                        JSONArray records = jsonObject.getJSONArray("rewardRecordViews");
                        for(int j = 0; j<records.length(); j++){
                            System.out.println("The length is " + records.length());
                            JSONObject record = records.getJSONObject(j);
                            String giverName = record.getString("giverName");
                            String amount = record.getString("amount");
                            String note = record.getString("note");
                            String awardDate = record.getString("awardDate");
                            count += Integer.parseInt(amount);
                        }
                        //we need to also get the points to display. The points in this care for all users is zero before we
                        //we have added any points
                        Employee employee = new Employee(fName, lName, userName, department, story, position,
                                imageBytes, String.valueOf(count));
                        employees.add(employee);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                System.out.println("The list is" + employees.size());
                viewProfiles.runOnUiThread(() ->
                    viewProfiles.handleList(employees));


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


        JsonRequest<JSONArray> jsonRequest = new JsonRequest<JSONArray>(
                Request.Method.GET, urlToUse, null, listener, error) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                // This method is always the same!
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONArray(jsonString),
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
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);





    }



    private static String makeUrl () {
        String urlString = "http://christopherhield.org/api/" + endPoint;
        Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
        String newString = buildURL.build().toString();
        return(newString);

    }

}
