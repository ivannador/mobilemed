package com.nador.mobilemed.presentation.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nador.mobilemed.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nador on 29/08/16.
 */
public class BulkyMultiDialog {

    private Dialog mDialog;
    private BulkyDialogView mView;

    public BulkyMultiDialog(Context context, String mainTitle) {
        mView = new BulkyDialogView(context, mainTitle);
        mDialog = new Dialog(context, R.style.BulkyDialog);
        mDialog.setContentView(mView);
    }

    public BulkyMultiDialog addAction(String title, View.OnClickListener listener) {
        mView.addAction(title, listener);
        return this;
    }

    public BulkyMultiDialog addCancelAction(View.OnClickListener listener) {
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

    public static class BulkyDialogView extends RelativeLayout {

        @Bind(R.id.choice1Button)
        Button mChoice1Button;
        @Bind(R.id.choice2Button)
        Button mChoice2Button;
        @Bind(R.id.choice3Button)
        Button mChoice3Button;
        @Bind(R.id.cancelButton)
        ImageButton mCancelButton;
        @Bind(R.id.dialogTextView)
        TextView mTextView;

        public BulkyDialogView(Context context, String mainTitle) {
            super(context);
            inflate(context);
            mTextView.setText(mainTitle);
            mChoice1Button.setVisibility(GONE);
            mChoice2Button.setVisibility(GONE);
            mChoice3Button.setVisibility(GONE);
            mCancelButton.setVisibility(INVISIBLE);
        }

        private void inflate(Context context) {
            inflate(context, R.layout.dialog_bulky_multi, this);
            ButterKnife.bind(this);
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        public void addAction(String title, OnClickListener listener) {
            if (mChoice1Button.getVisibility() == GONE) {
                mChoice1Button.setText(title);
                mChoice1Button.setOnClickListener(listener);
                mChoice1Button.setVisibility(VISIBLE);
            } else if (mChoice2Button.getVisibility() == GONE) {
                mChoice2Button.setText(title);
                mChoice2Button.setOnClickListener(listener);
                mChoice2Button.setVisibility(VISIBLE);
            } else {
                mChoice3Button.setText(title);
                mChoice3Button.setOnClickListener(listener);
                mChoice3Button.setVisibility(VISIBLE);
            }
        }

        public void addCancelAction(OnClickListener listener) {
            mCancelButton.setOnClickListener(listener);
            mCancelButton.setVisibility(VISIBLE);
        }
    }
}
