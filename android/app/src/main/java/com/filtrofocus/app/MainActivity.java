
package com.filtrofocus.app;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import org.json.JSONArray;
public class MainActivity extends Activity {
 WebView w; SharedPreferences prefs;
 public class Br {
  @JavascriptInterface public String getK(){ return prefs.getString("kw","[]"); }
  @JavascriptInterface public void setK(String j){ prefs.edit().putString("kw",j).apply(); }
  @JavascriptInterface public void add(String k){ try{ JSONArray a=new JSONArray(prefs.getString("kw","[]")); for(int i=0;i<a.length();i++) if(a.getString(i).equalsIgnoreCase(k)) return; a.put(k); prefs.edit().putString("kw",a.toString()).apply(); }catch(Exception e){} }
  @JavascriptInterface public void rem(String k){ try{ JSONArray a=new JSONArray(prefs.getString("kw","[]")); JSONArray n=new JSONArray(); for(int i=0;i<a.length();i++) if(!a.getString(i).equalsIgnoreCase(k)) n.put(a.getString(i)); prefs.edit().putString("kw",n.toString()).apply(); }catch(Exception e){} }
  @JavascriptInterface public boolean isOn(){ return prefs.getBoolean("on",false); }
  @JavascriptInterface public void toggleMaster(){ boolean on=!prefs.getBoolean("on",false); prefs.edit().putBoolean("on",on).apply(); }
  @JavascriptInterface public boolean hasAcc(){ String s=Settings.Secure.getString(getContentResolver(),Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES); return s!=null && s.contains(getPackageName()); }
  @JavascriptInterface public void reqAcc(){ startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)); }
  @JavascriptInterface public void testBlock(){ prefs.edit().putString("kw","[\"ANITTA\"]").putBoolean("on",true).apply(); Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=Anitta")); startActivity(i); }
 }
 @Override protected void onCreate(Bundle b){ super.onCreate(b); prefs=getSharedPreferences("ff",Context.MODE_PRIVATE); if(!prefs.contains("kw")) prefs.edit().putString("kw","[]").apply(); w=new WebView(this); WebSettings s=w.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); w.addJavascriptInterface(new Br(),"Android"); w.setWebViewClient(new WebViewClient(){ @Override public void onPageFinished(WebView v,String u){ try{ JSONArray arr=new JSONArray(prefs.getString("kw","[]")); if(arr.length()==0||!prefs.getBoolean("on",false)) return; StringBuilder sb=new StringBuilder("["); for(int i=0;i<arr.length();i++){ if(i>0) sb.append(","); sb.append("\"").append(arr.getString(i).toUpperCase()).append("\""); } sb.append("]"); String js="(function(){var B="+sb.toString()+";function bad(t){t=(t||'').toUpperCase();for(var i=0;i<B.length;i++)if(t.indexOf(B[i])!=-1)return true;return false;}function run(){document.querySelectorAll('div,article,li').forEach(function(el){try{var txt=(el.innerText||'').substring(0,250);if(el.children.length<=2&&bad(txt)&&txt.length>3){var c=el.closest('div');if(c&&c.offsetHeight>50){c.innerHTML='<div style=background:#ff0033;color:#fff;padding:12px;border-radius:10px;text-align:center>🚫 Bloqueado<br>'+txt.substring(0,40)+'</div>';}} }catch(e){}});}run();setInterval(run,800);})();"; v.evaluateJavascript(js,null);}catch(Exception e){}} }); w.loadUrl("file:///android_asset/www/index.html"); setContentView(w); }
 @Override public void onBackPressed(){ if(w.canGoBack()) w.goBack(); else super.onBackPressed(); }
}
