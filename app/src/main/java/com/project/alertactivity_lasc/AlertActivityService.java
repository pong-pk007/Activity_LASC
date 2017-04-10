package com.project.alertactivity_lasc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlertActivityService extends Service {

    private static String TAG = "MyService";
    private Handler handler;
    private Runnable runnable;
    private final int runTime = 5000;

    List<String> id = new ArrayList<>();
    List<String> name = new ArrayList<>();
    List<String> dt = new ArrayList<>();
    List<String> timediff = new ArrayList<>();
    List<String> in30 = new ArrayList<>();
    List<String> in5 = new ArrayList<>();
    List<String> newsID = new ArrayList<>();
    List<String> newsTitle = new ArrayList<>();
    private static String Server = new Server().name();
    private static String User,LastNews;
    private final DB myDb = new DB(this);

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");


        List<DB.sMembers> MebmerList = myDb.ReadLogin();
        for (DB.sMembers mem : MebmerList) {
            User = mem.gUser();
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onRun");

                List<DB.sNews> MebmerList = myDb.ReadNews();
                if(MebmerList != null)
                {
                    for (DB.sNews mem : MebmerList) {
                        LastNews=mem.gID();
                    }
                }

                new onLoad().execute(Server + "activity.json.php?user=" + User);
                new onLoad2().execute(Server + "news.json.php");
                handler.postDelayed(runnable, runTime);
            }
        };
        handler.post(runnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart");
    }

    private class onLoad extends AsyncTask<String,Void,Void>
    {
        boolean con = false;
        private String message;

        @Override
        protected void onPreExecute()
        {
            id.clear();
            name.clear();
            dt.clear();
            timediff.clear();
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

                        id.add(jObj.getString("id"));
                        name.add(jObj.getString("name"));
                        dt.add(jObj.getString("dtStart"));
                        timediff.add(jObj.getString("timediff"));

                    }// for
                    con = true;
                }// if

            } catch (IOException e) {
                //MessageBox("การเชื่อมต่อผิดพลาด");
                //message = e.toString();
            } catch (JSONException e) {
                //MessageBox("การรับส่งผิดพลาด");
                //message = e.toString();
            }catch (Exception e) {
                //Log.i(TAG, "Problem reading " + e.getLocalizedMessage());
                //MessageBox("การเชื่อมต่อผิดพลาด");
                //message = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            if(con==false)
            {
                //Log.i(TAG, "false");
            }
//            else {
//                for (int i = 0; i < id.size(); i++) {
//                    if(timediff.get(i).equals("30") && !in30.contains(id.get(i))) {
//                        in30.add(id.get(i));
//                        Notify("เริ่มกิจกรรม", name.get(i) + " ในอีก 30 นาที");
//                        Log.i(TAG, "เริ่มกิจกรรม");
//                    }
//                    if(timediff.get(i).equals("5") && !in5.contains(id.get(i))) {
//                        in5.add(id.get(i));
//                        Notify("เริ่มกิจกรรม", name.get(i) + " ในอีก 5 นาที");
//                        Log.i(TAG, "เริ่มกิจกรรม");
//                    }
//                    Log.i(TAG, timediff.get(i));
//                }
//            }
        }
    }

    private class onLoad2 extends AsyncTask<String,Void,Void>
    {
        boolean con = false;
        private String message;

        @Override
        protected void onPreExecute()
        {
            newsID.clear();
            newsTitle.clear();
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

                        newsID.add(jObj.getString("id"));
                        newsTitle.add(jObj.getString("name"));

                    }// for
                    con = true;
                }// if

            } catch (IOException e) {
                //MessageBox("การเชื่อมต่อผิดพลาด");
                //message = e.toString();
            } catch (JSONException e) {
                //MessageBox("การรับส่งผิดพลาด");
                //message = e.toString();
            }catch (Exception e) {
                //Log.i(TAG, "Problem reading " + e.getLocalizedMessage());
                //MessageBox("การเชื่อมต่อผิดพลาด");
                //message = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            if(con==false)
            {
                //Log.i(TAG, "false");
            }
            else {
                if(newsID.size()>0)
                {
                    if (!newsID.get(0).equals(LastNews))
                    {
                        Log.i(TAG, "มีข่าวใหม่ "+newsID.get(0));
                        Notify2("มีข่าวใหม่", newsTitle.get(0));
                        myDb.UpdateNews(newsID.get(0));
                    }
                }
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
            //Log.i(TAG, "The response is: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK){
                //Log.i(TAG, " size: " + httpCon.getContentLength());

                InputStream ins = httpCon.getInputStream();
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(ins,"UTF-8"));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append("\n");
                    //Log.i(TAG, line);
                }
                rd.close();
                return response.toString();
            }

        } catch (Exception ex) {
            //Log.i(TAG, "Problem reading " +  ex.getLocalizedMessage());
            //ex.printStackTrace();
            //MessageBox(ex.toString());
        }
        return null;
    }

//    private void Notify(String notificationTitle, String notificationMessage){
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        @SuppressWarnings("deprecation")
//
//        Notification notification = new Notification(R.drawable.ic_launcher,"แจ้งเตือนกิจกรรม", System.currentTimeMillis());
//        Intent notificationIntent = new Intent(this,Calendar.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
//
//        notification.setLatestEventInfo(AlertActivityService.this, notificationTitle, notificationMessage, pendingIntent);
//        notification.defaults = Notification.DEFAULT_SOUND; // Sound
//        notificationManager.notify(9998, notification);
//    }

    private void Notify2(String notificationTitle, String notificationMessage){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")

        Notification notification = new Notification(R.drawable.ic_launcher,"ข่าว", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this,News.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);

        notification.setLatestEventInfo(AlertActivityService.this, notificationTitle, notificationMessage, pendingIntent);
        notification.defaults = Notification.DEFAULT_SOUND; // Sound
        notificationManager.notify(9999, notification);
    }
}
