package com.example.qzl.qqslidemenu_50;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Qzl on 2016-07-24.
 */
public class SlideMenu extends FrameLayout{

    private View menuView;//菜单的view
    private View mainView;//主界面的view
    private ViewDragHelper viewDragHelper;
    private int width;//当前SlideMenu的宽
    private float dragRange;//拖拽范围
    private FloatEvaluator floatEvaluator;//float计算器
    private IntEvaluator intEvaluator;//int计算器
    private OnDragStateChangeListener listener;

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        new SlideMenu(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        new SlideMenu(context,attrs);
        init();
    }

    //定义状态常量
    enum DragState{
        Open,Close;
    }
    private DragState currentState = DragState.Close;//当前SlideMenu的状态默认是关闭的
    private void init(){
        viewDragHelper = ViewDragHelper.create(this,callback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }
    /**
     * 当dragLayout的xml布局的结束标签读取完成会执行该方法，此时，会知道自己有几个子View了
     * 一般用来初始化子view的引用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //简单的异常处理
        if (getChildCount() != 2){
            throw new IllegalArgumentException("SlideMenu only have 2 children");
        }
        //菜单布局
        menuView = getChildAt(0);//获取子布局
        //主布局
        mainView = getChildAt(1);
    }

    /**
     * 该方法在onMeasure执行完之后执行，那么可以在该方法中初始化自己的View的宽高
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragRange = width * 0.6f;
    }

    /**
     * 是否需要拦截
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
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
            return child == menuView || child == mainView;
        }
        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界，
         * @param child
         * @return ：目前用在手指抬起的时候view缓慢移动的动画时间的计算上面，最好不要反回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
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
            if (child == mainView){
                if (left < 0){
                    //限制左边的距离
                    left = 0;
                }else if (left > dragRange){
                    //限制mainView右边的距离
                    left = (int) dragRange;
                }
            }
            return left;
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
            if (changedView == menuView){
                //固定住menuView
                menuView.layout(0,0,menuView.getMeasuredWidth(),menuView.getMeasuredHeight());
                //让mainView动起来
                int newLeft = mainView.getLeft()+dx;
                if (newLeft < 0){
                    newLeft = 0;
                }else if (newLeft > dragRange){
                    newLeft = (int) dragRange;
                }
                mainView.layout(newLeft,mainView.getTop()+dy,newLeft+mainView.getMeasuredWidth(),mainView.getBottom()+dy);
            }
            //1 计算滑动的百分比
            float fraction = mainView.getLeft()/dragRange;
            //2 执行伴随的动画
            executeAnim(fraction);
//            Log.d("tag","fraction = "+fraction);
            //3 更改状态，回调listener的方法
            if(fraction < 0.05f && currentState != DragState.Close){
                //更改状态为关闭，并回调关闭的方法
                currentState = DragState.Close;
                if (listener != null){
                    listener.onClose();
                }
            }else if (fraction > 0.95f && currentState != DragState.Open){
                //更改状态为打开，并回调打开的方法
                currentState = DragState.Open;
                if (listener != null){
                    listener.onOpen();
                }
            }
            //将drag的fraction暴露给外界
            if(listener != null){
                listener.onDraging(fraction);
            }
        }
        /**
         * 手指抬起执行该方法
         * @param releasedChild ： 当前抬起的view
         * @param xvel ：x方向移动的速度 正：向右移动，负 ： 向左移动
         * @param yvel ：y方向的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mainView.getLeft() < dragRange/2){
                //在左半边
                viewDragHelper.smoothSlideViewTo(mainView,0,0);
                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
            }else {
                //在右半边
                viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange,mainView.getTop());
                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
            }
        }
    };

    /**
     * 执行一系列的动画操作
     * @param fraction
     */
    private void executeAnim(float fraction) {
        //1 缩小mainView
//        float scaleValue = 0.8f + 0.2f*(1 - fraction);//1-0.8f
        ViewHelper.setScaleX(mainView,floatEvaluator.evaluate(fraction,1f,0.8f));
        ViewHelper.setScaleY(mainView,floatEvaluator.evaluate(fraction,1f,0.8f));
        //移动menuView
        ViewHelper.setTranslationX(menuView,intEvaluator.evaluate(fraction,-menuView.getMeasuredWidth()/2,0));
        //方大menuView
        ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
        ViewHelper.setScaleY(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
        //改变menuView的透明度
        ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fraction,0.3f,1f));
        //给SlideMenu的背景添加一个黑色的遮罩效果
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)){
            //如果动画没有结束，在刷新一下
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    /**
     * 设置监听方法
     * @param listener
     */
    public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
        this.listener = listener;
    }
    public interface OnDragStateChangeListener{
        /**
         * 打开的回调
         */
        void onOpen();
        /**
         * 关闭的回调
         */
        void onClose();

        /**
         * 正在拖拽中的回调
         */
        void onDraging(float fraction);
    }
}
