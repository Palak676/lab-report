package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class SavedActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<JSONObject> reports = new ArrayList<>();

    protected void onCreate(Bundle b) {
        super.onCreate(b);

        listView = new ListView(this);
        setContentView(listView);

        loadReports();

        listView.setAdapter(new ReportAdapter());
    }

    // 🔥 LOAD FILES
    void loadReports() {
        try {
            File dir = getExternalFilesDir(null);
            File[] files = dir.listFiles();

            if (files == null) return;

            for (File f : files) {

                BufferedReader br = new BufferedReader(new FileReader(f));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                String data = sb.toString(); // full JSON
                JSONObject obj = new JSONObject(data);
                obj.put("file", f.getAbsolutePath());

                reports.add(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 CUSTOM LIST
    class ReportAdapter extends BaseAdapter {

        public int getCount() { return reports.size(); }
        public Object getItem(int i) { return reports.get(i); }
        public long getItemId(int i) { return i; }

        public View getView(int i, View v, ViewGroup parent) {

            JSONObject obj = reports.get(i);

            LinearLayout layout = new LinearLayout(SavedActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20,20,20,20);

            try {

                TextView info = new TextView(SavedActivity.this);
                info.setText(
                        "Name: " + obj.optString("name") + "\n" +
                                "Age: " + obj.optString("age") + "\n" +
                                "Date: " + obj.optString("date")
                );

                Button view = new Button(SavedActivity.this);
                view.setText("View Report");

                Button delete = new Button(SavedActivity.this);
                delete.setText("Delete");

                String path = obj.getString("file");

                // 🔥 VIEW REPORT (OPEN SAME FORM WITH DATA)
                view.setOnClickListener(v1 -> {
                    Intent intent = new Intent(SavedActivity.this, WebActivity.class);

                    // load correct page
                    String type = obj.optString("type");
                    if(type.contains("semen"))
                        intent.putExtra("file", "semen.html");
                    else
                        intent.putExtra("file", "urine.html");

                    intent.putExtra("filePath", path);
                    startActivity(intent);
                });

                // 🔥 DELETE
                delete.setOnClickListener(v12 -> {
                    new File(path).delete();
                    reports.remove(i);
                    notifyDataSetChanged();

                    Toast.makeText(SavedActivity.this,
                            "Deleted ✅", Toast.LENGTH_SHORT).show();
                });

                layout.addView(info);
                layout.addView(view);
                layout.addView(delete);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return layout;
        }
    }
}