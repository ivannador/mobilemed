package com.nador.mobilemed.presentation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayout;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

import com.nador.mobilemed.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by nador on 28/06/16.
 */
public class NumberPad extends GridLayout {

    @OnClick(R.id.oneButton)
    protected void onOneButtonTapped() {
        appendNumber(mTargetEditText, "1");
    }

    @OnClick(R.id.twoButton)
    protected void onTwoButtonTapped() {
        appendNumber(mTargetEditText, "2");
    }

    @OnClick(R.id.threeButton)
    protected void onThreeButtonTapped() {
        appendNumber(mTargetEditText, "3");
    }

    @OnClick(R.id.fourButton)
    protected void onFourButtonTapped() {
        appendNumber(mTargetEditText, "4");
    }

    @OnClick(R.id.fiveButton)
    protected void onFiveButtonTapped() {
        appendNumber(mTargetEditText, "5");
    }

    @OnClick(R.id.sixButton)
    protected void onSixButtonTapped() {
        appendNumber(mTargetEditText, "6");
    }

    @OnClick(R.id.sevenButton)
    protected void onSevenButtonTapped() {
        appendNumber(mTargetEditText, "7");
    }

    @OnClick(R.id.eightButton)
    protected void onEightButtonTapped() {
        appendNumber(mTargetEditText, "8");
    }

    @OnClick(R.id.nineButton)
    protected void onNineButtonTapped() {
        appendNumber(mTargetEditText, "9");
    }

    @OnClick(R.id.zeroButton)
    protected void onZeroButtonTapped() {
        if (mTargetEditText.getText().length() != 0) {
            appendNumber(mTargetEditText, "0");
        }
    }

    @Bind(R.id.dotButton)
    Button mDotButton;
    @OnClick(R.id.dotButton)
    protected void onDotButtonTapped() {
        if (mTargetEditText.getText().length() != 0 && !mHasDot) {
            mTargetEditText.append(".0");
            mHasDot = true;
        }
    }

    @OnClick(R.id.clearButton)
    protected void onClearButtonTapped() {
        Editable editable = mTargetEditText.getText();
        int charCount = editable.length();
        if (charCount > 0) {
            if (charCount > 1 && editable.charAt(charCount - 2) == '.') {
                editable.delete(charCount - 2, charCount);
                mHasDot = false;
            } else {
                editable.delete(charCount - 1, charCount);
            }
        }
    }
    @OnLongClick(R.id.clearButton)
    protected boolean onClearButtonLongTapped() {
        mTargetEditText.getText().clear();
        return true;
    }

    private EditText mTargetEditText;
    // State for having dot in input
    private boolean mHasDot = false;

    private boolean mShowDotButton;

    public NumberPad(Context context) {
        super(context, null, R.attr.numberPadStyle);
        inflate(context);
    }

    public NumberPad(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.numberPadStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NumberPad,
                0, 0);

        try {
            mShowDotButton = a.getBoolean(R.styleable.NumberPad_showDotButton, true);
        } finally {
            a.recycle();
        }

        inflate(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }

    public void attachTargetEditText(final EditText editText) {
        mTargetEditText = editText;
    }

    private void inflate(Context context) {
        inflate(getContext(), R.layout.widget_numberpad, this);
        ButterKnife.bind(this);

        mDotButton.setVisibility(mShowDotButton ? VISIBLE : INVISIBLE);

        // FIXME: why don't these work as styleable attributes?
        setOrientation(HORIZONTAL);
        setColumnCount(3);
    }

    /**
     * Append number with checking fraction part
     *
     * @param targetEditText
     * @param number
     */
    private void appendNumber(final EditText targetEditText, final String number) {
        Editable editable = targetEditText.getText();
        int charCount = editable.length();
        // Try to determine if we are in the fraction part of the input number
        if (charCount > 1 && editable.charAt(charCount - 2) == '.' && editable.charAt(charCount - 1) == '0') {
            editable.replace(charCount - 1, charCount, number);
        } else {
            targetEditText.append(number);
        }
    }
}
