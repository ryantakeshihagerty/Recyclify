package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LeaderboardActivity extends Activity {
    ListView listView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.listView);
        downloadJSON("https://ineedtophp.000webhostapp.com/view_leaderboard.php");
    }

    private void downloadJSON(final String webURL) {
        class DownloadJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(webURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = br.readLine()) != null) {
                        // Add to JSON string
                        sb.append(json + "\n");
                    }
                    // Return JSON string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    loadIntoListView(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        DownloadJSON dlJSON = new DownloadJSON();
        dlJSON.execute();
    }

    private void loadIntoListView(String jsonString) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonString);
        String[] output = new String[jsonArray.length()];
        int rank = 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            output[i] = "#" + rank + " " + json.getString("User") + " $" + json.getString("TotalMoneyMade");
            rank++;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, output);
        listView.setAdapter(arrayAdapter);
    }
}
