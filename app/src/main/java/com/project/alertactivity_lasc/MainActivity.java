package com.project.alertactivity_lasc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.alertactivity_lasc.DB.sMembers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MyService";
    private static String Server = new Server().name();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txt_about = (TextView)findViewById(R.id.text_about);
        txt_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivity = new Intent(MainActivity.this,About.class);
                startActivity(newActivity);
            }
        });

        if( getIntent().getBooleanExtra("Exit me", false)){
            return;
        }

        final DB myDb = new DB(this);
        myDb.getWritableDatabase(); // First method

        List<sMembers> MebmerList = myDb.ReadLogin();
        if(MebmerList != null)
        {
            for (sMembers mem : MebmerList) {
                EditText user = (EditText)findViewById(R.id.editText);
                EditText pass = (EditText)findViewById(R.id.editText2);
                user.setText(mem.gUser());
                pass.setText(mem.gPass());

                new onLoad().execute(Server + "login.json.php?user=" + user.getText().toString() + "&pass=" + pass.getText().toString());
            }
        }

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText user = (EditText) findViewById(R.id.editText);
                EditText pass = (EditText) findViewById(R.id.editText2);

                myDb.DeleteLogin();
                myDb.WriteLogin(user.getText().toString(), pass.getText().toString());

                new onLoad().execute(Server + "login.json.php?user=" + user.getText().toString() + "&pass=" + pass.getText().toString());
            }
        });
    }

    private class onLoad extends AsyncTask<String,Void,Void>
    {
        private ProgressDialog pd;
        boolean con = false;
        String id = null;
        private String message;

        @Override
        protected void onPreExecute()
        {
            pd = new ProgressDialog(MainActivity.this);
            pd.setTitle("กำลังทำงาน");
            pd.setMessage("โหลดข้อมูล...");
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String jString;
            try {
                jString = getJsonFromUrl(params[0]);

                if (jString != null) {

                    JSONArray jArray = new JSONArray(jString);
                    for (int i=0; i < jArray.length() ; i++ ) {
                        JSONObject jObj = jArray.getJSONObject(i);
                        id = jObj.getString("id");
                    }// for
                    con = true;
                }// if

            } catch (IOException e) {
                //MessageBox("การเชื่อมต่อผิดพลาด");
                message = e.toString();
            } catch (JSONException e) {
                //MessageBox("การรับส่งผิดพลาด");
                message = e.toString();
            }catch (Exception e) {
                //Log.d(TAG, "Problem reading " +  ex.getLocalizedMessage());
                //MessageBox("การเชื่อมต่อผิดพลาด");
                message = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            pd.dismiss();
            if(con!=false && id!=null)
            {
                StartService();
                Intent i = new Intent(MainActivity.this, News.class);
                startActivityForResult(i,1);
            }
            else {
                MessageBox("รหัสผ่านผิด");
                //MessageBox(message);
            }
        }
    }

    private void StartService()
    {
        startService(new Intent(this, AlertActivityService.class));
    }

    private String getJsonFromUrl(String strUrl) throws IOException {

        URL url = new URL(strUrl);
        try {
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("GET");
            httpCon.setConnectTimeout(6*1000);
            httpCon.connect();

            int responseCode = httpCon.getResponseCode();
            //Log.d(TAG, "The response is: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK){
                //Log.d(TAG, " size: " + httpCon.getContentLength());

                InputStream ins = httpCon.getInputStream();
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(ins,"UTF-8"));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append("\n");
                    //Log.d(TAG, line);
                }
                rd.close();
                return response.toString();
            }

        } catch (Exception ex) {
            //Log.d(TAG, "Problem reading " +  ex.getLocalizedMessage());
            //ex.printStackTrace();
        }
        return null;
    }

    public void MessageBox(String str) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(str);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    protected void onActivityResult ( int requestCode, int resultCode, Intent intent )
    {
        super.onActivityResult ( requestCode, resultCode, intent );
        if ( requestCode == 1)
        {
            finish();
        }
    }
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ออกจากแอพลิเคชั่น");
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setCancelable(true);
        dialog.setMessage("คุณต้องการออกจากแอพลิเคชั่น หรือไม่?");
        dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setAction(Intent.ACTION_MAIN);
                startActivity(intent);
            }
        });

        dialog.setNegativeButton("ไม่ใช่", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }
}
