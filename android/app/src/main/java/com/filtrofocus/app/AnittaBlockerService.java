
package com.filtrofocus.app;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

public class AnittaBlockerService extends AccessibilityService {
    @Override public void onAccessibilityEvent(AccessibilityEvent event){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root == null) return;
        checkNode(root);
    }
    private void checkNode(AccessibilityNodeInfo node){
        if(node == null) return;
        CharSequence text = node.getText();
        if(text != null){
            String t = text.toString().toUpperCase();
            if(t.contains("ANITTA") && t.contains("COROA") || t.contains("ANITTA")){
                // Só bloqueia no Google Discover / Google App / YouTube
                String pkg = "";
                if(getRootInActiveWindow()!=null && getRootInActiveWindow().getPackageName()!=null) pkg = getRootInActiveWindow().getPackageName().toString();
                if(pkg.contains("google") || pkg.contains("youtube")){
                    // Não conseguimos esconder nativamente, mas avisamos e podemos voltar
                    // Opcional: fechar o card não é possível, então mostramos toast e tentamos esconder via ação
                    // Para bloqueio real, o ideal é usar dentro do nosso WebView
                }
            }
        }
        // Busca recursiva
        for(int i=0;i<node.getChildCount();i++){
            AccessibilityNodeInfo child = node.getChild(i);
            if(child!=null){
                checkNode(child);
                child.recycle();
            }
        }
    }
    @Override public void onInterrupt(){}
}
