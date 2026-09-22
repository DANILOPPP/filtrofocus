
package com.filtrofocus.app;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.View;
import android.widget.TextView;
import android.graphics.Color;
import org.json.JSONArray;
import java.util.HashMap;

public class RealBlockService extends AccessibilityService {
    Handler h = new Handler(Looper.getMainLooper());
    WindowManager wm;
    HashMap<String, View> overlays = new HashMap<>();
    long lastCleanup = 0;

    @Override
    public void onServiceConnected() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        if (e.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && e.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return;
        SharedPreferences p = getSharedPreferences("ff", MODE_PRIVATE);
        if (!p.getBoolean("on", false)) return;
        try {
            JSONArray arr = new JSONArray(p.getString("kw", "[]"));
            if (arr.length() == 0) return;
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if (root == null) return;
            String pkg = e.getPackageName() != null ? e.getPackageName().toString() : "";
            if (pkg.contains("com.filtrofocus.app")) return;
            if (pkg.contains("com.android.systemui")) return;
            if (System.currentTimeMillis() - lastCleanup > 3000) {
                cleanupOverlays();
                lastCleanup = System.currentTimeMillis();
            }
            findAndBlockContent(root, arr, pkg);
        } catch (Exception ex) {}
    }

    void findAndBlockContent(AccessibilityNodeInfo node, JSONArray blocked, String pkg) {
        if (node == null) return;
        try {
            CharSequence txt = node.getText();
            CharSequence desc = node.getContentDescription();
            String fullText = "";
            if (txt != null) fullText += txt.toString() + " ";
            if (desc != null) fullText += desc.toString();
            String upper = fullText.toUpperCase().trim();
            // NAO BLOQUEIA anotacoes
            if (upper.contains("VOCE BLOQUEOU") || upper.contains("FILTROFOCUS") || upper.contains("BLOQUEIO ATIVADO") || upper.contains("LISTA VAZIA") || upper.startsWith("BLOQUEAR PALAVRA") || upper.length() < 2) {
            } else if (upper.length() > 1 && upper.length() < 500) {
                for (int i = 0; i < blocked.length(); i++) {
                    String keyword = blocked.getString(i).toUpperCase().trim();
                    if (keyword.length() > 1 && upper.contains(keyword)) {
                        Rect bounds = new Rect();
                        node.getBoundsInScreen(bounds);
                        if (bounds.width() > 20 && bounds.height() > 20 && bounds.width() < 1000 && bounds.height() < 800) {
                            if (bounds.top > 100) { 
                                blockNodeContent(node, bounds, blocked.getString(i));
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception ex) {}
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                findAndBlockContent(child, blocked, pkg);
                child.recycle();
            }
        }
    }

    void blockNodeContent(AccessibilityNodeInfo node, Rect bounds, String keyword) {
        try {
            String key = bounds.toString();
            if (overlays.containsKey(key)) return;
            TextView overlay = new TextView(this);
            overlay.setText("BLOQUEADO");
            overlay.setBackgroundColor(Color.parseColor("#CCFF0033"));
            overlay.setTextColor(Color.WHITE);
            overlay.setTextSize(10);
            overlay.setPadding(8, 4, 8, 4);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                bounds.width(),
                bounds.height(),
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            );
            params.x = bounds.left;
            params.y = bounds.top;
            try {
                wm.addView(overlay, params);
                overlays.put(key, overlay);
                h.postDelayed(() -> {
                    try {
                        wm.removeView(overlay);
                        overlays.remove(key);
                    } catch (Exception ex) {}
                }, 2500);
            } catch (Exception ex) {}
        } catch (Exception ex) {}
    }

    void cleanupOverlays() {
        try {
            for (View v : overlays.values()) {
                try { wm.removeView(v); } catch (Exception ex) {}
            }
            overlays.clear();
        } catch (Exception ex) {}
    }

    @Override
    public void onInterrupt() {
        cleanupOverlays();
    }
}
