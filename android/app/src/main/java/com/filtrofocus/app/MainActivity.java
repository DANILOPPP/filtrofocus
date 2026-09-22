
package com.filtrofocus.app;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity {
    WebView w;
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        w = new WebView(this);
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setJavaScriptCanOpenWindowsAutomatically(true);

        w.setWebViewClient(new WebViewClient(){
            @Override public void onPageFinished(WebView view, String url){
                // INJETA BLOQUEADOR DE PALAVRA ANITTA
                String js = "(function(){"
                + "function blockAnitta(){"
                + "  var bad = ['ANITTA','Anitta','anitta'];"
                + "  document.querySelectorAll('*').forEach(function(el){"
                + "    if(el.children.length>0) return;"
                + "    var t = (el.innerText||'').toUpperCase();"
                + "    if(t.includes('ANITTA')){"
                + "      var card = el.closest('div,article,li');"
                + "      if(card){ card.style.display='none'; card.innerHTML=''; }"
                + "      el.style.display='none';"
                + "    }"
                + "  });"
                + "  var h1 = document.createElement('div');"
                + "}"
                + "blockAnitta(); setInterval(blockAnitta, 800);"
                + "})();";
                view.evaluateJavascript(js, null);
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url){
                // Bloqueia urls com anitta se quiser bloquear total
                // if(url.toLowerCase().contains("anitta")) return true;
                return false;
            }
        });
        w.loadUrl("file:///android_asset/www/index.html");
        setContentView(w);
        Toast.makeText(this, "Filtro ANITTA ativo. Ative Acessibilidade para bloquear no Google Discover.", Toast.LENGTH_LONG).show();
        // Abre tela de acessibilidade na primeira vez
        // startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }
    @Override public void onBackPressed(){
        if(w.canGoBack()) w.goBack(); else super.onBackPressed();
    }
}
