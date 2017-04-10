package com.project.alertactivity_lasc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChangePass extends AppCompatActivity {

    private static String Server = new Server().name();
    private static String User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final DB myDb = new DB(this);
        List<DB.sMembers> MebmerList = myDb.ReadLogin();
        for (DB.sMembers mem : MebmerList) {
            User = mem.gUser();
        }

        ImageView im_news = (ImageView)findViewById(R.id.imageView2);
        im_news.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText e4 = (EditText)findViewById(R.id.editText4);
                EditText e5 = (EditText)findViewById(R.id.editText5);
                String p1 = e4.getText().toString();
                String p2 = e5.getText().toString();
                if(p1.equals(p2))
                {
                    NewPSWD(e4.getText().toString());
                    myDb.DeleteLogin();
                    Intent intent = new Intent(ChangePass.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Exit me", true);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getBaseContext(),"ใส่รหัสผ่านไม่ตรงกัน",Toast.LENGTH_LONG).show();
                    e4.setText("");
                    e5.setText("");
                }
            }
        });
    }

    private void NewPSWD(String c)
    {
        try
        {
            String url = Server+"newpswd.php";
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pswd", c));
            params.add(new BasicNameValuePair("user", User));
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

    public void MessageBox(String str) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(str);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
}
