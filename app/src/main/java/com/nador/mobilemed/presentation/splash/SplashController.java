package com.nador.mobilemed.presentation.splash;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.nador.mobilemed.presentation.dashboard.DashboardController;
import com.nador.mobilemed.presentation.presenter.SplashControllerPresenter;
import com.nador.mobilemed.R;
import com.nador.mobilemed.presentation.base.BaseController;
import com.nador.mobilemed.presentation.login.LoginController;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class SplashController extends BaseController<ISplashController, SplashControllerPresenter> implements ISplashController {

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.progressPercentTextView)
    TextView mProgressPercentTextView;

    private int mProgress = 0;

    public SplashController() {
        // Required empty public constructor
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_splash, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        updateProgressbar(mProgress);
        getPresenter().initialize();
    }

    @Override
    protected void onDetach(@NonNull View view) {
        super.onDetach(view);
    }

    @NonNull
    @Override
    public SplashControllerPresenter createPresenter() {
        return new SplashControllerPresenter(getActivity());
    }

    @Override
    public void setProgress(final int progress) {
        mProgress = progress;
        updateProgressbar(mProgress);
    }

    @Override
    public void dataInitialized() {
        navigateToDashboard();
    }

    @Override
    public void dataInitializedLoginNeeded() {
        navigateToLogin();
    }

    @Override
    public void userReauthenticated() {
        navigateToTargetController();
    }

    @Override
    public void userReauthLoginNeeded() {
        navigateToLogin();
    }

    private void navigateToLogin() {
        getRouter().setRoot(RouterTransaction.with(new LoginController())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    private void navigateToDashboard() {
        getRouter().replaceTopController(RouterTransaction.with(new DashboardController())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    private void navigateToTargetController() {
        getRouter().popController(this);
    }

    private void updateProgressbar(final int progress) {
        mProgressBar.setProgress(progress);
        mProgressPercentTextView.setText(String.valueOf(mProgressBar.getProgress()) + "%");
    }
}
