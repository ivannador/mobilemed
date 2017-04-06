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
 * Created by nador on 16/06/16.
 */
public class BulkyDialog {

    private Dialog mDialog;
    private BulkyDialogView mView;

    public BulkyDialog(Context context, String mainTitle) {
        mView = new BulkyDialogView(context, mainTitle);
        mDialog = new Dialog(context, R.style.BulkyDialog);
        mDialog.setContentView(mView);
    }

    public BulkyDialog addAffirmativeAction(String title, View.OnClickListener listener) {
        mView.addAffirmativeAction(title, listener);
        return this;
    }

    public BulkyDialog addCancelAction(View.OnClickListener listener) {
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

        @Bind(R.id.affirmativeButton)
        Button mAffirmativeButton;
        @Bind(R.id.cancelButton)
        ImageButton mCancelButton;
        @Bind(R.id.dialogTextView)
        TextView mTextView;

        public BulkyDialogView(Context context, String mainTitle) {
            super(context);
            inflate(context);
            mTextView.setText(mainTitle);
            mAffirmativeButton.setVisibility(INVISIBLE);
            mCancelButton.setVisibility(INVISIBLE);
        }

        private void inflate(Context context) {
            inflate(context, R.layout.dialog_bulky, this);
            ButterKnife.bind(this);
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
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
    }
}
