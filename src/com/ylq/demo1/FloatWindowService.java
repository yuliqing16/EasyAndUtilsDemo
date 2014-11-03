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
			// ��ǰ���������棬��û����������ʾ���򴴽���������
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
			// ��ǰ���治�����棬������������ʾ�����Ƴ���������
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
	          // ��ǰ���������棬������������ʾ��������ڴ����ݡ�  
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
     * �жϵ�ǰ�����Ƿ������� 
     */  
	private boolean isHome()
	{
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		
		return getHome().contains(rti.get(0).topActivity.getPackageName());
	}
	
    /** 
     * ������������Ӧ�õ�Ӧ�ð����� 
     *  
     * @return ���ذ������а������ַ����б� 
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
