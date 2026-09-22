
package com.filtrofocus.app;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends Activity {
    WebView w;
    SharedPreferences prefs;

    public class JsBridge {
        @JavascriptInterface
        public String getKeywordsJson(){
            String json = prefs.getString("keywords", "[\"ANITTA\"]");
            return json;
        }
        @JavascriptInterface
        public void setKeywordsJson(String json){
            prefs.edit().putString("keywords", json).apply();
        }
        @JavascriptInterface
        public void addKeyword(String kw){
            try{
                String json = prefs.getString("keywords", "[\"ANITTA\"]");
                JSONArray arr = new JSONArray(json);
                // evita duplicado
                for(int i=0;i<arr.length();i++){ if(arr.getString(i).equalsIgnoreCase(kw)) return; }
                arr.put(kw);
                prefs.edit().putString("keywords", arr.toString()).apply();
            }catch(Exception e){}
        }
        @JavascriptInterface
        public void removeKeyword(String kw){
            try{
                String json = prefs.getString("keywords", "[\"ANITTA\"]");
                JSONArray arr = new JSONArray(json);
                JSONArray novo = new JSONArray();
                for(int i=0;i<arr.length();i++){
                    if(!arr.getString(i).equalsIgnoreCase(kw)) novo.put(arr.getString(i));
                }
                prefs.edit().putString("keywords", novo.toString()).apply();
            }catch(Exception e){}
        }
    }

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        prefs = getSharedPreferences("filtrofocus", Context.MODE_PRIVATE);
        if(!prefs.contains("keywords")){
            prefs.edit().putString("keywords", "[\"ANITTA\"]").apply();
        }

        w = new WebView(this);
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        w.addJavascriptInterface(new JsBridge(), "Android");

        w.setWebViewClient(new WebViewClient(){
            @Override public void onPageFinished(WebView view, String url){
                try{
                    String json = prefs.getString("keywords", "[\"ANITTA\"]");
                    JSONArray arr = new JSONArray(json);
                    StringBuilder jsArray = new StringBuilder("[");
                    for(int i=0;i<arr.length();i++){
                        if(i>0) jsArray.append(",");
                        jsArray.append("\"").append(arr.getString(i).replace("\"","").toUpperCase()).append("\"");
                    }
                    jsArray.append("]");
                    String js = "(function(){"
                    + "var BLOCK="+jsArray.toString()+";"
                    + "function shouldBlock(t){"
                    + "  t=(t||'').toUpperCase();"
                    + "  for(var i=0;i<BLOCK.length;i++){ if(t.indexOf(BLOCK[i])!=-1) return true; }"
                    + "  return false;"
                    + "}"
                    + "function blockNow(){"
                    + "  document.querySelectorAll('div,article,li,a').forEach(function(el){"
                    + "    try{"
                    + "      var txt=(el.innerText||'').substring(0,300);"
                    + "      if(el.children.length<=2 && shouldBlock(txt) && txt.length>3){"
                    + "        var card=el.closest('div');"
                    + "        if(card && card.offsetHeight>60){ card.style.display='none'; }"
                    + "      }"
                    + "    }catch(e){}"
                    + "  });"
                    + "}"
                    + "blockNow(); setInterval(blockNow, 1000);"
                    + "})();";
                    view.evaluateJavascript(js, null);
                }catch(Exception e){}
            }
        });
        w.loadUrl("file:///android_asset/www/index.html");
        setContentView(w);
    }
    @Override public void onBackPressed(){ if(w.canGoBack()) w.goBack(); else super.onBackPressed(); }
}
