package com.ylq.demo4;

import java.util.Timer;
import java.util.TimerTask;

import com.example.easyandutilsdemo.R;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SlidingSwitchView 
extends RelativeLayout implements OnTouchListener
{

	/**
	 * 让菜单滚动，手指滑动需要达到的速度
	 */
	public static final int SNAP_VELOCITY = 200;
	/**
	 * SlidingSwitchView
	 */
	private int switcherViewWidth;
	
	/**
	 * 当前显示的元素下标
	 */
	private int currentItemIdex;
	/**
	 * 菜单包含的元素总数
	 */
	private int itemsCount;
	/**
	 * 
	 * 各个元素的偏移边界值
	 */
	private int []borders;
	
	/**
	 * 最多可以滑动到的左边缘，值由菜单中包含的元素总数来定，
	 * marginLeft到达此值之后，不能再减少。
	 */
	private int leftEdge = 0;
	/**
	 * 最多可以滑动到到右边缘，值恒为0，margingLeft到达此值之后，不能再增加。
	 */
	private int rightEdge = 0;
	/**
	 * 纪录手指移动时到横坐标。
	 */
	private float xDown;
	/**
	 * 纪录手指按下时到横坐标。
	 */
	private float xMove;
	/**
	 * 纪录手机抬起时的横坐标。
	 */
	private float xUp;
	/**
	 * 菜单布局
	 */
	private LinearLayout itemsLayouot;
	/**
	 * 标签布局
	 */
	private LinearLayout dotsLayout;
	/**
	 * 菜单中的第一个元素
	 */
	private View firstItem;
	/**
	 * 菜单中的第一个元素的布局，用于改变leftMargin的值，来决定当前显示的哪一个元素
	 */
	private MarginLayoutParams firstItemParams;
	/**
	 * 用于计算手指滑动的速度。
	 */
	private VelocityTracker mVelocityTracker;
	
	/**
	 * 重写构造函数，用于允许在XML中引用当前的自定义布局
	 * @param context
	 * @param attrs
	 */
	public SlidingSwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingSwitcherView);
		boolean isAutoPlay = a.getBoolean(R.styleable.SlidingSwitcherView_auto_play, false);
		if (isAutoPlay) {
			startAutoPlay();
		}
		a.recycle();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:
			xMove = event.getRawX();
			int distanceX = (int)(xMove - xDown) - (currentItemIdex * switcherViewWidth);
			firstItemParams.leftMargin = distanceX;
			if (beAbleToScroll()) {
				firstItem.setLayoutParams(firstItemParams);
			}
			break;
		case MotionEvent.ACTION_UP:
			xUp = event.getRawX();
			if (beAbleToScroll()) {
				if (wantScrollToPrevious()) {
					if (shouldScrollToPrevious()) {
						currentItemIdex--;
						scrollToPrevious();
						refeshDotsLayout();
					}
					else {
						scrollToNext();
					}
				}else if (wantScrollToNext()) {
					if (shouldScrollToNext()) {
						currentItemIdex++;
						scrollToNext();
						refeshDotsLayout();
					}
					else {
						scrollToPrevious();
					}
				}
			}
			recycleVelocityTracker(); 
			break;
		}
		return false;
	}
	/**
	 * 当前是否能够滚动，滚动到第一个或最后一个元素时将不能再滚动
	 * @return 当前leftMargin的值在leftEdge和rightEdge之间返回true，否则返回false。
	 */
	private boolean beAbleToScroll()
	{
		return firstItemParams.leftMargin < rightEdge
				&& firstItemParams.leftMargin > leftEdge;
	}
	
	/**
	 * 判断当前手势的意图是不是想滚动到下一个菜单元素。如果手指移动的距离是负数，则认为
	 * 当前收受是想要滚动到下一个菜单元素。
	 * @return 当前手势想滚动到上一个菜单元素返回true，否则返回false
	 */
	private boolean wantScrollToPrevious()
	{
		return xUp - xDown > 0;
	}
	/**
	 * 判断当前手势到意图是不是想滚动到下一个菜单元素。
	 * 如果手指移动到距离是负数，则认为当前手势是想要滚动到下一个菜单元素。
	 * @return 当前手势想滚动到下一个菜单元素返回true, 否则返回false
	 */
	private boolean wantScrollToNext()
	{
		return xUp - xDown < 0;
	}
	/** 
     * 判断是否应该滚动到下一个菜单元素。如果手指移动距离大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY， 
     * 就认为应该滚动到下一个菜单元素。 
     *  
     * @return 如果应该滚动到下一个菜单元素返回true，否则返回false。 
     */  
    private boolean shouldScrollToNext() {  
        return xDown - xUp > switcherViewWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;  
    }  
	  /** 
     * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中。 
     *  
     * @param event 
     *            右侧布局监听控件的滑动事件 
     */  
    private void createVelocityTracker(MotionEvent event) {  
        if (mVelocityTracker == null) {  
            mVelocityTracker = VelocityTracker.obtain();  
        }  
        mVelocityTracker.addMovement(event);  
    }  
    /** 
     * 滚动到下一个元素。 
     */  
    public void scrollToNext() {  
        new ScrollTask().execute(-20);  
    }  
  
    /** 
     * 滚动到上一个元素。 
     */  
    public void scrollToPrevious() {  
        new ScrollTask().execute(20);  
    }

    
    /**
     * 在onLayout中重新设定菜单元素和标签元素的参数
     */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		if (changed) 
		{
			initializaItems();
			initializeDots();
		}
	} 

    /**
     * 初始化菜单元素，为每一个子元素增加监听事件，并且改变
     * 所有子元素的宽度，让它们等于父元素的宽度
     */
	private void initializaItems()
	{
		switcherViewWidth = getWidth();
		itemsLayouot = (LinearLayout)getChildAt(0);
		itemsCount =  itemsLayouot.getChildCount();
		
		borders = new int[itemsCount];
		for (int i = 0; i < itemsCount; i++) 
		{
			borders[i] = -i * switcherViewWidth;
			View item = itemsLayouot.getChildAt(i);
			MarginLayoutParams params = 
					(MarginLayoutParams)item.getLayoutParams();
			params.width = switcherViewWidth;
			item.setLayoutParams(params);
			item.setOnTouchListener(this);
		}
		
		leftEdge = borders[itemsCount - 1];
		firstItem = itemsLayouot.getChildAt(0);
		firstItemParams = (MarginLayoutParams)firstItem.getLayoutParams();
	}
	
	/**
	 * 初始化标签元素
	 */
	private void initializeDots()
	{
		dotsLayout = (LinearLayout)getChildAt(1);
		refeshDotsLayout();
	}
	
	/**
	 * 刷新标签元素布局，每次currentItemIndex值改变的时候都应该进行刷新
	 */
	private void refeshDotsLayout()
	{
		dotsLayout.removeAllViews();
		for (int i = 0; i < itemsCount; i++) 
		{
			LinearLayout.LayoutParams linearParams
				= new LinearLayout.LayoutParams(0,LayoutParams.FILL_PARENT);
			linearParams.weight = 1;
			RelativeLayout relativeLayout = new RelativeLayout(getContext());
			ImageView image = new ImageView(getContext());
			if (i == currentItemIdex) 
			{
				image.setBackgroundResource(R.drawable.dot_selected);
			}
			else {
				image.setBackgroundResource(R.drawable.dot_unselected);
			}
			
			RelativeLayout.LayoutParams relativeParams
				= new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			relativeLayout.addView(image, relativeParams);
			dotsLayout.addView(relativeLayout, linearParams);
		}
		
	}
	 /** 
     * 判断是否应该滚动到上一个菜单元素。如果手指移动距离大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY， 
     * 就认为应该滚动到上一个菜单元素。 
     *  
     * @return 如果应该滚动到上一个菜单元素返回true，否则返回false。 
     */  
    private boolean shouldScrollToPrevious() {  
        return xUp - xDown > switcherViewWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;  
    }  

    
    /** 
     * 获取手指在右侧布局的监听View上的滑动速度。 
     *  
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。 
     */  
    private int getScrollVelocity() {  
        mVelocityTracker.computeCurrentVelocity(1000);  
        int velocity = (int) mVelocityTracker.getXVelocity();  
        return Math.abs(velocity);  
    }  
    /** 
     * 回收VelocityTracker对象。 
     */  
    private void recycleVelocityTracker() {  
        mVelocityTracker.recycle();  
        mVelocityTracker = null;  
    }  
	
    /**
     * 检测菜单滚动时，是否右穿越border，border的值都存储在(@link #broders)中
     * @param leftMargin
     * 			第一个元素的左偏移值
     * @param speed
     * 			滚动都速度，正数说明向右滚动，负数说明向左滚动
     * @return
     * 		穿越任何一个border了返回true，否则返回false
     */
    private boolean isCrossBorder(int leftMargin, int speed)
    {
    	for (int border : borders) {
			if (speed > 0) {
				if (leftMargin >= border && leftMargin - speed < border)
				{
					return true;
				}
			}
			else {
				if (leftMargin <= border && leftMargin - speed > border) {
					return true;
				}
			}
		}
    	return false;
    }
    /** 
     * 找到离当前的leftMargin最近的一个border值。 
     *  
     * @param leftMargin 
     *            第一个元素的左偏移值 
     * @return 离当前的leftMargin最近的一个border值。 
     */  
    private int findClosestBorder(int leftMargin) {  
        int absLeftMargin = Math.abs(leftMargin);  
        int closestBorder = borders[0];  
        int closestMargin = Math.abs(Math.abs(closestBorder) - absLeftMargin);  
        for (int border : borders) {  
            int margin = Math.abs(Math.abs(border) - absLeftMargin);  
            if (margin < closestMargin) {  
                closestBorder = border;  
                closestMargin = margin;  
            }  
        }  
        return closestBorder;  
    }  
  
    class ScrollTask extends AsyncTask<Integer, Integer, Integer> {  
  
        @Override  
        protected Integer doInBackground(Integer... speed) {  
            int leftMargin = firstItemParams.leftMargin;  
            // 根据传入的速度来滚动界面，当滚动穿越border时，跳出循环。  
            while (true) {  
                leftMargin = leftMargin + speed[0];  
                if (isCrossBorder(leftMargin, speed[0])) {  
                    leftMargin = findClosestBorder(leftMargin);  
                    break;  
                }  
                publishProgress(leftMargin);  
                // 为了要有滚动效果产生，每次循环使线程睡眠10毫秒，这样肉眼才能够看到滚动动画。  
                sleep(10);  
            }  
            return leftMargin;  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... leftMargin) {  
            firstItemParams.leftMargin = leftMargin[0];  
            firstItem.setLayoutParams(firstItemParams);  
        }  
  
        @Override  
        protected void onPostExecute(Integer leftMargin) {  
            firstItemParams.leftMargin = leftMargin;  
            firstItem.setLayoutParams(firstItemParams);  
        }  
    }  
  
    /** 
     * 使当前线程睡眠指定的毫秒数。 
     *  
     * @param millis 
     *            指定当前线程睡眠多久，以毫秒为单位 
     */  
    private void sleep(long millis) {  
        try {  
            Thread.sleep(millis);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  
	
    class ScrollToFirstItemTask extends AsyncTask<Integer, Integer, Integer>
    {

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int leftMargin = firstItemParams.leftMargin;
			while (true) {
				leftMargin = leftMargin + params[0];
				if (leftMargin > 0) {
					leftMargin = 0;
					break;
				}
				publishProgress(leftMargin);
				sleep(20);
			}
			return leftMargin;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			firstItemParams.leftMargin = values[0];
			firstItem.setLayoutParams(firstItemParams);
		}
    	@Override
    	protected void onPostExecute(Integer result) {
    		// TODO Auto-generated method stub
    		firstItemParams.leftMargin = result;
			firstItem.setLayoutParams(firstItemParams);
    	}
    }
    /**
     * 滚动到第一个元素
     */
    public void scrollToFirstItem()
    {
    	new ScrollToFirstItemTask().execute(20 * itemsCount);
    }
    
    /**
     * 用于在定时器当中操作ui界面
     */
    private Handler handler = new Handler();
    /**
     * 开启图片自动播放功能，当滚动到最后一张图片当时候，会自动回滚到第一张
     */
    public void startAutoPlay()
    {
    	new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (currentItemIdex == itemsCount - 1) {
					currentItemIdex = 0;
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							scrollToFirstItem();
							refeshDotsLayout();
						}
					});
				}
				else {
					currentItemIdex++;
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							scrollToNext();
							refeshDotsLayout();
						}
					});
				}
			}
		}, 3000, 3000);
    }
    
    
    
    
}
