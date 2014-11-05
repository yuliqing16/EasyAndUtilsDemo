package com.ylq.demo2;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.easyandutilsdemo.R;

@EActivity(R.layout.demo2_activity)
public class Demo2Activity extends Activity implements OnTouchListener{

    /** 
     * 滚动显示和隐藏menu时，手指滑动需要达到的速度。 
     */  
	public static final int SNAP_VELOCITY = 200;
	
    /** 
     * 屏幕宽度值。 
     */  
	private int screenWidth;
    /** 
     * menu最多可以滑动到的左边缘。值由menu布局的宽度来定，marginLeft到达此值之后，不能再减少。 
     */  
	private int leftEdge;
    /** 
     * menu最多可以滑动到的右边缘。值恒为0，即marginLeft到达0之后，不能增加。 
     */  
	private int rightEdge = 0;
    /** 
     * menu完全显示时，留给content的宽度值。 
     */ 
	private int menuPadding = 80;
	
	/**
	 * 菜单布局
	 */
	@ViewById(R.id.menu)
	LinearLayout menu;
	/**
	 * 内容布局
	 */
	@ViewById(R.id.content)
	View content;
    /** 
     * menu布局的参数，通过此参数来更改leftMargin的值。 
     */ 
	private LinearLayout.LayoutParams menuParams;
	
	/**
	 * 纪录手指按下时的横坐标
	 */
	private float xDown;
	/**
	 * 纪录手指移动时的横坐标
	 */
	private float xMove;
	/**
	 * 纪录手指抬起时的横坐标。
	 */
	private float xUp;
	
	/**
	 * menu当前是显示还是隐藏，只有完全显示或隐藏menu时才会更改此值，活动过程中此值无效
	 */
	private boolean isMenuVisible;
	
	/**
	 * 用于计算手指的滑动的速度
	 */
	private VelocityTracker mVelocityTracker;
	
	@SystemService
	WindowManager mWindowManager;
	
	@AfterViews
	void init()
	{
		screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		menuParams = (LinearLayout.LayoutParams) menu.getLayoutParams();
		// 将menu的宽度设置为屏幕宽度减去menuPadding  
		menuParams.width = screenWidth - menuPadding;
		// 左边缘的值赋值为menu宽度的负数 
		leftEdge = -menuParams.width;
		// menu的leftMargin设置为左边缘的值，这样初始化时menu就变为不可见  
		menuParams.leftMargin = leftEdge;
		// 将contentde宽度设置为屏幕宽度
		content.getLayoutParams().width = screenWidth;
		content.setOnTouchListener(this);
		

		
	}
	
    /** 
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。 
     *  
     * @param event 
     *            content界面的滑动事件 
     */ 
	private void createVelocityTracker(MotionEvent event)
	{
		if (mVelocityTracker == null) 
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//手指按下时，纪录按下时的横坐标
			xDown = event.getRawX();
			break;
			
		case MotionEvent.ACTION_MOVE:
			// 手指移动时，对比按下时的横坐标，计算出移动的距离，来调整menu的leftMargin值，从而显示和隐藏menu  
			xMove = event.getRawX();
			int distanceX = (int)(xMove - xDown);
			
			if (isMenuVisible) {
				menuParams.leftMargin = distanceX;
			}
			else {
				menuParams.leftMargin = leftEdge + distanceX;
			}
			
			if (menuParams.leftMargin < leftEdge) {
				menuParams.leftMargin = leftEdge;
			}
			else if (menuParams.leftMargin > rightEdge){
				menuParams.leftMargin = rightEdge;
			}
			
			menu.setLayoutParams(menuParams);
			break;
		case MotionEvent.ACTION_UP:
			xUp = event.getRawX();
			if (wantToShowMenu()) {
				if (shouldScrollToMenu()) {
					scrollToMenu();
				}
				else {
					scrollToContent();
				}
			}
			else if (wantToShowContent()){
				if (shouldScrollToContent()) {
					scrollToContent();
				}
				else {
					scrollToMenu();
				}
			}
			//TODO
			recycleVelocityTracker();
			break;
		default:
			break;
		}
		return true;
	}

    /** 
     * 判断当前手势的意图是不是想显示content。如果手指移动的距离是负数，且当前menu是可见的，则认为当前手势是想要显示content。 
     *  
     * @return 当前手势想显示content返回true，否则返回false。 
     */  
	private boolean wantToShowContent()
	{
		return xUp - xDown < 0 && isMenuVisible;
	}
	
    /** 
     * 判断当前手势的意图是不是想显示menu。如果手指移动的距离是正数，且当前menu是不可见的，则认为当前手势是想要显示menu。 
     *  
     * @return 当前手势想显示menu返回true，否则返回false。 
     */ 
	private boolean wantToShowMenu()
	{
		return xUp - xDown > 0 && !isMenuVisible;
	}
    /** 
     * 判断是否应该滚动将menu展示出来。如果手指移动距离大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY， 
     * 就认为应该滚动将menu展示出来。 
     *  
     * @return 如果应该滚动将menu展示出来返回true，否则返回false。 
     */ 
	private boolean shouldScrollToMenu()
	{
		return xUp - xDown > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
	}
	
	 /** 
     * 判断是否应该滚动将content展示出来。如果手指移动距离加上menuPadding大于屏幕的1/2， 
     * 或者手指移动速度大于SNAP_VELOCITY， 就认为应该滚动将content展示出来。 
     *  
     * @return 如果应该滚动将content展示出来返回true，否则返回false。 
     */  
	private boolean shouldScrollToContent()
	{
		return xDown - xUp + menuPadding > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
	}
    /** 
     * 将屏幕滚动到menu界面，滚动速度设定为30. 
     */  
	private void scrollToMenu()
	{
		slid(25);
	}
    /** 
     * 将屏幕滚动到content界面，滚动速度设定为-30. 
     */  
    private void scrollToContent() 
    {  
    	slid(-25);
    }  
    
    /** 
     * 获取手指在content界面滑动的速度。 
     *  
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。 
     */  
    private int getScrollVelocity()
    {
    	mVelocityTracker.computeCurrentVelocity(1000);
    	int velocity = (int)mVelocityTracker.getXVelocity();
    	return Math.abs(velocity);
    }
    
    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker()
    {
    	mVelocityTracker.recycle();
    	mVelocityTracker = null;
    }
    
    @Background
    void slid(int speed)
    {
    	int leftMargin = menuParams.leftMargin;
		// 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。  
		while (true)
		{
			leftMargin = leftMargin + speed;
			if (leftMargin > rightEdge) 
			{
				menuParams.leftMargin = rightEdge;
				break;
			}
			
			if (leftMargin < leftEdge) 
			{
				menuParams.leftMargin = leftEdge;
				break;
			}
			
			menuParams.leftMargin = leftMargin;
			SetUi();
			// 为了要有滚动效果产生，每次循环使线程睡眠20毫秒，这样肉眼才能够看到滚动动画。
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if (speed > 0) {
			isMenuVisible = true;
		}
		else {
			isMenuVisible = false;
		}
		
		SetUi();
    }
    
    @UiThread
    void SetUi()
    {
    	menu.setLayoutParams(menuParams);
    }
	
}
