
package com.filtrofocus.app;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.SharedPreferences;
import org.json.JSONArray;

public class AnittaBlockerService extends AccessibilityService {
    @Override public void onAccessibilityEvent(AccessibilityEvent event){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root==null) return;
        SharedPreferences prefs = getSharedPreferences("filtrofocus", MODE_PRIVATE);
        String json = prefs.getString("keywords", "[\"ANITTA\"]");
        try{
            JSONArray arr = new JSONArray(json);
            checkNode(root, arr);
        }catch(Exception e){}
    }
    private void checkNode(AccessibilityNodeInfo node, JSONArray blocked){
        if(node==null) return;
        try{
            CharSequence txt = node.getText();
            if(txt!=null){
                String t = txt.toString().toUpperCase();
                for(int i=0;i<blocked.length();i++){
                    String kw = blocked.getString(i).toUpperCase();
                    if(t.contains(kw) && t.length()>2){
                        // Futuro: esconder ou voltar. Por enquanto só detecta.
                        // Poderia adicionar overlay, mas para V13 focamos no WebView.
                        break;
                    }
                }
            }
        }catch(Exception e){}
        for(int i=0;i<node.getChildCount();i++){
            AccessibilityNodeInfo child = node.getChild(i);
            if(child!=null){ checkNode(child, blocked); child.recycle(); }
        }
    }
    @Override public void onInterrupt(){}
}
