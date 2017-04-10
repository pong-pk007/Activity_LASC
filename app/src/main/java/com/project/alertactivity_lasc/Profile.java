package com.project.alertactivity_lasc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

public class Profile extends AppCompatActivity {

    private static String Server = new Server().name();
    private static String User;
    private static String Name;
    private static String Major;
    List<String> activityID = new ArrayList<>();
    List<String> activityName = new ArrayList();
    List<String> activityDT = new ArrayList();
    List<String> activityYear = new ArrayList();
    List<String> activityCategory = new ArrayList();
    List<String> activityLocation = new ArrayList();
    List<String> activityTeacher = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final DB myDb = new DB(this);
        List<DB.sMembers> MebmerList = myDb.ReadLogin();
        for (DB.sMembers mem : MebmerList) {
            User = mem.gUser();
        }

        ImageView im_news = (ImageView)findViewById(R.id.imageView3);
        ImageView im_calendar = (ImageView)findViewById(R.id.imageView4);
//        ImageView im_album = (ImageView)findViewById(R.id.imageView5);
        ImageView im_question = (ImageView)findViewById(R.id.imageView6);
        ImageView im_profile = (ImageView)findViewById(R.id.imageView7);

        im_news.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, News.class);
                startActivity(i);
            }
        });

//        im_calendar.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent i = new Intent(Profile.this, Calendar.class);
//                startActivity(i);
//            }
//        });

//        im_album.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent i = new Intent(Profile.this, Album.class);
//                startActivity(i);
//            }
//        });

        im_question.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, Question.class);
                startActivity(i);
            }
        });

        im_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
        });

        TextView pswd = (TextView)findViewById(R.id.textView24);
        pswd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, ChangePass.class);
                startActivity(i);
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

        ImageView im_refresh = (ImageView)findViewById(R.id.refresh);
        im_refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new onLoad().execute(Server + "profile.json.php?username=" + User);
                new onLoad2().execute(Server + "registed.json.php?username=" + User);
            }
        });

        new onLoad().execute(Server + "profile.json.php?username=" + User);
        new onLoad2().execute(Server + "registed.json.php?username=" + User);
    }

    private class onLoad extends AsyncTask<String,Void,Void>
    {
        private ProgressDialog pd;
        ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
        boolean con = false;

        @Override
        protected void onPreExecute()
        {

            pd = new ProgressDialog(Profile.this);
            pd.setTitle("กำลังทำงาน");
            pd.setMessage("โหลดข้อมูล...");
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String jString;
            HashMap<String, String> map ;
            try {
                jString = getJsonFromUrl(params[0]);

                if (jString != null) {

                    JSONArray jArray = new JSONArray(jString);
                    for (int i=0; i < jArray.length() ; i++ ) {
                        JSONObject jObj = jArray.getJSONObject(i);

                        Name = jObj.getString("name");
                        Major = jObj.getString("major");

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
            TextView user = (TextView)findViewById(R.id.textView16);
            TextView name = (TextView)findViewById(R.id.textView17);
            TextView major = (TextView)findViewById(R.id.textView18);
            user.setText(User);
            name.setText(Name);
            major.setText(Major);

            pd.dismiss();
            if(con==false)
            {
                MessageBox("การเชื่อมต่อผิดพลาด");
            }
        }
    }

    private class onLoad2 extends AsyncTask<String,Void,Void>
    {
        private ProgressDialog pd;
        ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
        boolean con = false;

        @Override
        protected void onPreExecute()
        {
            activityDT.clear();
            activityID.clear();
            activityName.clear();
            activityYear.clear();
            activityCategory.clear();
            activityLocation.clear();
            activityTeacher.clear();
            pd = new ProgressDialog(Profile.this);
            pd.setTitle("กำลังทำงาน");
            pd.setMessage("โหลดข้อมูล...");
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String jString;
            HashMap<String, String> map ;
            try {
                jString = getJsonFromUrl(params[0]);

                if (jString != null) {

                    String year;
                    String name;
                    String status;

                    JSONArray jArray = new JSONArray(jString);
                    for (int i=0; i < jArray.length() ; i++ ) {
                        JSONObject jObj = jArray.getJSONObject(i);

                        year = jObj.getString("year");
                        name = jObj.getString("name");
                        status = jObj.getString("status");

                        map = new HashMap<String, String>();
                        map.put("year",  year);
                        map.put("name",  name);
                        map.put("status",  status);
                        myList.add(map);

                        activityName.add(jObj.getString("name2"));
                        activityID.add(jObj.getString("id"));
                        activityYear.add(jObj.getString("schoolyear"));
                        activityDT.add(jObj.getString("dt"));
                        activityCategory.add(jObj.getString("category"));
                        activityLocation.add(jObj.getString("location"));
                        activityTeacher.add(jObj.getString("teacher"));
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
                ListAdapter adapter = new SimpleAdapter(getBaseContext(), myList , R.layout.list_registed,
                        new String[] { "year", "name", "status" },
                        new int[] { R.id.textView22, R.id.textView23, R.id.textView21 });
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(!activityID.get(position).equals(""))
                        {
                            final Dialog dialog = new Dialog(Profile.this);
                            dialog.setTitle("รายละเอียดกิจกรรม");
                            dialog.setContentView(R.layout.popup);

                            final TextView name = (TextView) dialog.findViewById(R.id.textView30);
                            final TextView dt = (TextView) dialog.findViewById(R.id.textView31);
                            final TextView year = (TextView) dialog.findViewById(R.id.textView32);
                            final TextView category = (TextView) dialog.findViewById(R.id.textView25);
                            final TextView location = (TextView) dialog.findViewById(R.id.textView35);
                            final TextView teacher = (TextView) dialog.findViewById(R.id.textView37);
                            Button buttonClose = (Button) dialog.findViewById(R.id.button2);

                            buttonClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            name.setText(activityName.get(position));
                            year.setText(activityYear.get(position));
                            dt.setText(activityDT.get(position));
                            category.setText(activityCategory.get(position));
                            location.setText(activityLocation.get(position));
                            teacher.setText(activityTeacher.get(position));
                            dialog.show();
                        }
                    }
                });

            }
            catch (Exception e)
            {
                //Log.i("MyService", "Problem reading " + e.toString());
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
            //Log.i("MyService", "Problem reading " + ex.getLocalizedMessage());
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

    public void onBackPressed() {
        Intent intent = new Intent(Profile.this, News.class);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
    }
}
