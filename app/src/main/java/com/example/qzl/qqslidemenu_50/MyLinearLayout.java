package com.example.qzl.qqslidemenu_50;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 当SlideMenu打开的时候，拦截并消费掉触摸事件
 * Created by Qzl on 2016-07-25.
 */
public class MyLinearLayout extends LinearLayout {
    private SlideMenu slideMenu;
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSlideMenu(SlideMenu slideMenu){
        this.slideMenu = slideMenu;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slideMenu != null && slideMenu.getCurrentState() == SlideMenu.DragState.Open){
            //如果SlideMenu打开则应该拦截，并消费掉事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 处理点击事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slideMenu != null && slideMenu.getCurrentState() == SlideMenu.DragState.Open){
            if (event.getAction() == MotionEvent.ACTION_UP){
                //点击抬起之后，关闭Slidemenu
                slideMenu.close();
            }
            //如果SlideMenu打开则应该拦截，并消费掉事件
            return true;
        }
        return super.onTouchEvent(event);
    }
}
