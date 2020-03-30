package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardActivity extends Activity implements PopupMenu.OnMenuItemClickListener {
    ListView listViewLB;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listViewLB = findViewById(R.id.listViewLB);
        downloadJSON("https://ineedtophp.000webhostapp.com/view_leaderboard.php");
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popup.inflate(R.menu.leaderboard_menu);
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
        LinkedHashMap<String, String> lbData = new LinkedHashMap<>();
        int rank = 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            lbData.put("#" + rank + " " + json.getString("User") + " $" + json.getString("TotalMoneyMade"), "last seen at " + json.getString("LastVisited"));
            rank++;
        }

        List<HashMap<String, String>> lbList = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, lbList, R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2});

        Iterator it = lbData.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> rsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            rsMap.put("First Line", pair.getKey().toString());
            rsMap.put("Second Line", pair.getValue().toString());
            lbList.add(rsMap);
        }
        listViewLB.setAdapter(adapter);
    }
}
