package com.nador.mobilemed.presentation.function.measurement;

import android.support.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.nador.mobilemed.presentation.presenter.function.measurement.GlucometerFunctionControllerPresenter;
import com.nador.mobilemed.R;
import com.nador.mobilemed.presentation.function.base.MeasurementFunctionController;

/**
 * Created by nador on 14/07/16.
 */
public class GlucometerFunctionController extends MeasurementFunctionController<IGlucometerFunctionController, GlucometerFunctionControllerPresenter> implements IGlucometerFunctionController {

    @NonNull
    @Override
    public GlucometerFunctionControllerPresenter createPresenter() {
        return new GlucometerFunctionControllerPresenter(getActivity());
    }

    @Override
    protected final void setMainTitle() {
        mFunctionTitleTextView.setText(getActivity().getString(R.string.FUNCTION_TITLE_GLUCOMETER));
        mMeasurementTitleTextView.setText(getActivity().getString(R.string.FUNCTION_TEXT_GLUCOMETER));
    }

    @Override
    protected void setAutomaticTitle() {
        mFunctionTitleTextView.setText(getActivity().getString(R.string.MEASUREMENT_TITLE_AUTOMATIC_GLUCOMETER));
    }

    @Override
    protected void setManualTitle() {
        mFunctionTitleTextView.setText(getActivity().getString(R.string.MEASUREMENT_TITLE_MANUAL_GLUCOMETER));
    }

    @Override
    protected final void navigateToAutomaticMeasurement() {
        super.navigateToAutomaticMeasurement();
        Controller controller = new GlucometerMeasurementController.AutomaticMeasurement();
        controller.setRetainViewMode(RetainViewMode.RETAIN_DETACH);
        getChildRouter(mContainerLayout, null).setPopsLastView(true).pushController(RouterTransaction.with(controller)
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override
    protected final void navigateToManualMeasurement() {
        super.navigateToManualMeasurement();
        Controller controller = new GlucometerMeasurementController.ManualMeasurement();
        controller.setRetainViewMode(RetainViewMode.RETAIN_DETACH);
        getChildRouter(mContainerLayout, null).setPopsLastView(true).pushController(RouterTransaction.with(controller)
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }
}
