package com.example.test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.*;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class WebActivity extends AppCompatActivity {

    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);

        webView.setWebChromeClient(new WebChromeClient()); // ALERTS now work
        webView.setWebViewClient(new WebViewClient());

        JSBridge bridge = new JSBridge();
        webView.addJavascriptInterface(bridge, "Android");

        String file = getIntent().getStringExtra("file");
        webView.loadUrl("file:///android_asset/" + file);

        // 🔥 Load saved report if filePath is provided
        webView.postDelayed(() -> {
            String path = getIntent().getStringExtra("filePath");
            if (path != null) {
                bridge.loadSavedData(path);
            }
        }, 500); // delay to ensure page has loaded
    }

    class JSBridge {

        // ✅ SAVE REPORT
        @JavascriptInterface
        public void saveData(String data) {
            try {
                File file = new File(
                        getExternalFilesDir(null),
                        "report_" + System.currentTimeMillis() + ".txt"
                );

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data.getBytes());
                fos.close();

                runOnUiThread(() ->
                        Toast.makeText(WebActivity.this,
                                "Saved Successfully ✅",
                                Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ✅ LOAD SAVED REPORT
        public void loadSavedData(String path) {
            try {
                if (path == null) return;

                BufferedReader br = new BufferedReader(new FileReader(path));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                String safeData = sb.toString()
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"");

                webView.post(() -> webView.evaluateJavascript(
                        "loadData(JSON.parse(\"" + safeData + "\"))",
                        null
                ));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ✅ PRINT OR SAVE AS PDF
        @JavascriptInterface
        public void printPDF() {
            runOnUiThread(() -> {
                PrintManager pm = (PrintManager) getSystemService(PRINT_SERVICE);

                pm.print(
                        "Lab_Report",
                        webView.createPrintDocumentAdapter("Report"),
                        new PrintAttributes.Builder().build()
                );

                Toast.makeText(WebActivity.this,
                        "Choose 'Save as PDF' or Printer 🖨️",
                        Toast.LENGTH_LONG).show();
            });
        }
    }
}