package com.ylq.demo3;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.example.easyandutilsdemo.R;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

@EActivity(R.layout.demo3_activity)
public class Demo3Activity extends Activity {
	
	@ViewById(R.id.slidingLayout)
	SlidingLayout slidingLayout;
	
	@ViewById(R.id.menuButton)
	Button menuButton;
	
	@ViewById(R.id.contentList)
	ListView contentListView;
	
	ArrayAdapter<String> contentListAdapter;
	
	String[] contentItem = {
			"Content Item 1", "Content Item 2", "Content Item 3",  
            "Content Item 4", "Content Item 5", "Content Item 6", "Content Item 7",  
            "Content Item 8", "Content Item 9", "Content Item 10", "Content Item 11",  
            "Content Item 12", "Content Item 13", "Content Item 14", "Content Item 15",  
            "Content Item 16" 
	};
	
	
	@AfterViews
	void init()
	{
		contentListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				contentItem);
		contentListView.setAdapter(contentListAdapter);
		
		slidingLayout.setScrollEvent(contentListView);
	}
	
	@Click(R.id.menuButton)
	void menuBtClick()
	{
		if (slidingLayout.isLeftLayoutVisibe()) {
			slidingLayout.scrollToRightLayuot();
		}else {
			slidingLayout.scrollToLeftLayout();
		}
	}	
}
