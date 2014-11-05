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
     * ������ʾ������menuʱ����ָ������Ҫ�ﵽ���ٶȡ� 
     */  
	public static final int SNAP_VELOCITY = 200;
	
    /** 
     * ��Ļ���ֵ�� 
     */  
	private int screenWidth;
    /** 
     * menu�����Ի����������Ե��ֵ��menu���ֵĿ��������marginLeft�����ֵ֮�󣬲����ټ��١� 
     */  
	private int leftEdge;
    /** 
     * menu�����Ի��������ұ�Ե��ֵ��Ϊ0����marginLeft����0֮�󣬲������ӡ� 
     */  
	private int rightEdge = 0;
    /** 
     * menu��ȫ��ʾʱ������content�Ŀ��ֵ�� 
     */ 
	private int menuPadding = 80;
	
	/**
	 * �˵�����
	 */
	@ViewById(R.id.menu)
	LinearLayout menu;
	/**
	 * ���ݲ���
	 */
	@ViewById(R.id.content)
	View content;
    /** 
     * menu���ֵĲ�����ͨ���˲���������leftMargin��ֵ�� 
     */ 
	private LinearLayout.LayoutParams menuParams;
	
	/**
	 * ��¼��ָ����ʱ�ĺ�����
	 */
	private float xDown;
	/**
	 * ��¼��ָ�ƶ�ʱ�ĺ�����
	 */
	private float xMove;
	/**
	 * ��¼��ָ̧��ʱ�ĺ����ꡣ
	 */
	private float xUp;
	
	/**
	 * menu��ǰ����ʾ�������أ�ֻ����ȫ��ʾ������menuʱ�Ż���Ĵ�ֵ��������д�ֵ��Ч
	 */
	private boolean isMenuVisible;
	
	/**
	 * ���ڼ�����ָ�Ļ������ٶ�
	 */
	private VelocityTracker mVelocityTracker;
	
	@SystemService
	WindowManager mWindowManager;
	
	@AfterViews
	void init()
	{
		screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		menuParams = (LinearLayout.LayoutParams) menu.getLayoutParams();
		// ��menu�Ŀ������Ϊ��Ļ��ȼ�ȥmenuPadding  
		menuParams.width = screenWidth - menuPadding;
		// ���Ե��ֵ��ֵΪmenu��ȵĸ��� 
		leftEdge = -menuParams.width;
		// menu��leftMargin����Ϊ���Ե��ֵ��������ʼ��ʱmenu�ͱ�Ϊ���ɼ�  
		menuParams.leftMargin = leftEdge;
		// ��contentde�������Ϊ��Ļ���
		content.getLayoutParams().width = screenWidth;
		content.setOnTouchListener(this);
		

		
	}
	
    /** 
     * ����VelocityTracker���󣬲�������content����Ļ����¼����뵽VelocityTracker���С� 
     *  
     * @param event 
     *            content����Ļ����¼� 
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
			//��ָ����ʱ����¼����ʱ�ĺ�����
			xDown = event.getRawX();
			break;
			
		case MotionEvent.ACTION_MOVE:
			// ��ָ�ƶ�ʱ���ԱȰ���ʱ�ĺ����꣬������ƶ��ľ��룬������menu��leftMarginֵ���Ӷ���ʾ������menu  
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
     * �жϵ�ǰ���Ƶ���ͼ�ǲ�������ʾcontent�������ָ�ƶ��ľ����Ǹ������ҵ�ǰmenu�ǿɼ��ģ�����Ϊ��ǰ��������Ҫ��ʾcontent�� 
     *  
     * @return ��ǰ��������ʾcontent����true�����򷵻�false�� 
     */  
	private boolean wantToShowContent()
	{
		return xUp - xDown < 0 && isMenuVisible;
	}
	
    /** 
     * �жϵ�ǰ���Ƶ���ͼ�ǲ�������ʾmenu�������ָ�ƶ��ľ������������ҵ�ǰmenu�ǲ��ɼ��ģ�����Ϊ��ǰ��������Ҫ��ʾmenu�� 
     *  
     * @return ��ǰ��������ʾmenu����true�����򷵻�false�� 
     */ 
	private boolean wantToShowMenu()
	{
		return xUp - xDown > 0 && !isMenuVisible;
	}
    /** 
     * �ж��Ƿ�Ӧ�ù�����menuչʾ�����������ָ�ƶ����������Ļ��1/2��������ָ�ƶ��ٶȴ���SNAP_VELOCITY�� 
     * ����ΪӦ�ù�����menuչʾ������ 
     *  
     * @return ���Ӧ�ù�����menuչʾ��������true�����򷵻�false�� 
     */ 
	private boolean shouldScrollToMenu()
	{
		return xUp - xDown > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
	}
	
	 /** 
     * �ж��Ƿ�Ӧ�ù�����contentչʾ�����������ָ�ƶ��������menuPadding������Ļ��1/2�� 
     * ������ָ�ƶ��ٶȴ���SNAP_VELOCITY�� ����ΪӦ�ù�����contentչʾ������ 
     *  
     * @return ���Ӧ�ù�����contentչʾ��������true�����򷵻�false�� 
     */  
	private boolean shouldScrollToContent()
	{
		return xDown - xUp + menuPadding > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
	}
    /** 
     * ����Ļ������menu���棬�����ٶ��趨Ϊ30. 
     */  
	private void scrollToMenu()
	{
		slid(25);
	}
    /** 
     * ����Ļ������content���棬�����ٶ��趨Ϊ-30. 
     */  
    private void scrollToContent() 
    {  
    	slid(-25);
    }  
    
    /** 
     * ��ȡ��ָ��content���滬�����ٶȡ� 
     *  
     * @return �����ٶȣ���ÿ�����ƶ��˶�������ֵΪ��λ�� 
     */  
    private int getScrollVelocity()
    {
    	mVelocityTracker.computeCurrentVelocity(1000);
    	int velocity = (int)mVelocityTracker.getXVelocity();
    	return Math.abs(velocity);
    }
    
    /**
     * ����VelocityTracker����
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
		// ���ݴ�����ٶ����������棬������������߽���ұ߽�ʱ������ѭ����  
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
			// Ϊ��Ҫ�й���Ч��������ÿ��ѭ��ʹ�߳�˯��20���룬�������۲��ܹ���������������
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
