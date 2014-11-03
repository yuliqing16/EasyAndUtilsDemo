package com.example.easyandutilsdemo;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

@EBean
public class EBeanTest {
	@RootContext
	Context context;
	
	@RootContext
	MainActivity activity;
	
	@ViewById
	TextView textView1;
	
	public void Toast()
	{
		android.widget.Toast.makeText(
				context, "在EBean重调用Toast", 
				Toast.LENGTH_LONG).show();
	}
	
	//后台线程执行
	@Background
	public void backThread()
	{
		for (int i = 0; i < 999; i++) 
		{
			try {
				Thread.sleep(1000);
				updateTv(i);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	@UiThread
	public void updateTv(int i)
	{
		textView1.setText("" + i);
	}
	
	@AfterInject
	public void doSomethingAfterInject()
	{
		System.out.println("EBean test AfterInject!!!");
	}
}
