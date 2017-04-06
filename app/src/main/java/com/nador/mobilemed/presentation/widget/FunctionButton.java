package com.nador.mobilemed.presentation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nador.mobilemed.R;
import com.nador.mobilemed.presentation.utils.DateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nador on 16/06/16.
 */
public class FunctionButton extends RelativeLayout {

    @Bind(R.id.titleTextView)
    TextView mTitleTextView;
    @Bind(R.id.lastUsedDateTextView)
    TextView mLastUsedDateTextView;
    @Bind(R.id.imageView)
    ImageView mImageView;

    private int mImageLeftMargin;
    private int mImageRightMargin;
    private int mImageTopMargin;
    private int mImageBottomMargin;

    private int mLastUsedTextBottomMargin;

    private int mTitleTextSize;
    private int mLastUsedTextSize;

    private boolean mLastUsedTextVisible;

    private Drawable mImage;

    private String mTitle;
    private String mLastUsed;

    public FunctionButton(Context context) {
        super(context);
        inflate(context);
    }

    public FunctionButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FunctionButton,
                0, 0);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        try {
            mImage = a.getDrawable(R.styleable.FunctionButton_image);
            mImageLeftMargin = a.getDimensionPixelSize(R.styleable.FunctionButton_imageLeftMargin, 0);
            mImageRightMargin = a.getDimensionPixelSize(R.styleable.FunctionButton_imageRightMargin, 0);
            mImageTopMargin = a.getDimensionPixelSize(R.styleable.FunctionButton_imageTopMargin, 0);
            mImageBottomMargin = a.getDimensionPixelSize(R.styleable.FunctionButton_imageBottomMargin, 0);
            mTitle = a.getString(R.styleable.FunctionButton_titleLabel);
            mLastUsed = a.getString(R.styleable.FunctionButton_lastUsed);
            mTitleTextSize = a.getDimensionPixelSize(R.styleable.FunctionButton_titleTextSize, 10);
            mLastUsedTextSize = a.getDimensionPixelSize(R.styleable.FunctionButton_lastUsedTextSize, 6);
            mLastUsedTextBottomMargin = a.getDimensionPixelSize(R.styleable.FunctionButton_lastUsedTextBottomMargin, 20);
            mLastUsedTextVisible = a.getBoolean(R.styleable.FunctionButton_lastUsedTextVisible, true);
        } finally {
            a.recycle();
        }

        inflate(context);
    }

    public void setActive() {
        setEnabled(true);
        setClickable(true);
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.teldoc_button_function));
        mLastUsedDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlueLight));
        mImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorBlueLight));
        setTextsAlignment(mLastUsedTextVisible);
    }

    public void setInactive() {
        setEnabled(false);
        setClickable(false);
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.teldoc_button_function_inactive));
        mLastUsedDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreyLight));
        mImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyLight));
        setTextsAlignment(mLastUsedTextVisible);
    }

    public void setImportant() {
        setEnabled(true);
        setClickable(true);
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.teldoc_button_function_red));
        mImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorRedMedium));
        setTextsAlignment(false);
    }

    public void setLastUsedText(final long timestamp) {
        mLastUsed = (timestamp == 0) ?
                "" : getContext().getString(R.string.DASHBOARD_TEXT_LAST_USE)
                + " "
                + DateUtil.getDateString(timestamp);
        mLastUsedDateTextView.setText(mLastUsed);
    }

    private void inflate(Context context) {
        inflate(context, R.layout.button_function, this);
        ButterKnife.bind(this);

        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins((int) mImageLeftMargin, (int) mImageTopMargin, (int) mImageRightMargin, (int) mImageBottomMargin);
        mImageView.setLayoutParams(params);
        mImageView.setImageDrawable(mImage);

        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
        mLastUsedDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLastUsedTextSize);

        LayoutParams lastusedTextParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        lastusedTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lastusedTextParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lastusedTextParams.setMargins(0, 0, 0, (int) mLastUsedTextBottomMargin);
        mLastUsedDateTextView.setLayoutParams(lastusedTextParams);

        mTitleTextView.setText(mTitle);
        mLastUsedDateTextView.setText("");

        setInactive();
    }

    private void setTextsAlignment(boolean lastUsedVisible) {
        if (lastUsedVisible) {
            LayoutParams titleTextParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            titleTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            titleTextParams.addRule(RelativeLayout.ABOVE, R.id.lastUsedDateTextView);
            mTitleTextView.setLayoutParams(titleTextParams);

            mLastUsedDateTextView.setVisibility(View.VISIBLE);
        } else {

            LayoutParams titleTextParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            titleTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            titleTextParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            titleTextParams.setMargins(0, 0, 0, (int) (mLastUsedTextBottomMargin + mLastUsedTextSize + 10));
            mTitleTextView.setLayoutParams(titleTextParams);

            mLastUsedDateTextView.setVisibility(View.GONE);
        }
    }
}
