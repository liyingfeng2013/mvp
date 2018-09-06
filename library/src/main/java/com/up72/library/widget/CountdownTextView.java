package com.up72.library.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.up72.library.R;

/**
 * 倒计时控件
 * Created by liyingfeng on 2016/6/13.
 */
public class CountdownTextView extends android.support.v7.widget.AppCompatTextView {
    private static final int INTERVAL = 1000;
    private String key = "CountdownTextView";
    private int mCountdownSecond = 60;//倒计时秒数
    private long endTime;//倒计时结束时间
    private String textStart = "";
    private String textEnd = "";
    private String textContent = "";

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int restTime = (int) ((endTime - System.currentTimeMillis()) / 1000);
            if (restTime <= 0) {
                mHandler.sendEmptyMessage(1);
            } else {
                mHandler.postDelayed(this, INTERVAL);
                mHandler.sendMessage(mHandler.obtainMessage(2, restTime));
            }
        }
    };

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setEnabled(true);
                    setText(textContent);
                    break;
                case 2:
                    setEnabled(false);
                    if (msg.obj instanceof Integer) {
                        String text = String.valueOf(msg.obj);
                        if (textStart != null) {
                            text = textStart + text;
                        }
                        if (textEnd != null) {
                            text += textEnd;
                        }
                        setText(text);
                    }
                    break;
            }
            return false;
        }
    });

    public CountdownTextView(Context context) {
        super(context);
        init(null);
    }

    public CountdownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CountdownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null && getContext() != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountdownTextView);
            if (typedArray != null) {
                mCountdownSecond = typedArray.getInt(R.styleable.CountdownTextView_maxTime, 60);
                String tempKey = typedArray.getString(R.styleable.CountdownTextView_key);
                if (tempKey != null) {
                    key += tempKey;
                }
                textStart = typedArray.getString(R.styleable.CountdownTextView_countdownTextStart);
                textEnd = typedArray.getString(R.styleable.CountdownTextView_countdownTextEnd);

                typedArray.recycle();
            }
        }
        textContent = getText().toString();
        endTime = read(getContext(), key, 0);
    }

    /**
     * 开始倒计时
     */
    public void start() {
        if (isEnabled()) {
            endTime = System.currentTimeMillis() + mCountdownSecond * 1000;
            textContent = getText().toString();
            mHandler.postDelayed(mRunnable, 0);
            write(getContext(), key, endTime);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mHandler.removeCallbacks(mRunnable);
        if (hasWindowFocus) {
            if (endTime > 0) {
                mHandler.postDelayed(mRunnable, 0);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        mHandler = null;
        mRunnable = null;
        super.onDetachedFromWindow();
    }

    private boolean write(Context context, String key, long value) {
        if (context == null || key == null) {
            return false;
        }
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = spf.edit();
        edit.putLong(key, value);
        return edit.commit();
    }

    private long read(Context context, String key, long defaultValue) {
        SharedPreferences spf1 = PreferenceManager.getDefaultSharedPreferences(context);
        return spf1.getLong(key, defaultValue);
    }
}