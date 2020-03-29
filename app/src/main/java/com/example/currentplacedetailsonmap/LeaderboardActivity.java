package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LeaderboardActivity extends Activity implements PopupMenu.OnMenuItemClickListener {
    ListView listView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.listView);
        downloadJSON("https://ineedtophp.000webhostapp.com/view_leaderboard.php");
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popup.inflate(R.menu.profile_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_get_place:
                startActivity(new Intent( this, MapsActivityCurrentPlace.class));
                return true;
            case R.id.nav_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.nav_share:
                startActivity(new Intent(this, ShareActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
