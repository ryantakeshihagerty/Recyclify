package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class ShareActivity extends Activity implements PopupMenu.OnMenuItemClickListener {
    EditText moneyInput;
    Button submitButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Initialize widgets
        moneyInput = findViewById(R.id.editTextMoney);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String userID = getUserID();
                String time = getTime();
                String plantName = getPlantName();
                String moneyMade = getMoneyMade();
                InsertData(userID, time, plantName, moneyMade);
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popup.inflate(R.menu.share_menu);
        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_get_place:
                startActivity(new Intent(this, MapsActivityCurrentPlace.class));
                return true;
            case R.id.nav_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.nav_leaderboard:
                startActivity(new Intent(this, LeaderboardActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getUserID() {
        // get user's userID or phone#?
        return "FakeName";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getTime() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(formatter);
    }

    private String getPlantName() {
        // use find place to find nearest recycling center
        // get name of that recycling center from returned json string/object
        // return name
        return "My Local Recycling Plant";
    }

    private String getMoneyMade() {
        String moneyMade = moneyInput.getText().toString();
        return moneyMade;
    }

    public void InsertData(final String userID, final String time, final String plantName, final String moneyMade) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... Strings) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("UserID", userID));
                nameValuePairs.add(new BasicNameValuePair("Time", time));
                nameValuePairs.add(new BasicNameValuePair("PlantName", plantName));
                nameValuePairs.add(new BasicNameValuePair("MoneyMade", moneyMade));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("https://ineedtophp.000webhostapp.com/insert_data.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "Submitted!";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Toast.makeText(ShareActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
