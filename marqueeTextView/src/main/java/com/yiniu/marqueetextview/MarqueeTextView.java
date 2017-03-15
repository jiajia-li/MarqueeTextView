package com.yiniu.marqueetextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;

/**
 * Created by lijiaming on 2017/2/13.
 */

/**
 * 一个会根据字体长度自动判断是否使用跑马灯效果的TextView(仅支持单行)
 *
 */
public class MarqueeTextView extends AppCompatTextView implements Runnable {

    
    public static final String DEFAULT_DIVIDER = "     ";

    public static final int DEFAULT_DIVIDER_LENGTH = 12;

    public static final float DEFAULT_SPEED = 0.5f;

    public static final int DEFAULT_STOP_DURATION = 2000;

    public static final int DEFAULT_INTERVAL = 10;

    private CharSequence originalString;
    private String spaceString = DEFAULT_DIVIDER;
    private float currentScrollX; // 当前滚动的位置
    private boolean isStop = false;
    private int speed ;
    private int interval = DEFAULT_INTERVAL;
    private int textWidth;
    private int spaceWidth;
    private int resetScrollX;
    private int stopDuration = DEFAULT_STOP_DURATION;
    private boolean isMeasure = false;

    private int measureType = 0;
    private int maxWidth = -1;
    private int gravity;
    private boolean isGetGravity = false;



    public MarqueeTextView(Context context) {
        this(context,null);
    }


    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }


    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setSingleLine(true);
        super.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.MarqueeTextView);
        //如果没有设置特定的间隔字符将会使用若干个空格间隔
        String spaceStr = ta.getString(R.styleable.MarqueeTextView_dividerText);
        if (spaceStr!=null){
            //自定义间隔字符
            spaceString = spaceStr;
        }else {
            //自定义空格个数
            int length = ta.getInt(R.styleable.MarqueeTextView_spaceLength,DEFAULT_DIVIDER_LENGTH);
            StringBuilder builder = new StringBuilder();
            for(int i = 0;i<length;i++){
                builder.append(" ");
            }
            spaceString = builder.toString();
        }
        //每秒移动像素
        speed = ta.getDimensionPixelSize(R.styleable.MarqueeTextView_moveSpeed,(int)(DEFAULT_SPEED*getContext().getResources().getDisplayMetrics().density+0.5f));
        stopDuration = ta.getInt(R.styleable.MarqueeTextView_remainDuration,DEFAULT_STOP_DURATION);
        interval = ta.getInt(R.styleable.MarqueeTextView_refreshInterval,DEFAULT_INTERVAL);
        ta.recycle();
        gravity = getGravity();
        isGetGravity = true;
    }

    private void initMarquee(){
        textWidth = getTextWidth(originalString.toString());
        spaceWidth = getTextWidth(spaceString);
        resetScrollX = getTextWidth(originalString.toString()+spaceString);
        setGravity(Gravity.NO_GRAVITY);
        setText(originalString+spaceString+originalString);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        currentScrollX = this.getWidth();
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize =  MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                maxWidth = -1;
                break;
            case MeasureSpec.AT_MOST:
                maxWidth = specSize;
                break;
            case MeasureSpec.EXACTLY:
                maxWidth = specSize;
                break;
        }
        measureType = specMode;
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isMeasure) {
//            getTextWidth();// 文字宽度只需要获取一次就可以了
            if (getTextWidth(originalString.toString()) > maxWidth && maxWidth != -1) {
                initMarquee();
                startScroll();
            } else {
                if (isGetGravity) {
                    setGravity(gravity);
                }
            }
            isMeasure = true;
        }
    }


    private int getTextWidth(String str) {
        Paint paint = this.getPaint();
        return (int) paint.measureText(str);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        stopScroll();
        originalString = text;
        reset();
        isMeasure = false;

    }

    @Override
    public void run() {
        if (isStop) {
            return;
        }
        currentScrollX += speed;
        scrollTo(Math.round(currentScrollX), 0);
        if(getScrollX()>resetScrollX){
            postDelayed(this, 2000);
            currentScrollX = getScrollX()-resetScrollX;
            return;
        }
        Log.d("MarqueeTextView",System.currentTimeMillis()+"");
        postDelayed(this,interval);
    }


    private void startScroll() {
        isStop = false;
        this.removeCallbacks(this);
        postDelayed(this,stopDuration);
    }


    // 停止滚动
    private void stopScroll() {
        isStop = true;
    }

    private void reset(){
        currentScrollX = 0;
        scrollTo(Math.round(currentScrollX),0);
    }
    // 从头开始滚动
    public void startFromHead() {
        currentScrollX = 0;
        startScroll();
    }

    public String getSpaceString() {
        return spaceString;
    }

    public void setSpaceString(String spaceString) {
        this.spaceString = spaceString;
        stopScroll();
        reset();
        isMeasure = false;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Deprecated
    @Override
    public void setMaxLines(int maxlines) {
    }

    @Deprecated
    @Override
    public void setMinLines(int minlines) {
    }
    @Deprecated
    @Override
    public void setEllipsize(TextUtils.TruncateAt where) {

    }
    @Deprecated
    @Override
    public void setSingleLine() {

    }
}
