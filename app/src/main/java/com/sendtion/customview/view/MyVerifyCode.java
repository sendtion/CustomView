package com.sendtion.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.sendtion.customview.R;

/**
 * 自定义验证码
 */
public class MyVerifyCode extends View {
    private String verifyCode;
    private Paint paint;
    private Rect rect;

    private boolean vcAutoRefresh;
    private int vcTextSize = 56;//单位
    private int vcTextColor = getResources().getColor(R.color.color_f);
    private int vcBackground = getResources().getColor(R.color.colorAccent);

    public MyVerifyCode(Context context) {
        this(context, null);
    }

    public MyVerifyCode(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyVerifyCode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyVerifyCode);
        vcAutoRefresh = typedArray.getBoolean(R.styleable.MyVerifyCode_vc_auto_refresh, false);
        vcTextSize = typedArray.getDimensionPixelSize(R.styleable.MyVerifyCode_vc_text_size, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        vcTextColor = typedArray.getColor(R.styleable.MyVerifyCode_vc_text_color, getResources().getColor(R.color.color_f));
        vcBackground =typedArray.getColor(R.styleable.MyVerifyCode_vc_background, getResources().getColor(R.color.colorAccent));
        typedArray.recycle();

        init();
    }

    private void init() {
        //会出现三位数的情况
        verifyCode = String.valueOf((int) (Math.random() * 10000));//四位验证码
        verifyCode = "abcdefg";

        paint = new Paint();
        //paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(vcTextSize);
        //Paint.ANTI_ALIAS_FLAG ：抗锯齿标志
        //Paint.FILTER_BITMAP_FLAG : 使位图过滤的位掩码标志
        //Paint.DITHER_FLAG : 使位图进行有利的抖动的位掩码标志
        //Paint.UNDERLINE_TEXT_FLAG : 下划线
        //Paint.STRIKE_THRU_TEXT_FLAG : 中划线
        //Paint.FAKE_BOLD_TEXT_FLAG : 加粗
        //Paint.LINEAR_TEXT_FLAG : 使文本平滑线性扩展的油漆标志
        //Paint.SUBPIXEL_TEXT_FLAG : 使文本的亚像素定位的绘图标志
        //Paint.EMBEDDED_BITMAP_TEXT_FLAG : 绘制文本时允许使用位图字体的绘图标志
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        rect = new Rect();

        if (vcAutoRefresh) {

        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode = String.valueOf((int) (Math.random() * 10000));//四位验证码
                postInvalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        } else {
            paint.setTextSize(vcTextSize);
            paint.getTextBounds(verifyCode, 0, verifyCode.length(), rect);
            float textWidth = rect.width();
            Log.e("---", "onMeasure: textWidth= " + textWidth);
            //width = (int) textWidth; //这样也可以，但是没计算padding
            width = (int) (getPaddingLeft() + textWidth + getPaddingRight());
        }
        if (heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        } else {
            paint.setTextSize(vcTextSize);
            paint.getTextBounds(verifyCode, 0, verifyCode.length(), rect);
            float textHeight = rect.height();
            Log.e("---", "onMeasure: textHeight= " + textHeight);
            //height = (int) textHeight; //这样也可以，但是没计算padding
            height = (int) (getPaddingTop() + textHeight + getPaddingBottom());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画背景
        paint.setColor(vcBackground);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        //画文字
        Log.e("---", "onDraw: getWidth=" + getWidth() + ", getHeight=" + getHeight());
        paint.setColor(vcTextColor);
        paint.setStrokeWidth(2);
        paint.getTextBounds(verifyCode, 0, verifyCode.length(), rect);
        //第一种方式，计算的基准点偏上
        int startX = getWidth()/2 - rect.width()/2;
        int startY = getHeight()/2 - rect.height()/2;
        int offset = (rect.top + rect.bottom)/2;//随着绘制内容的变化，导致中间位置也会变化
        //canvas.drawText(verifyCode, startX, startY-offset, paint);
        //第二种方式，跟第一种效果一样
        Paint.FontMetricsInt fontMetrics=paint.getFontMetricsInt();
        offset = (fontMetrics.descent + fontMetrics.ascent)/2;//固定的文字测量工具，中间位置不会随绘制内容发生改变
        //canvas.drawText(verifyCode, startX, startY-offset, paint);
        //第三种方式，貌似更准确，计算的基准点更准，内容变化也会居中显示
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        startX = (int) (getWidth() / 2 - paint.measureText(verifyCode) / 2);
        startY = getHeight() / 2 - fm.descent + (fm.bottom - fm.top) / 2;
        startY = getHeight() / 2 + (fm.bottom - fm.top) / 2 - fm.bottom;
        startY = getHeight() / 2 + (fm.descent - fm.ascent) / 2 - fm.descent;
        canvas.drawText(verifyCode, startX, startY, paint);

        //画十字交叉线
//        paint.setColor(Color.GREEN);
//        paint.setStrokeWidth(2);
//        canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, paint);
//        canvas.drawLine(getWidth()/2, 0, getWidth()/2, getHeight(), paint);
//
//        //画基准线
//        canvas.drawLine(0, getHeight()/2-rect.height()/2, getWidth(), getHeight()/2-rect.height()/2, paint);
//        canvas.drawLine(0, getHeight()/2+rect.height()/2, getWidth(), getHeight()/2+rect.height()/2, paint);
//        canvas.drawLine(getWidth()/2-rect.width()/2, 0, getWidth()/2-rect.width()/2, getHeight(), paint);
//        canvas.drawLine(getWidth()/2+rect.width()/2, 0, getWidth()/2+rect.width()/2, getHeight(), paint);
//
//        //画基准点
//        paint.setColor(Color.BLUE);
//        paint.setStrokeWidth(10);
//        canvas.drawPoint(getWidth()/2-rect.width()/2, getHeight()/2-rect.height()/2, paint);

        //https://www.jianshu.com/p/8c10a8a8e669
        //https://blog.csdn.net/SilenceOO/article/details/73498331
        //文字不居中，有点偏上，是因为drawText中的参数x和y是基线坐标
    }

}
