package com.ylq.demo1;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.api.BackgroundExecutor;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

@EService
public class FloatWindowService extends Service {

	Handler myHandler = new Handler();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean hasStart = false;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (hasStart == false) 
		{
			hasStart = true;
			reflush();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		hasStart = false;
		BackgroundExecutor.cancelAll("Task", true);

		super.onDestroy();
	}
	
	@Background(id = "Task")
	void reflush()
	{
		while (true && hasStart) 
		{
			Log.d("[FM]", "Flush:" + System.currentTimeMillis());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
			if (isHome() && !MyWindowManager.isWindowShowing())
			{
				myHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (hasStart) {
							MyWindowManager.createSmallWindow(getApplicationContext());
						}
						else {
							MyWindowManager.removeSmallWindow(getApplicationContext());
							MyWindowManager.removeBigWindow(getApplicationContext());
						}
						
					}
				});
			}
			// 当前界面不是桌面，且有悬浮窗显示，则移除悬浮窗。
			else if (!isHome() && MyWindowManager.isWindowShowing())
			{
				myHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						MyWindowManager.removeSmallWindow(getApplicationContext());
						MyWindowManager.removeBigWindow(getApplicationContext());
					}
				});
			}
	          // 当前界面是桌面，且有悬浮窗显示，则更新内存数据。  
            else if (isHome() && MyWindowManager.isWindowShowing()) {  
            	myHandler.post(new Runnable() {  
                    @Override  
                    public void run() {  
                        MyWindowManager.updateUsedPercent(getApplicationContext());  
                    }  
                });  
            }  
			
		}
	}
	
	
	
	@SystemService
	ActivityManager mActivityManager;
	
    /** 
     * 判断当前界面是否是桌面 
     */  
	private boolean isHome()
	{
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		
		return getHome().contains(rti.get(0).topActivity.getPackageName());
	}
	
    /** 
     * 获得属于桌面的应用的应用包名称 
     *  
     * @return 返回包含所有包名的字符串列表 
     */
	private List<String> getHome()
	{
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		
		List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfos) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}
}
