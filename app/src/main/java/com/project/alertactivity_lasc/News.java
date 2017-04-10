package com.project.alertactivity_lasc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.HashMap;
import java.util.List;

public class News extends Activity {

    private static String Server = new Server().name();
    List<String> ID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        final DB myDb = new DB(this);
        List<DB.sMembers> MebmerList = myDb.ReadLogin();

        ImageView im_news = (ImageView)findViewById(R.id.imageView3);
//        ImageView im_calendar = (ImageView)findViewById(R.id.imageView4);
//        ImageView im_album = (ImageView)findViewById(R.id.imageView5);
        ImageView im_question = (ImageView)findViewById(R.id.imageView6);
        ImageView im_profile = (ImageView)findViewById(R.id.imageView7);

        im_news.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
        });

//        im_calendar.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent i = new Intent(News.this, Calendar.class);
//                startActivity(i);
//            }
//        });

//        im_album.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent i = new Intent(News.this, Album.class);
//                startActivity(i);
//            }
//        });

        im_question.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(News.this, Question.class);
                startActivity(i);
            }
        });

        im_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(News.this, Profile.class);
                startActivity(i);
            }
        });

        ImageView im_refresh = (ImageView)findViewById(R.id.refresh);
        im_refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new onLoad().execute(Server + "news.json.php");
            }
        });
        ImageView logout = (ImageView) findViewById(R.id.imageView4);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StopService();
                myDb.DeleteLogin();
                restart();
            }
        });

        new onLoad().execute(Server + "news.json.php");
    }

    private class onLoad extends AsyncTask<String,Void,Void>
    {
        private ProgressDialog pd;
        ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
        boolean con = false;

        @Override
        protected void onPreExecute()
        {
            pd = new ProgressDialog(News.this);
            pd.setTitle("กำลังทำงาน");
            pd.setMessage("โหลดข้อมูล...");
            pd.show();
            ID.clear();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String jString;
            HashMap<String, String> map ;
            try {
                jString = getJsonFromUrl(params[0]);

                if (jString != null) {

                    String name;
                    String dt;
                    String teacher;

                    JSONArray jArray = new JSONArray(jString);
                    for (int i=0; i < jArray.length() ; i++ ) {
                        JSONObject jObj = jArray.getJSONObject(i);

                        ID.add(jObj.getString("id"));
                        name = jObj.getString("name");
                        dt = jObj.getString("dt");
                        teacher = jObj.getString("teacher");

                        map = new HashMap<String, String>();
                        map.put("name",  name);
                        map.put("dt",  "แจ้งเมื่อวันที่ "+dt);
                        map.put("teacher",  "แจ้งโดย "+teacher);
                        myList.add(map);

                    }// for
                    con = true;
                }// if

            } catch (IOException e) {
                //func.MessageBox("การเชื่อมต่อผิดพลาด");
            } catch (JSONException e) {
                //func.MessageBox("การรับส่งผิดพลาด");
            }catch (Exception e) {
                //Log.d(TAG, "Problem reading " +  ex.getLocalizedMessage());
                //func.MessageBox("การเชื่อมต่อผิดพลาด");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            try{
                ListView lv = (ListView)findViewById(R.id.listView);
                ListAdapter adapter = new SimpleAdapter(getBaseContext(), myList , R.layout.list_news,
                        new String[] { "name", "dt", "teacher" },
                        new int[] { R.id.textView, R.id.textView2, R.id.textView3 });
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(News.this, News2.class);
                        i.putExtra("ID", ID.get(position));
                        startActivity(i);
                    }
                });

            }
            catch (Exception e)
            {

            }
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
    private void StopService()
    {
        stopService(new Intent(this, AlertActivityService.class));
    }

    private void restart()
    {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
