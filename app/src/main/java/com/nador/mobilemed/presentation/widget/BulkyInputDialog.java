package com.nador.mobilemed.presentation.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nador.mobilemed.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nador on 12/09/16.
 */
public class BulkyInputDialog {

    public Dialog mDialog;
    private BulkyDialogView mView;

    public BulkyInputDialog(Context context, String mainTitle) {
        mView = new BulkyDialogView(context, mainTitle);
        mDialog = new Dialog(context, R.style.BulkyDialog);

        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mDialog.setContentView(mView);

        WindowManager.LayoutParams attrs = mDialog.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public BulkyInputDialog addAffirmativeAction(String title, View.OnClickListener listener) {
        mView.addAffirmativeAction(title, listener);
        return this;
    }

    public BulkyInputDialog addCancelAction(View.OnClickListener listener) {
        mView.addCancelAction(listener);
        return this;
    }

    public BulkyInputDialog setFirstEditText(final String hint, boolean isPassword) {
        mView.setFirstEditText(hint, isPassword);
        return this;
    }

    public BulkyInputDialog setSecondEditText(final String hint, boolean isPassword) {
        mView.setSecondEditText(hint, isPassword);
        return this;
    }

    public BulkyInputDialog setThirdEditText(final String hint, boolean isPassword) {
        mView.setThirdEditText(hint, isPassword);
        return this;
    }

    public String getFirstInput() {
        return mView.mFirstEditText.getText().toString();
    }

    public String getSecondInput() {
        return mView.mSecondEditText.getText().toString();

    }

    public String getThirdInput() {
        return mView.mThirdEditText.getText().toString();
    }

    public void show() {
        mDialog.show();
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
        @Bind(R.id.firstEditText)
        EditText mFirstEditText;
        @Bind(R.id.secondEditText)
        EditText mSecondEditText;
        @Bind(R.id.thirdEditText)
        EditText mThirdEditText;

        public BulkyDialogView(Context context, String mainTitle) {
            super(context);
            inflate(context);
            mTextView.setText(mainTitle);
            mAffirmativeButton.setVisibility(INVISIBLE);
            mCancelButton.setVisibility(INVISIBLE);
            mFirstEditText.setVisibility(GONE);
            mSecondEditText.setVisibility(GONE);
            mThirdEditText.setVisibility(GONE);
        }

        private void inflate(Context context) {
            inflate(context, R.layout.dialog_bulky_input, this);
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

        public void setFirstEditText(final String hint, boolean isPassword) {
            mFirstEditText.setHint(hint);
            mFirstEditText.setInputType(isPassword ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
            mFirstEditText.setVisibility(VISIBLE);
        }

        public void setSecondEditText(final String hint, boolean isPassword) {
            mSecondEditText.setHint(hint);
            mSecondEditText.setInputType(isPassword ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
            mSecondEditText.setVisibility(VISIBLE);
        }

        public void setThirdEditText(final String hint, boolean isPassword) {
            mThirdEditText.setHint(hint);
            mThirdEditText.setInputType(isPassword ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
            mThirdEditText.setVisibility(VISIBLE);
        }
    }
}
