package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends Activity implements PopupMenu.OnMenuItemClickListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //temp userID until login is implemented
        String myUserID = "fxie";
        getProfileValues(myUserID);
    }

    private void getProfileValues(final String user) {
        class DownloadProfile extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://ineedtophp.000webhostapp.com/get_profile_values.php?userID=" + user);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = br.readLine()) != null) {
                        sb.append(json + "\n"); // Adding to JSON string
                    }
                    return sb.toString().trim(); // Returning JSON string
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    loadValues(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        DownloadProfile dlProfile = new DownloadProfile();
        dlProfile.execute();
    }

    private void loadValues(String jsonString) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonString);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.profile_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_get_place:
                startActivity(new Intent( this, MapsActivityCurrentPlace.class));
                return true;
            case R.id.nav_leaderboard:
                startActivity(new Intent(this, LeaderboardActivity.class));
                return true;
            case R.id.nav_share:
                startActivity(new Intent(this, ShareActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
