
package com.filtrofocus.app;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
public class RealBlockService extends AccessibilityService {
 Handler h=new Handler(Looper.getMainLooper()); long last=0;
 @Override public void onAccessibilityEvent(AccessibilityEvent e){
  if(e.getEventType()!=AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && e.getEventType()!=AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return;
  SharedPreferences p=getSharedPreferences("ff",MODE_PRIVATE);
  if(!p.getBoolean("on",false)) return;
  try{ JSONArray arr=new JSONArray(p.getString("kw","[]")); if(arr.length()==0) return; AccessibilityNodeInfo root=getRootInActiveWindow(); if(root==null) return; String pkg=e.getPackageName()!=null?e.getPackageName().toString():""; if(pkg.contains("com.filtrofocus.app")) return; if(System.currentTimeMillis()-last<1500) return; if(check(root,arr)){ last=System.currentTimeMillis(); String f=find(root,arr); performGlobalAction(GLOBAL_ACTION_BACK); h.postDelayed(()->{ try{ Intent it=new Intent(RealBlockService.this, BlockActivity.class); it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP); it.putExtra("word",f); startActivity(it);}catch(Exception ex){} },200);} }catch(Exception ex){}
 }
 boolean check(AccessibilityNodeInfo n, JSONArray b){ if(n==null) return false; try{ CharSequence t=n.getText(); if(t!=null){ String s=t.toString().toUpperCase(); if(s.length()>1 && s.length()<400) for(int i=0;i<b.length();i++){ String k=b.getString(i).toUpperCase().trim(); if(k.length()>1 && s.contains(k)) return true; } } }catch(Exception e){} for(int i=0;i<n.getChildCount();i++){ AccessibilityNodeInfo c=n.getChild(i); if(c!=null){ boolean r=check(c,b); c.recycle(); if(r) return true; } } return false; }
 String find(AccessibilityNodeInfo n, JSONArray b){ if(n==null) return ""; try{ CharSequence t=n.getText(); if(t!=null){ String s=t.toString().toUpperCase(); for(int i=0;i<b.length();i++){ String k=b.getString(i); if(s.contains(k.toUpperCase())) return k; } } }catch(Exception e){} for(int i=0;i<n.getChildCount();i++){ AccessibilityNodeInfo c=n.getChild(i); if(c!=null){ String r=find(c,b); c.recycle(); if(!r.isEmpty()) return r; } } return ""; }
 @Override public void onInterrupt(){}
}
