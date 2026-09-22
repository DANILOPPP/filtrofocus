
package com.filtrofocus.app;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        WebView w=new WebView(this);
        WebSettings s=w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        w.setWebViewClient(new WebViewClient());
        w.loadUrl("file:///android_asset/www/index.html");
        setContentView(w);
    }
}
