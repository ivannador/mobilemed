package com.nador.mobilemed.presentation.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.conductor.MvpController;
import com.nador.mobilemed.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nador on 23/05/16.
 */
public abstract class BaseController<V extends MvpView, P extends MvpPresenter<V>> extends MvpController<V, P> {

    @Nullable
    @Bind(R.id.loadingView)
    protected FrameLayout mLoadingView;

    private boolean mInTransition = false;
    private boolean mChildInTransition = false;

    protected abstract View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container);

    @NonNull
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflateView(inflater, container);
        ButterKnife.bind(this, view);
        onViewBound(view);
        return view;
    }

    protected void onViewBound(@NonNull View view) {}

    @Override
    protected void onDestroyView(View view) {
        super.onDestroyView(view);
        ButterKnife.unbind(this);
    }

    @Override
    protected void onChangeStarted(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
        super.onChangeStarted(changeHandler, changeType);
        setInTransition(true);
    }

    @Override
    protected void onChangeEnded(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
        super.onChangeEnded(changeHandler, changeType);
        setInTransition(false);
    }

    protected void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    protected void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    protected void setInTransition(boolean inTransition) {
        mInTransition = inTransition;
    }

    protected void setChildInTransition(boolean childInTransition) {
        mChildInTransition = childInTransition;
    }

    public boolean isInTransition() {
        return mInTransition;
    }
    public boolean isChildInTransition() {
        return mChildInTransition;
    }
}
