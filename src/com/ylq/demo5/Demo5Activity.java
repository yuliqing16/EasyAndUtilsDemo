package com.ylq.demo5;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.example.easyandutilsdemo.R;

import android.R.integer;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AlphabetIndexer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@EActivity(R.layout.demo5_activity)
public class Demo5Activity extends Activity {
	
	/**
	 * 分组的布局
	 */
	@ViewById(R.id.title_layout)
	LinearLayout titleLayout;
	/**
	 * 分组上显示的字母
	 */
	@ViewById(R.id.title)
	TextView title;
	/**
	 * 联系人ListView
	 */
	@ViewById(R.id.contacts_list_view)
	ListView contactListView;
	
	/**
	 * 联系人列表适配器
	 */
	private ContactAdapter adapter;
	
	/**
	 * 用于进行字母表分组
	 */
	private AlphabetIndexer indexer;
	/**
	 * 存储所有手机中的联系人
	 */
	private List<Contact> contacts = new ArrayList<Contact>();
	/**
	 * 定义字母表的排序规则
	 */
	private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	/**
	 * 上次第一个可见元素，用于滚动时记录标识
	 */
	private int lastFirstVisibleItem = -1;
	

	@AfterViews
	void init()
	{
		adapter = new ContactAdapter(this, R.layout.demo5_contact_item, contacts);
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri,
				new String[]{"display_name", "sort_key"},
				null, null, "sort_key");
		
		if (cursor.moveToFirst()) 
		{
			do {
				String name = cursor.getString(0);
				String sortKey = getSortKey(cursor.getString(1));
				Contact contact = new Contact();
				contact.setName(name);
				contact.setSortKey(sortKey);
				contacts.add(contact);
			} while (cursor.moveToNext());
		}
		
		startManagingCursor(cursor);
		indexer = new AlphabetIndexer(cursor, 1, alphabet);
		adapter.setIndexer(indexer);
		if (contacts.size() > 0) {
			setupContactsListView();
		}
	}
	
	private void setupContactsListView()
	{
		contactListView.setAdapter(adapter);
		contactListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				int section = indexer.getSectionForPosition(firstVisibleItem);  
                int nextSecPosition = indexer.getPositionForSection(section + 1);  
                if (firstVisibleItem != lastFirstVisibleItem) {  
                    MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();  
                    params.topMargin = 0;  
                    titleLayout.setLayoutParams(params);  
                    title.setText(String.valueOf(alphabet.charAt(section)));  
                }  
                if (nextSecPosition == firstVisibleItem + 1) {  
                    View childView = view.getChildAt(0);  
                    if (childView != null) {  
                        int titleHeight = titleLayout.getHeight();  
                        int bottom = childView.getBottom();  
                        MarginLayoutParams params = (MarginLayoutParams) titleLayout  
                                .getLayoutParams();  
                        if (bottom < titleHeight) {  
                            float pushedDistance = bottom - titleHeight;  
                            params.topMargin = (int) pushedDistance;  
                            titleLayout.setLayoutParams(params);  
                        } else {  
                            if (params.topMargin != 0) {  
                                params.topMargin = 0;  
                                titleLayout.setLayoutParams(params);  
                            }  
                        }  
                    }  
                }  
                lastFirstVisibleItem = firstVisibleItem;  
			}
		});
	}
	
    /** 
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。 
     *  
     * @param sortKeyString 
     *            数据库中读取出的sort key 
     * @return 英文字母或者# 
     */  
    private String getSortKey(String sortKeyString) {  
        String key = sortKeyString.substring(0, 1).toUpperCase();  
        if (key.matches("[A-Z]")) {  
            return key;  
        }  
        return "#";  
    }  
	
	
}
