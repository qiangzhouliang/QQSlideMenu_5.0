package com.example.qzl.qqslidemenu_50.test;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.example.qzl.qqslidemenu_50.ColorUtil;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Qzl on 2016-07-24.
 */
public class DragLayout extends FrameLayout {

    private View redView;//红孩子
    private View blueView;//蓝精灵
    private ViewDragHelper viewDragHelper;
    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 当dragLayout的xml布局的结束标签读取完成会执行该方法，此时，会知道自己有几个子View了
     * 一般用来初始化子view的引用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redView = getChildAt(0);
        blueView = getChildAt(1);
    }

    /**
     * 创建ViewDragHelper
     1.ViewDragHelper在高版本的v4包(android4.4以上的v4)中
     2.它主要用于处理ViewGroup中对子View的拖拽处理
     3.它是Google在2013年开发者大会提出的
     4.它主要封装了对View的触摸位置，触摸速度，移动距离等的检测和Scroller,通过接口回调的
     方式告诉我们;只需要我们指定是否需要移动，移动多少等;
     5.本质是对触摸事件的解析类;
     */
    public void init(){
        //ViewDragHelper :用来处理子view的移动情况
        viewDragHelper = ViewDragHelper.create(this,callback);
    }
    /**
     * 设置当前控件的宽高
     */
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //测量自己的view
//        //int size = (int) getResources().getDimension(R.dimen.width);//100dp
//        //EXACTLY:精确值
//        //int messureSpec = MeasureSpec.makeMeasureSpec(redView.getLayoutParams().width,MeasureSpec.EXACTLY);
//        //redView.measure(messureSpec,messureSpec);
//        //blueView.measure(messureSpec,messureSpec);
//        //如果说没有特殊的对子view的测量需求，可以使用此方法
//        measureChild(redView,widthMeasureSpec,heightMeasureSpec);
//        measureChild(blueView,widthMeasureSpec,heightMeasureSpec);
//    }

    //摆放位置，相当于重写了frameLayout的onLayout方法
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //int left = getPaddingLeft()+getMeasuredWidth()/2 - redView.getMeasuredWidth()/2;
        int left = getPaddingLeft();
        int top = getPaddingTop();
        //摆放位置
        redView.layout(left,top,left+redView.getMeasuredWidth(),top+redView.getMeasuredHeight());
        blueView.layout(left,top+redView.getBottom(),left+redView.getMeasuredWidth(),redView.getBottom()+blueView.getMeasuredHeight());
    }

    /**
     * 用来处理是否拦截
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让viewDragHelper帮我们判断是否应该拦截
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    /**
     * 触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将触摸事件交给viewDragHelper来解析处理
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 是否用于判断是否捕捉当前child的触摸事件
         * @param child：当前触摸的子view
         * @param pointerId
         * @return true：就捕获并解析，false：不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //当触摸蓝色的时候进行捕获
            return child == blueView || child == redView;
        }

        /**
         * 当view被开始捕获和解析的回调
         * @param capturedChild：当前被捕获的子view
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
//            Log.e("tag","onViewCaptured");
        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界，
         * @param child
         * @return ：目前用在手指抬起的时候view缓慢移动的动画时间的计算上面，最好不要反回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        /**
         * 获取view垂直方向的拖拽范围
         * @param child
         * @return
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        /**
         * 控制child在水平方向的移动
         * @param child ：当前所触摸的子view
         * @param left ：表示viewDragHelper认为你想让当前child的left改变的值，left = child.getLeft+dx
         * @param dx : 表示本次child水平方向移动的距离
         * @return : 表示你想让child的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //Log.e("tag","left: "+left+" dx : "+dx);
            if(left < 0){
                //限制左边界
                left = 0;
            }else if (left > getMeasuredWidth() - child.getMeasuredWidth()){
                //限制右边界
                left = getMeasuredWidth() - child.getMeasuredWidth();
            }
            return left;
            //return left - dx; //水平方向不能移动

        }

        /**
         * 控制child在垂直方向的移动
         * @param child
         * @param top ：表示viewDragHelper认为你想让当前child的top改变的值，top = child.getTop+dx
         * @param dy
         * @return ：表示你正真想让child的top变成的值
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if(top < 0 ){
                //限制上边界
                top = 0;
            }else if (top > getMeasuredHeight() - child.getMeasuredHeight()){
                //限制下边界
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;
        }

        /**
         * 当child的位置改变的时候执行,一般用来做其他子view的跟随移动
         * @param changedView ；位置改变的child
         * @param left ： child当前最新的left
         * @param top ： child当前最新的top
         * @param dx ： 本次水平移动的距离
         * @param dy ：本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.e("tag","left :"+left+" top : " + top+" dx : "+dx +" dy :"+dy);
            if (changedView == blueView){
                //blueView移动的时候让redView跟随移动
                redView.layout(redView.getLeft()+dx,redView.getTop()+dy,redView.getRight()+dx,redView.getBottom()+dy);
            }else if (changedView == redView){
                //redView移动的时候让blueView跟随移动
                blueView.layout(blueView.getLeft()+dx,blueView.getTop()+dy,blueView.getRight()+dx,blueView.getBottom()+dy);
            }
            // 1 计算view移动的百分比
            float fraction = changedView.getLeft()*1f/(getMeasuredWidth() - changedView.getMeasuredWidth());
            // 2 执行一系列的伴随动画
            executeAnim(fraction);
        }

        /**
         * 手指抬起执行该方法
         * @param releasedChild ： 当前抬起的view
         * @param xvel ：x方向移动的速度 正：向右移动，负 ： 向左移动
         * @param yvel ：y方向的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerLeft = getMeasuredWidth()/2 - releasedChild.getMeasuredWidth()/2;
            if (releasedChild.getLeft() < centerLeft){
                //在左半边，应该向左缓慢移动
                viewDragHelper.smoothSlideViewTo(releasedChild,0,releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);//刷新
            }else {
                //在右半边，应该向右移动
                viewDragHelper.smoothSlideViewTo(releasedChild,getMeasuredWidth() - releasedChild.getMeasuredWidth(),releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);//刷新
            }
        }
    };

    /**
     * 执行伴随动画
     * @param fraction
     */
    private void executeAnim(float fraction) {
        //fraction: 0-1
        //缩放
//        ViewHelper.setScaleX(redView,1+0.5f*fraction);//实现缩放
//        ViewHelper.setScaleY(redView,1+0.5f*fraction);//实现缩放

        //旋转
        ViewHelper.setRotation(blueView,360*fraction);//平面转，围绕Z轴转
        ViewHelper.setRotationX(redView,360*fraction);//上下转，围绕x轴转
//        ViewHelper.setRotationY(blueView,360*fraction);//左右转，围绕Y轴转

        //平移
//        ViewHelper.setTranslationX(redView,80 * fraction);
//        ViewHelper.setTranslationY(redView,80 * fraction);

        //透明
//        ViewHelper.setAlpha(redView,1 - fraction);

        //设置过度颜色的渐变
        redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED,Color.GREEN));
//        setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED,Color.GREEN));//设置DragLayout
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }
}
