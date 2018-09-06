package com.up72.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import com.up72.library.utils.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 弹幕控件
 * Created by LYF on 2016/7/13.
 */
public class BarrageView extends View {
    private static final int INTERVAL = 30;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Position.ALL, Position.TOP, Position.CENTER, Position.BOTTOM})
    public @interface Position {
        int ALL = 1;
        int TOP = 2;
        int CENTER = 3;
        int BOTTOM = 4;
    }

    private static final float MIN_SIZE = 40;//弹幕的最小字号
    private static final float MAX_SIZE = 100;//弹幕的最大字号
    @Position
    private int mPosition = Position.ALL;
    private List<BarrageModel> mList;
    private Paint textPaint;//文字画笔
    private int mWidth, mHeight;//画布宽高
    private float currentSize = MIN_SIZE;//弹幕的当前字号
    private int textHeight = 0;
    private int textAlpha = 255;

    private Log log = new Log(getClass());

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(1);
            if (mList != null && mList.size() > 0) {
                mHandler.postDelayed(this, INTERVAL);
            }
        }
    };

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    invalidate();
                    break;
            }
            return false;
        }
    });

    public BarrageView(Context context) {
        super(context);
        init();
    }

    public BarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(currentSize);
        textPaint.setColor(Color.WHITE);
        textPaint.setAlpha(255);
        setBarrageSize(20);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    public void add(List<String> source) {
        this.add(source, Color.WHITE);
    }

    public void add(List<String> source, int color) {
        if (source == null || source.size() == 0) {
            return;
        }
        if (mList == null) {
            mList = new ArrayList<>();
        }
        for (String str : source) {
            mList.add(new BarrageModel(getRandomSpeed(), mWidth, getRandomY(mHeight, mPosition), getStringWidth(textPaint, str), str, currentSize, color, textAlpha));
        }
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, INTERVAL);
    }

    public void add(String source) {
        this.add(source, Color.WHITE);
    }

    public void add(String source, int color) {
        if (source == null || source.length() == 0) {
            return;
        }
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(new BarrageModel(getRandomSpeed(), mWidth, getRandomY(mHeight, mPosition), getStringWidth(textPaint, source), source, currentSize, color, textAlpha));
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, INTERVAL);
    }

    /**
     * 设置弹幕大小
     *
     * @param progress progress/100 (progress>=0&&progress<=100)
     */
    public void setBarrageSize(int progress) {
        if (progress >= 0 && progress <= 100) {
            currentSize = (MIN_SIZE + (MAX_SIZE - MIN_SIZE) * progress / 100F);
            textPaint.setTextSize(currentSize);
            textHeight = getStringHeight(textPaint);
        }
    }

    /**
     * 设置弹幕透明度
     *
     * @param progress progress/100 (progress>=0&&progress<=100)
     */
    public void setBarrageAlpha(int progress) {
        if (progress >= 0 && progress <= 100) {
            this.textAlpha = progress * 255 / 100;
        }
    }

    /**
     * 设置弹幕位置
     *
     * @param position @Position
     */
    public void setBarragePosition(@Position int position) {
        this.mPosition = position;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            if (width > 0 && height > 0) {
                if (mList != null) {
                    int size = mList.size();
                    BarrageModel model1;
                    for (int i = size - 1; i >= 0; i--) {
                        model1 = mList.get(i);
                        model1.x -= model1.speed;
                        if (model1.x <= -model1.width) {
                            mList.remove(model1);
                        }
                    }

                    for (BarrageModel model2 : mList) {
                        textPaint.setTextSize(model2.size);
                        textPaint.setColor(model2.textColor);
                        textPaint.setAlpha(model2.alpha);
                        canvas.drawText(model2.text, model2.x, model2.y, textPaint);
                    }
                }
            }
        }
    }

    /**
     * 获取文字宽度
     *
     * @param paint 画笔
     * @param str   字符串
     * @return 字符串宽度
     */
    private int getStringWidth(Paint paint, String str) {
        return (int) paint.measureText(str);
    }

    /**
     * 获取文字高度
     *
     * @param paint 画笔
     * @return 字符串高度
     */
    private int getStringHeight(Paint paint) {
        Paint.FontMetrics fr = paint.getFontMetrics();
        //ceil() 函数向上舍入为最接近的整数。
        return (int) Math.ceil(fr.descent - fr.top) + 2;
    }

    /**
     * 获取随机速度
     *
     * @return 随机速度
     */
    private int getRandomSpeed() {
        return (int) (5 * Math.random() + 5);
    }

    /**
     * 获取随机Y
     *
     * @return 随机Y
     */
    private int getRandomY(int height, @Position int position) {
        int tempTextHeight = textHeight / 2;
        height -= tempTextHeight;
        double random = Math.random() * 1000;
        switch (position) {
            case Position.BOTTOM:
                return (int) (height * random / 3000) + height * 2 / 3 + tempTextHeight;
            case Position.CENTER:
                return (int) (height * random / 3000) + height / 3 + tempTextHeight;
            case Position.ALL:
                return (int) (height * Math.random()) + tempTextHeight;
            case Position.TOP:
                return (int) (height * random / 3000) + tempTextHeight;
        }
        return (int) (height * random / 1000) + tempTextHeight;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mHandler.removeCallbacks(mRunnable);
        if (hasWindowFocus) {
            mHandler.postDelayed(mRunnable, INTERVAL);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        super.onDetachedFromWindow();
    }

    private class BarrageModel {
        private int speed;//移动速度
        private int x;//x坐标
        private int y;//y坐标
        private int width;//文字宽度
        private String text;//文字
        private float size;//画笔字体大小
        private int textColor;
        private int alpha;//文字透明度

        public BarrageModel(int speed, int x, int y, int width, String text, float size, int textColor, int alpha) {
            this.speed = speed;
            this.x = x;
            this.y = y;
            this.width = width;
            this.text = text;
            this.size = size;
            this.textColor = textColor;
            this.alpha = alpha;
        }
    }
}