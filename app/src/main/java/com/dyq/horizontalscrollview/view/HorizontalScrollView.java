package com.dyq.horizontalscrollview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * author:duyongqiang
 * mail: duyongqiang09@126.com
 * date:2019/7/15
 * Description:继承ViewGroup的自定义view
 */
public class HorizontalScrollView extends ViewGroup {
    private static final String TAG="HorizontalScrollView";

    private int mChildrenSize;
    private int mChildWidth;
    private int mChildIndex;


    //分别记录上次滑动的坐标
    private int mLastX=0;
    private int mLastY=0;

    //分别记录上次滑动的坐标(在onInterceptTouchEvent()方法中)
    private int mLastXIntercept=0;
    private int mLastYIntercept=0;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    //-------------------------构造方法------------------------------------
    public HorizontalScrollView(Context context) {
        super(context);
        init();
    }

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mScroller==null){
            mScroller=new Scroller(getContext());
            mVelocityTracker=VelocityTracker.obtain();
        }
    }


    //-------------------------事件处理----------------------------------
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted=false;

        int x= (int) event.getX();
        int y= (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://按下
                intercepted=false;
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                    intercepted=true;
                }
                break;
            case MotionEvent.ACTION_MOVE://移动
                int deltaX=x-mLastXIntercept;//x轴移动的距离
                int deltaY=y-mLastYIntercept;//y轴移动的距离

                if (Math.abs(deltaX)>Math.abs(deltaY)){//如果x轴移动的距离大于y轴移动的距离，说明是横向滑动，则拦截滑动事件
                    intercepted=true;
                }else {//纵向移动，不拦截滑动事件
                    intercepted=false;
                }




                break;
            case MotionEvent.ACTION_UP://抬起
                intercepted=false;
                break;
                default:
                    break;

        }

        //打印是否拦截滑动事件
        Log.e(TAG,"intercepted="+intercepted);

        mLastX=x;
        mLastY=y;

        mLastXIntercept=x;
        mLastYIntercept=y;

        //返回是否拦截滑动事件结果
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//事件被拦截后被调用
        mVelocityTracker.addMovement(event);
        int x= (int) event.getX();
        int y= (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://按下
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE://移动
                int deltaX=x-mLastX;//横向移动距离
                int deltaY=y-mLastY;//纵向移动距离
                scrollBy(-deltaX,0);

                break;
            case MotionEvent.ACTION_UP://抬起
                int scrollX=getScrollX();
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity=mVelocityTracker.getXVelocity();//x轴的移动速度
                if (Math.abs(xVelocity)>=50){
                    mChildIndex=xVelocity>0?mChildIndex-1:mChildIndex+1;
                }else {
                    mChildIndex=(scrollX+mChildWidth/2)/mChildWidth;
                }

                mChildIndex=Math.max(0,Math.min(mChildIndex,mChildrenSize-1));
                int dx=mChildIndex*mChildWidth-scrollX;
                smoothScrollBy(dx,0);
                mVelocityTracker.clear();

                break;
                default:
                    break;
        }

        mLastX=x;
        mLastY=y;


        //滑动事件被父控件消费了，所以必须返回true
        return true;
    }

    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(getScrollX(),0,dx,0,500);
        //刷新view
        invalidate();
    }

    //--------------------------绘制处理-----------------------------------
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth=0;
        int measureHeight=0;
        int childCount=getChildCount();
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        int widthSpecSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode=MeasureSpec.getMode(widthMeasureSpec);

        int heightSpecSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode=MeasureSpec.getMode(heightMeasureSpec);

        if (childCount==0){//子view数为0
            setMeasuredDimension(0,0);
        }else if (widthSpecMode==MeasureSpec.AT_MOST&&heightSpecMode==MeasureSpec.AT_MOST){//宽和高都是wrap_content
            View childView=getChildAt(0);
            measureWidth=childView.getMeasuredWidth()*childCount;
            measureHeight=childView.getMeasuredHeight();
            setMeasuredDimension(measureWidth,measureHeight);
        }else if (widthSpecMode==MeasureSpec.AT_MOST){//只有宽是wrap_content
            View childView=getChildAt(0);
            measureWidth=childView.getMeasuredWidth()*childCount;
            setMeasuredDimension(measureWidth,heightSpecSize);

        }else if (heightSpecMode==MeasureSpec.AT_MOST){//只有高是wrap_content
            View childView=getChildAt(0);
            measureHeight=childView.getMeasuredHeight();
            setMeasuredDimension(widthSpecSize,measureHeight);

        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft=0;
        int childCount=getChildCount();
        mChildrenSize=childCount;
        for (int i=0;i<childCount;i++){
            View childView=getChildAt(i);
            if (childView.getVisibility()!=View.GONE){
                int childWidth=childView.getMeasuredWidth();
                mChildWidth=childWidth;
                childView.layout(childLeft,0,childLeft+childWidth,childView.getMeasuredHeight());
                childLeft+=childWidth;
            }
        }

    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            //刷新view
            postInvalidate();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        //资源回收
        mVelocityTracker.recycle();

        super.onDetachedFromWindow();
    }
}
