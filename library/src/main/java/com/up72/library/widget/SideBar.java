package com.up72.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SideBar extends View {
    private static final String FILLER = "";
    private static final int defaultCount = 27;//默认27个字符高度排列
    private int defaultColor = Color.GRAY;
    private int selectColor = Color.BLUE;
    private float textSize = 32;
    private int choose = -1;
    private Paint paint = new Paint();
    private TextView mTextDialog;
    private OnSelectListener onSelectListener;

    public String[] b = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
    };

    public void setTextDialog(@Nullable TextView textView) {
        this.mTextDialog = textView;
        if (mTextDialog != null) {
            mTextDialog.setVisibility(GONE);
        }
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    public void setStrings(String[] strings) {
        if (strings != null && strings.length > 0) {
            int length = strings.length;
            int def = defaultCount - length;
            if (def > 0) {
                int startIndex = def / 2;
                int endIndex = startIndex + length;
                String[] newStrings = new String[defaultCount];
                for (int i = 0; i < defaultCount; i++) {
                    if (i >= startIndex && i < endIndex) {
                        newStrings[i] = strings[i - startIndex];
                    } else {
                        newStrings[i] = FILLER;
                    }
                }
                b = newStrings;
            } else {
                b = strings;
            }
            choose = -1;
            invalidate();
        }
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setDefaultColor(@ColorInt int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setSelectColor(@ColorInt int selectColor) {
        this.selectColor = selectColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        float singleHeight = height / b.length;
        for (int i = 0; i < b.length; i++) {
            paint.setColor(defaultColor);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);
            if (i == choose) {
                paint.setColor(selectColor);
                paint.setFakeBoldText(true);
            }
            float x = width / 2 - paint.measureText(b[i]) / 2;
            float y = singleHeight * i + singleHeight / 2 + paint.getTextSize() / 2;
            canvas.drawText(b[i], x, y, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnSelectListener changedListener = onSelectListener;
        final int letterPos = (int) (y / getHeight() * b.length);
        switch (action) {
            case MotionEvent.ACTION_UP:
                choose = -1;
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.GONE);
                }
                break;
            default:
                if (oldChoose != letterPos) {
                    if (letterPos >= 0 && letterPos < b.length) {
                        String temp = b[letterPos];
                        if (!temp.equals(FILLER)) {
                            if (changedListener != null) {
                                changedListener.onSelect(temp);
                            }
                            if (mTextDialog != null) {
                                mTextDialog.setText(temp);
                                mTextDialog.setVisibility(View.VISIBLE);
                            }
                            choose = letterPos;
                            invalidate();
                        }
                    }
                }
                break;
        }
        return true;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void onSelect(String string);
    }
}