package com.nador.mobilemed.presentation.function.measurement;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nador.mobilemed.data.utils.MeasurementValidator;
import com.nador.mobilemed.data.utils.fsm.State;
import com.nador.mobilemed.presentation.function.base.MeasurementFunctionController;
import com.nador.mobilemed.presentation.presenter.function.measurement.GlucometerMeasurementControllerPresenter;
import com.nador.mobilemed.presentation.widget.BulkyAlertDialog;
import com.nador.mobilemed.presentation.widget.StepIndicator;
import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.measurement.BloodGlucoseLevel;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.data.utils.fsm.StateContext;
import com.nador.mobilemed.presentation.MainActivity;
import com.nador.mobilemed.presentation.base.BluetoothUserController;
import com.nador.mobilemed.presentation.widget.BulkyDialog;
import com.nador.mobilemed.presentation.widget.NumberPad;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by nador on 14/07/16.
 */
public abstract class GlucometerMeasurementController extends BluetoothUserController<IGlucometerMeasurementController, GlucometerMeasurementControllerPresenter> implements IGlucometerMeasurementController, StateContext {

    @Bind(R.id.numberPad)
    NumberPad mNumberPad;

    @Bind(R.id.glucoseContainer)
    View mGlucoseContainer;
    @Bind(R.id.glucoseEditText)
    EditText mGlucoseEditText;

    @Bind(R.id.hintTextView)
    TextView mHintTextView;
    @Bind(R.id.subhintTextView)
    TextView mSubhintTextView;
    @Bind(R.id.buttonHintTextView)
    TextView mButtonHintTextView;

    @Bind(R.id.stepIndicator)
    StepIndicator mStepIndicator;

    @Bind(R.id.stepButton)
    Button mStepButton;

    @OnClick(R.id.stepButton)
    protected void onStepButtonTapped() {
        mCurrentState.onState(this);
    }

    // ------ FSM ------

    protected State mCurrentState;

    @Override
    public void state(State state) {
        mCurrentState = state;
    }

    @Override
    public State state() {
        return mCurrentState;
    }

    // ------ FSM ------

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_glucometer, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        initLayout();
    }

    @NonNull
    @Override
    public GlucometerMeasurementControllerPresenter createPresenter() {
        return new GlucometerMeasurementControllerPresenter(getActivity());
    }

    protected void initLayout() {
        Context context = getActivity();
        int currentColor = ContextCompat.getColor(context, R.color.colorBlueLight);
        mGlucoseContainer.getBackground().setColorFilter(currentColor, PorterDuff.Mode.SRC_ATOP);
        mGlucoseEditText.setTextColor(currentColor);
        mNumberPad.attachTargetEditText(mGlucoseEditText);

        mStepIndicator.setStep1State(StepIndicator.State.ACTIVE);
    }

    protected MeasurementFunctionController getFunctionController() {
        return (MeasurementFunctionController) getParentController();
    }

    protected abstract void measurementDone();

    @Override
    public void storeResultSuccessful() {
        hideLoading();
        Context context = getActivity();
        BulkyDialog dialog = new BulkyDialog(context, context.getString(R.string.MEASUREMENT_DIALOG_TEXT_SENT));
        dialog.addAffirmativeAction(context.getString(R.string.MEASUREMENT_DIALOG_BUTTON_OK), v -> {
            dialog.dismiss();
            if (mGlucoseEditText.length() != 0) {
                getFunctionController().measurementDone();
            }
        });
        dialog.show();
    }

    @Override
    public void storeResultFailed() {
        hideLoading();
        Context context = getActivity();
        BulkyDialog dialog = new BulkyDialog(context, context.getString(R.string.MEASUREMENT_DIALOG_TEXT_FAIL));
        dialog.addAffirmativeAction(context.getString(R.string.MEASUREMENT_DIALOG_BUTTON_OK), v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void userReauthNeeded() {
        ((MainActivity) getActivity()).showSplashForReauth();
    }

    /**
     * Subclass for usage with automatic (Bluetooth) measurement methods
     */
    public static class AutomaticMeasurement extends GlucometerMeasurementController {

        private enum AutomaticMeasurementState implements State {
            MEASURING {
                @Override
                public void onState(StateContext context) {
                    AutomaticMeasurement measurementController = (AutomaticMeasurement) context;
                    measurementController.showLoading();
                    measurementController.getPresenter().measureGlucoseLevel();
                }
            }, DONE {
                @Override
                public void onState(StateContext context) {
                    ((AutomaticMeasurement) context).showLoading();
                    ((AutomaticMeasurement) context).measurementDone();
                }
            }
        }

        @Override
        protected void onViewBound(@NonNull View view) {
            super.onViewBound(view);
            mNumberPad.setVisibility(View.GONE);
        }

        @Override
        public void processMeasurementResult(final BloodGlucoseLevel glucoseLevel) {
            Timber.d("Blood glucose level: %s", glucoseLevel.getFormattedString());

            Context context = getActivity();
            int finishedColor = ContextCompat.getColor(context, R.color.colorGreenLight);
            mGlucoseContainer.getBackground().setColorFilter(finishedColor, PorterDuff.Mode.SRC_ATOP);
            mGlucoseEditText.setTextColor(finishedColor);
            mHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_GLUCOMETER_HINT_AUTO_3)));
            mSubhintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_GLUCOMETER_HINT_AUTO_4)));
            mButtonHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BUTTON_HINT_SEND)));
            mStepButton.setText(context.getString(R.string.MEASUREMENT_BUTTON_SEND));

            mStepIndicator.setStep1State(StepIndicator.State.FINISHED);

            state(AutomaticMeasurementState.DONE);

            mGlucoseEditText.setText(glucoseLevel.getStringValue());
            hideLoading();
        }

        @Override
        public void processMeasurementFailed() {
            Timber.d("Measurement error!");
            hideLoading();
        }

        @Override
        protected void initLayout() {
            super.initLayout();

            Context context = getActivity();
            mHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_GLUCOMETER_HINT_AUTO_1)));
            mSubhintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_GLUCOMETER_HINT_AUTO_2)));
            mButtonHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BUTTON_HINT_NEXT)));
            mStepButton.setText(context.getString(R.string.MEASUREMENT_BUTTON_NEXT));

            state(AutomaticMeasurementState.MEASURING);
        }

        @Override
        protected void measurementDone() {
            if (mGlucoseEditText.length() != 0) {
                getPresenter().storeMeasurement(Float.parseFloat(mGlucoseEditText.getText().toString()), Result.Method.AUTOMATIC);
            }
        }
    }

    /**
     * Subclass for usage with manual measurement methods
     */
    public static class ManualMeasurement extends GlucometerMeasurementController {

        private enum ManualMeasurementState implements State {
            DONE {
                @Override
                public void onState(StateContext context) {
                    GlucometerMeasurementController.ManualMeasurement measurementController = (GlucometerMeasurementController.ManualMeasurement) context;
                    switch (MeasurementValidator.GlucometerValidator.validate(measurementController.mGlucoseEditText.getText().toString())) {
                        case EMPTY:
                            return;
                        case HIGH: {
                            BulkyAlertDialog alertDialog = new BulkyAlertDialog(measurementController.getActivity(), "Glucose value too high.");
                            alertDialog.show();
                        }
                        break;
                        case LOW: {
                            BulkyAlertDialog alertDialog = new BulkyAlertDialog(measurementController.getActivity(), "Glucose value too low.");
                            alertDialog.show();
                        }
                        break;
                        case OK:
                            measurementController.showLoading();
                            measurementController.measurementDone();
                            break;
                    }
                }
            }
        }

        // Override this, so no Bluetooth availability is checked
        @Override
        protected void onAttach(@NonNull View view) {}

        @Override
        public void processMeasurementResult(final BloodGlucoseLevel glucoseLevel) {}
        @Override
        public void processMeasurementFailed() {}

        @Override
        protected void initLayout() {
            super.initLayout();

            Context context = getActivity();
            mHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_GLUCOMETER_HINT_1)));
            mSubhintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_GLUCOMETER_HINT_2)));
            mButtonHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BUTTON_HINT_SEND)));
            mStepButton.setText(context.getString(R.string.MEASUREMENT_BUTTON_SEND));

            state(ManualMeasurementState.DONE);
        }

        @Override
        protected void measurementDone() {
            Context context = getActivity();

            int finishedColor = ContextCompat.getColor(context, R.color.colorGreenLight);
            mGlucoseContainer.getBackground().setColorFilter(finishedColor, PorterDuff.Mode.SRC_ATOP);
            mGlucoseEditText.setTextColor(finishedColor);

            mStepIndicator.setStep1State(StepIndicator.State.FINISHED);

            if (mGlucoseEditText.length() != 0) {
                getPresenter().storeMeasurement(Float.parseFloat(mGlucoseEditText.getText().toString()), Result.Method.MANUAL);
            }
        }
    }
}
