
package com.filtrofocus.app;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
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
 @Override protected void onCreate(Bundle b){ super.onCreate(b); prefs=getSharedPreferences("ff",Context.MODE_PRIVATE); if(!prefs.contains("kw")) prefs.edit().putString("kw","[]").apply(); w=new WebView(this); WebSettings s=w.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); w.addJavascriptInterface(new Br(),"Android"); w.loadUrl("file:///android_asset/www/index.html"); setContentView(w); }
}
