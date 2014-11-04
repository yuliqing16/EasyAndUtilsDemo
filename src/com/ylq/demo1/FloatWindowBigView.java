package com.ylq.demo1;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;

import com.example.easyandutilsdemo.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

@EViewGroup(R.layout.float_window_big)
public class FloatWindowBigView extends LinearLayout{
	/** 
     * 记录大悬浮窗的宽度 
     */
	public static int viewWidth;
	/** 
     * 记录大悬浮窗的高度 
     */ 
	public static int viewHeight;
	
	public Context mContext;
	
	@ViewById(R.id.big_window_layout)
	LinearLayout view;
	
	@ViewById
	Button close;
	
	@Click(R.id.close)
	void closeClick()
	{
        MyWindowManager.removeBigWindow(mContext);  
        MyWindowManager.removeSmallWindow(mContext);  
        Intent intent = new Intent(getContext(), FloatWindowService_.class);  
        mContext.stopService(intent); 
	}
	
	@Click(R.id.back)
	void backClick()
	{
        // 点击返回的时候，移除大悬浮窗，创建小悬浮窗  
        MyWindowManager.removeBigWindow(mContext);  
        MyWindowManager.createSmallWindow(mContext);  
	}
	
	@ViewById
	Button back;
	
	@AfterViews
	void AfterView()
	{
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
	}
	
	
	public FloatWindowBigView(final Context context) {
		super(context);
		
		mContext = context;
	}
	

}
