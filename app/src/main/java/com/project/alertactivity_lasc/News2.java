package com.project.alertactivity_lasc;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class News2 extends AppCompatActivity {

    private static String TAG = "MyService";
    private static String Server = new Server().name();
    private static String ID;
    private static String name;
    private static String detail;
    private static String dt;
    private static String img;
    private static String teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news2);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(9999);

        ImageView im_news = (ImageView)findViewById(R.id.imageView2);
        im_news.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();
        ID = i.getStringExtra("ID");
        i.removeExtra("ID");
        Log.i(TAG, "Get: " + ID);

        new onLoad().execute(Server + "news2.json.php?id="+ID);
    }

    private class onLoad extends AsyncTask<String,Void,Void>
    {
        private ProgressDialog pd;
        boolean con = false;

        @Override
        protected void onPreExecute()
        {
            pd = new ProgressDialog(News2.this);
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

                        name = jObj.getString("name");
                        dt = "แจ้งเมื่อวันที่ " + jObj.getString("dt");
                        detail = "... " + jObj.getString("detail");
                        teacher = jObj.getString("teacher");
                        img = jObj.getString("img");

                    }// for
                    con = true;
                }// if

            } catch (IOException e) {
                //MessageBox("การเชื่อมต่อผิดพลาด IO");
            } catch (JSONException e) {
                //MessageBox("การรับส่งผิดพลาด JSON");
            }catch (Exception e) {
                //Log.i("MyService", "Problem reading " + e.toString());
                //MessageBox("การเชื่อมต่อผิดพลาด"+e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            TextView nametxt = (TextView)findViewById(R.id.textView10);
            TextView dttxt = (TextView)findViewById(R.id.textView11);
            TextView detailtxt = (TextView)findViewById(R.id.textView12);
            TextView teachertxt = (TextView)findViewById(R.id.textView13);

            nametxt.setText(name);
            dttxt.setText(dt);
            detailtxt.setText(detail);
            teachertxt.setText(teacher);

            ImageView image = (ImageView) findViewById(R.id.imageView12);
            image.setImageBitmap(fetchImage(Server.replace("app/", "") + "upload/news/" + img + ".jpg"));

            pd.dismiss();
            if(con==false)
            {
                MessageBox("การเชื่อมต่อผิดพลาด");
            }
        }
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

    public Bitmap fetchImage( String imageUrl )
    {
        try
        {
            URL url = new URL( imageUrl.trim() ); // imageUrl คือ url ของรูปภาพ
            InputStream input = null;
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            httpConn.setRequestMethod("GET");
            httpConn.setReadTimeout(6000); // ตั้งเวลา  connect timeout
            httpConn.connect(); // connection

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                input = httpConn.getInputStream(); // จับใส่ InputStream
            }
            Bitmap bitmap = BitmapFactory.decodeStream(input); //แปลงเป็น Bitmap
            input.close();
            httpConn.disconnect();
            return bitmap;

        }
        catch ( MalformedURLException e ){
            //Log.d("fetchImage", "MalformedURLException invalid URL: " + imageUrl);
            //Toast.makeText(this.mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }catch ( IOException e ){
            //Log.d("fetchImage","IO exception: " + e);
            //Toast.makeText(this.mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            //Log.d("fetchImage","Exception: " + e);
            //Toast.makeText(this.mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
