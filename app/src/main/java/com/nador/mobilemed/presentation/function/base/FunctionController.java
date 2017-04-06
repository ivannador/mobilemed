package com.nador.mobilemed.presentation.function.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.nador.mobilemed.R;
import com.nador.mobilemed.presentation.base.BaseController;
import com.nador.mobilemed.presentation.widget.BulkyHelpDialog;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by nador on 18/07/16.
 */
public abstract class FunctionController<V extends MvpView, P extends MvpBasePresenter<V>> extends BaseController<V, P> {

    private final static String KEY_CURRENT_TITLE = "KEY_CURRENT_TITLE";
    private final static String KEY_CURRENT_BACK_TITLE = "KEY_CURRENT_BACK_TITLE";

    // Must implement in subclass layout
    @Bind(R.id.containerLayout)
    protected ViewGroup mContainerLayout;

    @Bind(R.id.backButton)
    protected Button mBackButton;
    @Bind(R.id.functionTitleTextView)
    protected TextView mFunctionTitleTextView;

    @OnClick(R.id.supportButton)
    protected void onSupportButtonTapped() {
        BulkyHelpDialog helpDialog = new BulkyHelpDialog(getActivity(), "Connecting to assistance...");
        helpDialog.show();
    }
    @OnClick(R.id.helpButton)
    protected void onHelpButtonTapped() {
        BulkyHelpDialog helpDialog = new BulkyHelpDialog(getActivity(), "Connecting to assistance...");
        helpDialog.show();
    }
    @OnClick(R.id.backButton)
    protected void onBackButtonTapped() {
        final Router childRouter = getChildRouter(mContainerLayout, null);
        if (childRouter.getBackstackSize() != 0) {
            childRouter.popCurrentController();

            if (childRouter.getBackstackSize() == 0) {
                setMainTitle();
                mBackButton.setText(getActivity().getString(R.string.FUNCTION_BUTTON_BACK_HOME));
            }
        } else {
            getRouter().popController(this);
        }
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setMainTitle();
    }

    @Override
    protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
        super.onSaveViewState(view, outState);

        outState.putString(KEY_CURRENT_TITLE, mFunctionTitleTextView.getText().toString());
        outState.putString(KEY_CURRENT_BACK_TITLE, mBackButton.getText().toString());
    }

    @Override
    protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
        super.onRestoreViewState(view, savedViewState);

        mFunctionTitleTextView.setText(savedViewState.getString(KEY_CURRENT_TITLE));
        mBackButton.setText(savedViewState.getString(KEY_CURRENT_BACK_TITLE));
    }

    protected abstract void setMainTitle();
}
