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
	 * ��¼С�������Ŀ��
	 */
	public static int viewWidth;
	
	/**
	 * ��¼С�������ĸ߶�
	 */
	public static int viewHeight;
	
	/**
	 * ��¼ϵͳ״̬���߶�
	 * 
	 */
	private static int statusBarHeight;

	/** 
     * ���ڸ���С��������λ�� 
     */
	@SystemService
	WindowManager windowManager;
	
	/** 
     * С�������Ĳ��� 
     */  
	private WindowManager.LayoutParams mParams;
	/** 
     * ��¼��ǰ��ָλ������Ļ�ϵĺ�����ֵ 
     */
	private float xInScreen;
	/** 
     * ��¼��ǰ��ָλ������Ļ�ϵ�������ֵ 
     */
	private float yInScreen;
	/** 
     * ��¼��ָ����ʱ����Ļ�ϵĺ������ֵ 
     */ 
	private float xDownInScreen;
	/** 
     * ��¼��ָ����ʱ����Ļ�ϵ��������ֵ 
     */  
	private float yDownInScreen;
	/** 
     * ��¼��ָ����ʱ��С��������View�ϵĺ������ֵ 
     */  
	private float xInView;
	/** 
     * ��¼��ָ����ʱ��С��������View�ϵ��������ֵ 
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
			// ��ָ�ƶ���ʱ�����С��������λ��
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			// �����ָ�뿪��Ļʱ��xDownInScreen��xInScreen��ȣ���yDownInScreen��yInScreen��ȣ�����Ϊ�����˵����¼���
			Log.d("Tag", "xd,yd:(" + xDownInScreen + "," + yDownInScreen +"),xi,yi:(" + xInScreen + yInScreen + ")");
			if (xDownInScreen == xInScreen && yDownInScreen == yInScreen)
			{
				openBigWindow();
			}
		}
		return true;
	}
	
	/**
	 * ��С�������Ĳ������룬���ڸ���С��������λ��
	 * @param params С�������Ĳ���
	 */
	public void setParams(WindowManager.LayoutParams params)
	{
		mParams = params;
	}
	
	/**
	 * ����С����������Ļ�е�λ�á�
	 */
	private void updateViewPosition()
	{
		mParams.x = (int)(xInScreen - xInView);
		mParams.y = (int)(yInScreen - yInView);
		windowManager.updateViewLayout(this, mParams);
	}
	
	/**
	 * �򿪴���������ͬʱ�ر�С��������
	 */
	private void openBigWindow()
	{
		MyWindowManager.createBigWindow(getContext());  
        MyWindowManager.removeSmallWindow(getContext());  
	}
	
	
	/**
	 * ���ڻ�ȡ״̬���ĸ߶�
	 * 
	 * @return ����״̬���߶ȵ�����ֵ
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
