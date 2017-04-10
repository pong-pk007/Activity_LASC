package com.project.alertactivity_lasc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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

public class Question2 extends AppCompatActivity {

    private static String Server = new Server().name();
    private static String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question2);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent i = getIntent();
        ID = i.getStringExtra("ID");

        ImageView im_news = (ImageView)findViewById(R.id.imageView2);
        im_news.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText comment = (EditText)findViewById(R.id.editText);
                comment(comment.getText().toString());
                comment.setText("");
                new onLoad().execute(Server + "question2.json.php?id=" + ID);
            }
        });


        ImageView im_refresh = (ImageView)findViewById(R.id.refresh);
        im_refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new onLoad().execute(Server + "question2.json.php?id="+ID);
            }
        });

        new onLoad().execute(Server + "question2.json.php?id="+ID);
    }

    private class onLoad extends AsyncTask<String,Void,Void>
    {
        private ProgressDialog pd;
        ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
        boolean con = false;

        @Override
        protected void onPreExecute()
        {
            pd = new ProgressDialog(Question2.this);
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

                    String comment;
                    String dt;
                    String q;

                    JSONArray jArray = new JSONArray(jString);
                    for (int i=0; i < jArray.length() ; i++ ) {
                        JSONObject jObj = jArray.getJSONObject(i);

                        comment = jObj.getString("comment");
                        dt = jObj.getString("dt");
                        q = jObj.getString("q");

                        map = new HashMap<String, String>();
                        if(q.equals("2")) {
                            map.put("comment1", comment);
                            map.put("dt1", "วันที่ " + dt);
                            map.put("comment2", "");
                            map.put("dt2", "");
                        }else{
                            map.put("comment1", "");
                            map.put("dt1", "");
                            map.put("comment2", comment);
                            map.put("dt2", "วันที่ " + dt);
                        }
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
                ListAdapter adapter = new SimpleAdapter(getBaseContext(), myList , R.layout.list_chat,
                        new String[] { "comment1", "comment2", "dt1", "dt2" },
                        new int[] { R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7 });
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
                lv.setSelection(lv.getCount() - 1);

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

    private void comment(String c)
    {
        try
        {
            String url = Server+"comment.php";
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("comment", c));
            params.add(new BasicNameValuePair("post", ID));
            getHttpPost(url, params);

        } catch (Exception e) {
            MessageBox("การเชื่อมต่อผิดพลาด");
            //MessageBox(e.toString());
        }
    }

    public String getHttpPost(String url,List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                //Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
