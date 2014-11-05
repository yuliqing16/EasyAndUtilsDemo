package com.example.easyandutilsdemo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import com.ylq.demo1.FloatWindowService_;
import com.ylq.demo2.Demo2Activity_;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

	/*
	@ViewById(R.id.textView1)
	@FromHtml(R.string.hello_html)
	TextView textView1;
	
	@ViewById
	Button button1;
	
	@ViewById
	EditText editText1;
	
	@AfterTextChange(R.id.editText1)
	void afterEditChange(Editable able, TextView hello)
	{
		textView1.setText(hello.getText());
	}
	
	@Click(R.id.button1)
	void proj()
	{
		
		//MainActivity2_.intent(this).start();
		
		bTest.Toast();
		bTest.backThread();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TimeUtils my = new TimeUtils();
		my.SayHello();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Bean
	EBeanTest bTest;
	
	//AfterViews注释定义的方法会在OnCreate方法的setContentView后执行
	@AfterViews
	void init()
	{
		textView1.setText("MainActivity AfterViews");
	}*/
	
	@ViewById
	ListView list;
	
	String [] buttons ={"1.360手机卫士悬浮窗效果", 
						"1.模仿人人侧滑"}; 
	
	@ItemClick(R.id.list)
	public void click(int position)
	{
		switch (position + 1) {
		case 1:
		{
			FloatWindowService_.intent(this).start();
		}
		break;
		case 2:
		{
			Demo2Activity_.intent(this).start();
		}
		break;
		default:
			break;
		}
	}
	
	@AfterViews
	void init()
	{
		list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,buttons));
	}
	
	

}
