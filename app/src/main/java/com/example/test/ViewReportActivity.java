package com.example.test;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

public class ViewReportActivity extends AppCompatActivity {

    protected void onCreate(Bundle b){
        super.onCreate(b);

        WebView web = new WebView(this);
        setContentView(web);

        try {
            String path = getIntent().getStringExtra("file");

            BufferedReader br = new BufferedReader(new FileReader(path));
            String data = br.readLine();
            br.close();

            web.loadData("<h2>"+data+"</h2>", "text/html", "UTF-8");

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}