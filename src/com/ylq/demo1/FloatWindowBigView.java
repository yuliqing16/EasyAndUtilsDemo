package com.ylq.demo1;

import org.androidannotations.annotations.EViewGroup;

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
     * ��¼���������Ŀ�� 
     */
	public static int viewWidth;
	/** 
     * ��¼���������ĸ߶� 
     */ 
	public static int viewHeight;
	
	public FloatWindowBigView(final Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		
		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		
		Button close = (Button)findViewById(R.id.close);
		Button back = (Button)findViewById(R.id.back);
		
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                // ����ر���������ʱ���Ƴ���������������ֹͣService  
                MyWindowManager.removeBigWindow(context);  
                MyWindowManager.removeSmallWindow(context);  
                Intent intent = new Intent(getContext(), FloatWindowService_.class);  
                context.stopService(intent); 
			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                // ������ص�ʱ���Ƴ���������������С������  
                MyWindowManager.removeBigWindow(context);  
                MyWindowManager.createSmallWindow(context);  
			}
		});
	}
	

}
