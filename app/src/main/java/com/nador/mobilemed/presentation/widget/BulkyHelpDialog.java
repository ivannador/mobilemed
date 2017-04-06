package com.nador.mobilemed.presentation.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nador.mobilemed.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nador on 05/12/2016.
 */

public class BulkyHelpDialog {

    private Dialog mDialog;
    private BulkyHelpDialog.BulkyDialogView mView;

    public BulkyHelpDialog(Context context, String mainTitle) {
        mView = new BulkyHelpDialog.BulkyDialogView(context, mainTitle);
        mDialog = new Dialog(context, R.style.BulkyDialog);
        mDialog.setContentView(mView);

        // Add default dismiss action for Toast-like mechanics
        mView.addCancelAction(v -> dismiss());
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

        @Bind(R.id.imageView)
        ImageView mImageView;
        @Bind(R.id.dialogTextView)
        TextView mTextView;
        @Bind(R.id.cancelButton)
        ImageButton mCancelButton;

        public BulkyDialogView(Context context, String mainTitle) {
            super(context);
            inflate(context);
            mTextView.setText(mainTitle);
        }

        private void inflate(Context context) {
            inflate(context, R.layout.dialog_bulky_help, this);
            ButterKnife.bind(this);
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

            Glide.with(context)
                    .load(R.raw.ripple)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mImageView);
        }

        public void addCancelAction(OnClickListener listener) {
            mCancelButton.setOnClickListener(listener);
        }
    }
}
