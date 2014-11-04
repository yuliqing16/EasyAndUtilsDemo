package com.ylq.demo1;

import java.lang.reflect.Field;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.easyandutilsdemo.R;

@EViewGroup(R.layout.float_window_small)
public class FloatWindowSmallView extends LinearLayout {

	/**
	 * 纪录小悬浮窗的宽度
	 */
	public static int viewWidth;
	
	/**
	 * 纪录小悬浮窗的高度
	 */
	public static int viewHeight;
	
	/**
	 * 纪录系统状态栏高度
	 * 
	 */
	private static int statusBarHeight;

	/** 
     * 用于更新小悬浮窗的位置 
     */
	@SystemService
	WindowManager windowManager;
	
	/** 
     * 小悬浮窗的参数 
     */  
	private WindowManager.LayoutParams mParams;
	/** 
     * 记录当前手指位置在屏幕上的横坐标值 
     */
	private float xInScreen;
	/** 
     * 记录当前手指位置在屏幕上的纵坐标值 
     */
	private float yInScreen;
	/** 
     * 记录手指按下时在屏幕上的横坐标的值 
     */ 
	private float xDownInScreen;
	/** 
     * 记录手指按下时在屏幕上的纵坐标的值 
     */  
	private float yDownInScreen;
	/** 
     * 记录手指按下时在小悬浮窗的View上的横坐标的值 
     */  
	private float xInView;
	/** 
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值 
     */  
	private float yInView;
	
	@ViewById(R.id.small_window_layout)
	LinearLayout view;
	
	@AfterViews
	void init()
	{
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		percentView.setText("");
	}

	@ViewById(R.id.percent)
	TextView percentView;
	public FloatWindowSmallView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY() - getStatusBarHeight();
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			break;

		case MotionEvent.ACTION_MOVE:
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			// 手指移动的时候更新小悬浮窗的位置
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			// 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
			Log.d("Tag", "xd,yd:(" + xDownInScreen + "," + yDownInScreen +"),xi,yi:(" + xInScreen + yInScreen + ")");
			if (xDownInScreen == xInScreen && yDownInScreen == yInScreen)
			{
				openBigWindow();
			}
		}
		return true;
	}
	
	/**
	 * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置
	 * @param params 小悬浮窗的参数
	 */
	public void setParams(WindowManager.LayoutParams params)
	{
		mParams = params;
	}
	
	/**
	 * 更新小悬浮窗在屏幕中的位置。
	 */
	private void updateViewPosition()
	{
		mParams.x = (int)(xInScreen - xInView);
		mParams.y = (int)(yInScreen - yInView);
		windowManager.updateViewLayout(this, mParams);
	}
	
	/**
	 * 打开大悬浮窗，同时关闭小悬浮窗。
	 */
	private void openBigWindow()
	{
		MyWindowManager.createBigWindow(getContext());  
        MyWindowManager.removeSmallWindow(getContext());  
	}
	
	
	/**
	 * 用于获取状态栏的高度
	 * 
	 * @return 返回状态栏高度的像素值
	 */
	
	private int getStatusBarHeight()
	{
		if (statusBarHeight == 0) {
			try {
				Class<?> class1 = Class.forName("com.android.internal.R$dimen");
				Object o = class1.newInstance();
				Field field = class1.getField("status_bar_height");
				int x = (Integer)field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}
}
