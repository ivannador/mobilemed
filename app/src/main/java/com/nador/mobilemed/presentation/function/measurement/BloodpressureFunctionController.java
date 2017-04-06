package com.nador.mobilemed.presentation.function.measurement;

import android.support.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.nador.mobilemed.presentation.presenter.function.measurement.BloodpressureFunctionControllerPresenter;
import com.nador.mobilemed.R;
import com.nador.mobilemed.presentation.function.base.MeasurementFunctionController;

/**
 * Class for handling blood pressure related operations
 *
 * Created by nador on 27/06/16.
 */
public class BloodpressureFunctionController extends MeasurementFunctionController<IBloodpressureFunctionController, BloodpressureFunctionControllerPresenter> implements IBloodpressureFunctionController {

    @NonNull
    @Override
    public BloodpressureFunctionControllerPresenter createPresenter() {
        return new BloodpressureFunctionControllerPresenter(getActivity());
    }

    @Override
    protected final void setMainTitle() {
        mFunctionTitleTextView.setText(getActivity().getString(R.string.FUNCTION_TITLE_BLOOD_PRESSURE));
        mMeasurementTitleTextView.setText(getActivity().getString(R.string.FUNCTION_TEXT_BLOOD_PRESSURE));
    }

    @Override
    protected void setAutomaticTitle() {
        mFunctionTitleTextView.setText(getActivity().getString(R.string.MEASUREMENT_TITLE_AUTOMATIC_BLOOD_PRESSURE));
    }

    @Override
    protected void setManualTitle() {
        mFunctionTitleTextView.setText(getActivity().getString(R.string.MEASUREMENT_TITLE_MANUAL_BLOOD_PRESSURE));
    }

    @Override
    protected final void navigateToAutomaticMeasurement() {
        super.navigateToAutomaticMeasurement();
        Controller controller = new BloodpressureMeasurementController.AutomaticMeasurement();
        controller.setRetainViewMode(RetainViewMode.RETAIN_DETACH);
        getChildRouter(mContainerLayout, null).setPopsLastView(true).pushController(RouterTransaction.with(controller)
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override
    protected final void navigateToManualMeasurement() {
        super.navigateToManualMeasurement();
        Controller controller = new BloodpressureMeasurementController.ManualMeasurement();
        controller.setRetainViewMode(RetainViewMode.RETAIN_DETACH);
        getChildRouter(mContainerLayout, null).setPopsLastView(true).pushController(RouterTransaction.with(controller)
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }
}
