package com.nador.mobilemed.presentation.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nador.mobilemed.R;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.ButterKnife;

/**
 * Created by nador on 25/07/16.
 */
public class BulkySliderDialog {

    private Dialog mDialog;
    private BulkyDialogView mView;

    public BulkySliderDialog(Context context, final String hintText) {
        mView = new BulkyDialogView(context, hintText);
        mDialog = new Dialog(context, R.style.BulkyDialog);
        mDialog.setContentView(mView);
    }

    public BulkySliderDialog addAffirmativeAction(String title, View.OnClickListener listener) {
        mView.addAffirmativeAction(title, listener);
        return this;
    }

    public BulkySliderDialog addCancelAction(View.OnClickListener listener) {
        mView.addCancelAction(listener);
        return this;
    }

    public void show() {
        mDialog.show();

        // Make the dialog fill the screen (must be called AFTER show())
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = mDialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public int getSliderProgress() {
        return mView.getSliderProgress();
    }

    public int getSliderColor() {
        return mView.getSliderColor();
    }

    public static class BulkyDialogView extends RelativeLayout {

        private static final int MAX_SEEKBAR = 10;

        @Bind(R.id.hint2TextView)
        TextView mHint2TextView;
        @Bind(R.id.affirmativeButton)
        Button mAffirmativeButton;
        @Bind(R.id.cancelButton)
        ImageButton mCancelButton;
        @Bind(R.id.painLevelSeekBar)
        SeekBar mPainLevelSeekBar;

        @BindColor(R.color.colorPainLevel0)
        int mPainLevel0Color;
        @BindColor(R.color.colorPainLevel1)
        int mPainLevel1Color;
        @BindColor(R.color.colorPainLevel2)
        int mPainLevel2Color;
        @BindColor(R.color.colorPainLevel3)
        int mPainLevel3Color;
        @BindColor(R.color.colorPainLevel4)
        int mPainLevel4Color;
        @BindColor(R.color.colorPainLevel5)
        int mPainLevel5Color;
        @BindColor(R.color.colorPainLevel6)
        int mPainLevel6Color;
        @BindColor(R.color.colorPainLevel7)
        int mPainLevel7Color;
        @BindColor(R.color.colorPainLevel8)
        int mPainLevel8Color;
        @BindColor(R.color.colorPainLevel9)
        int mPainLevel9Color;
        @BindColor(R.color.colorPainLevel10)
        int mPainLevel10Color;
        @BindColor(R.color.colorGreyLight)
        int mPainLevelBackgroundColor;

        @BindDrawable(R.drawable.dialog_slider_thumb_1)
        Drawable mThumb0;
        @BindDrawable(R.drawable.dialog_slider_thumb_2)
        Drawable mThumb1;
        @BindDrawable(R.drawable.dialog_slider_thumb_3)
        Drawable mThumb2;
        @BindDrawable(R.drawable.dialog_slider_thumb_4)
        Drawable mThumb3;
        @BindDrawable(R.drawable.dialog_slider_thumb_5)
        Drawable mThumb4;
        @BindDrawable(R.drawable.dialog_slider_thumb_6)
        Drawable mThumb5;
        @BindDrawable(R.drawable.dialog_slider_thumb_7)
        Drawable mThumb6;
        @BindDrawable(R.drawable.dialog_slider_thumb_8)
        Drawable mThumb7;
        @BindDrawable(R.drawable.dialog_slider_thumb_9)
        Drawable mThumb8;
        @BindDrawable(R.drawable.dialog_slider_thumb_10)
        Drawable mThumb9;
        @BindDrawable(R.drawable.dialog_slider_thumb_11)
        Drawable mThumb10;

        @BindDimen(R.dimen.radius_seekbar)
        int mRadius;

        private ClipDrawable mSeekBarGradient;
        private LayerDrawable mSeekbarDrawable;

        private HashMap<Integer, Drawable> mPainLevelThumbMap = new HashMap<>();
        private int[] mColors;

        public BulkyDialogView(Context context, final String hintText) {
            super(context);
            inflate(context);
            mHint2TextView.setText(hintText);
            mAffirmativeButton.setVisibility(INVISIBLE);
            mCancelButton.setVisibility(INVISIBLE);
        }

        private void inflate(Context context) {
            inflate(context, R.layout.dialog_bulky_slider, this);
            ButterKnife.bind(this);
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            initSeekbar();
        }

        public void addAffirmativeAction(String title, OnClickListener listener) {
            mAffirmativeButton.setText(title);
            mAffirmativeButton.setOnClickListener(listener);
            mAffirmativeButton.setVisibility(VISIBLE);
        }

        public void addCancelAction(OnClickListener listener) {
            mCancelButton.setOnClickListener(listener);
            mCancelButton.setVisibility(VISIBLE);
        }

        public int getSliderProgress() {
            return mPainLevelSeekBar.getProgress();
        }

        public int getSliderColor() {
            return mColors[mPainLevelSeekBar.getProgress()];
        }

        private void initSeekbar() {
            mPainLevelThumbMap.put(0, mThumb0);
            mPainLevelThumbMap.put(1, mThumb1);
            mPainLevelThumbMap.put(2, mThumb2);
            mPainLevelThumbMap.put(3, mThumb3);
            mPainLevelThumbMap.put(4, mThumb4);
            mPainLevelThumbMap.put(5, mThumb5);
            mPainLevelThumbMap.put(6, mThumb6);
            mPainLevelThumbMap.put(7, mThumb7);
            mPainLevelThumbMap.put(8, mThumb8);
            mPainLevelThumbMap.put(9, mThumb9);
            mPainLevelThumbMap.put(10, mThumb10);

            mColors = new int[] {
                    mPainLevel0Color,
                    mPainLevel1Color,
                    mPainLevel2Color,
                    mPainLevel3Color,
                    mPainLevel4Color,
                    mPainLevel5Color,
                    mPainLevel6Color,
                    mPainLevel7Color,
                    mPainLevel8Color,
                    mPainLevel9Color,
                    mPainLevel10Color
            };
            GradientDrawable seekBarGradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mColors);
            seekBarGradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            seekBarGradient.setCornerRadius(mRadius);
            mSeekBarGradient = new ClipDrawable(seekBarGradient, Gravity.LEFT, ClipDrawable.HORIZONTAL);

            int[] bgColors = new int[] { mPainLevelBackgroundColor, mPainLevelBackgroundColor };
            GradientDrawable backgroundGradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, bgColors);
            backgroundGradient.setCornerRadius(mRadius);

            Drawable[] layers = new Drawable[] {backgroundGradient, mSeekBarGradient};
            mSeekbarDrawable = new LayerDrawable(layers);
            mPainLevelSeekBar.setProgressDrawable(mSeekbarDrawable);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            float density = metrics.density;
            float widthDp = metrics.widthPixels / density;
            // Magic numbers
            float padding = 19.f;
            if (widthDp > 600) {
//                padding = padding * 1.7f;
                padding = padding * 2.f;
            } else if (widthDp > 720) {
                padding = padding * 2.2f;
            }

            mPainLevelSeekBar.setPadding((int) (density * padding), 0, (int) (density * padding), 0);
            mPainLevelSeekBar.setMax(MAX_SEEKBAR);
            mPainLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mSeekBarGradient.setLevel(10000 * progress / MAX_SEEKBAR);
                    Drawable thumb = mPainLevelThumbMap.get(progress);
                    if (thumb != null) {
                        mPainLevelSeekBar.setThumb(thumb);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }
}
