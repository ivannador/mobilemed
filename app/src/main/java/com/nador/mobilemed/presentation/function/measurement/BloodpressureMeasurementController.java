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

import com.nador.mobilemed.data.utils.fsm.State;
import com.nador.mobilemed.presentation.widget.BulkyAlertDialog;
import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.measurement.BloodPressure;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.data.utils.MeasurementValidator;
import com.nador.mobilemed.data.utils.fsm.StateContext;
import com.nador.mobilemed.presentation.MainActivity;
import com.nador.mobilemed.presentation.base.BluetoothUserController;
import com.nador.mobilemed.presentation.function.base.MeasurementFunctionController;
import com.nador.mobilemed.presentation.presenter.function.measurement.BloodpressureMeasurementControllerPresenter;
import com.nador.mobilemed.presentation.widget.BulkyDialog;
import com.nador.mobilemed.presentation.widget.NumberPad;
import com.nador.mobilemed.presentation.widget.StepIndicator;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by nador on 27/06/16.
 */
public abstract class BloodpressureMeasurementController extends BluetoothUserController<IBloodpressureMeasurementController, BloodpressureMeasurementControllerPresenter> implements IBloodpressureMeasurementController, StateContext {

    @Bind(R.id.numberPad)
    NumberPad mNumberPad;

    @Bind(R.id.systolicContainer)
    View mSystolicContainer;
    @Bind(R.id.systolicEditText)
    EditText mSystolicEditText;
    @Bind(R.id.diastolicContainer)
    View mDiastolicContainer;
    @Bind(R.id.diastolicEditText)
    EditText mDiastolicEditText;
    @Bind(R.id.pulseContainer)
    View mPulseContainer;
    @Bind(R.id.pulseEditText)
    EditText mPulseEditText;

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
        return inflater.inflate(R.layout.controller_bloodpressure, container, false);
    }

    @NonNull
    @Override
    public BloodpressureMeasurementControllerPresenter createPresenter() {
        return new BloodpressureMeasurementControllerPresenter(getActivity());
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
            if (mSystolicEditText.length() != 0  && mDiastolicEditText.length() != 0 && mPulseEditText.length() != 0) {
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
    public static class AutomaticMeasurement extends BloodpressureMeasurementController {

        private enum AutomaticMeasurementState implements State {
            MEASURING {
                @Override
                public void onState(StateContext context) {
                    AutomaticMeasurement measurementController = (AutomaticMeasurement) context;
                    measurementController.showLoading();
                    measurementController.getPresenter().measureBloodpressure();
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
            initLayout();
        }

        @Override
        public void processMeasurementResult(final BloodPressure bloodPressure) {
            Timber.d("Blood pressure: %s", bloodPressure.toString());

            Context context = getActivity();
            int finishedColor = ContextCompat.getColor(context, R.color.colorGreenLight);
            mSystolicContainer.getBackground().setColorFilter(finishedColor, PorterDuff.Mode.SRC_ATOP);
            mSystolicEditText.setTextColor(finishedColor);
            mDiastolicContainer.getBackground().setColorFilter(finishedColor, PorterDuff.Mode.SRC_ATOP);
            mDiastolicEditText.setTextColor(finishedColor);
            mPulseContainer.getBackground().setColorFilter(finishedColor, PorterDuff.Mode.SRC_ATOP);
            mPulseEditText.setTextColor(finishedColor);
            mHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_HINT_AUTO_3)));
            mSubhintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_HINT_AUTO_4)));
            mButtonHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BUTTON_HINT_SEND)));
            mStepButton.setText(context.getString(R.string.MEASUREMENT_BUTTON_SEND));

            mStepIndicator.setStep1State(StepIndicator.State.FINISHED);
            mStepIndicator.setStep2State(StepIndicator.State.FINISHED);
            mStepIndicator.setStep3State(StepIndicator.State.FINISHED);

            state(AutomaticMeasurementState.DONE);

            mSystolicEditText.setText(String.valueOf(bloodPressure.getSystolicValue()));
            mDiastolicEditText.setText(String.valueOf(bloodPressure.getDiastolicValue()));
            mPulseEditText.setText(String.valueOf(bloodPressure.getPulseValue()));
            hideLoading();
        }

        @Override
        public void processMeasurementFailed() {
            Timber.d("Measurement error!");
            hideLoading();
        }

        @Override
        protected void measurementDone() {
            if (mSystolicEditText.length() != 0 && mDiastolicEditText.length() != 0 && mPulseEditText.length() != 0) {
                getPresenter().storeMeasurement(
                        Long.parseLong(mSystolicEditText.getText().toString()),
                        Long.parseLong(mDiastolicEditText.getText().toString()),
                        Long.parseLong(mPulseEditText.getText().toString()),
                        Result.Method.AUTOMATIC);
            }
        }

        private void initLayout() {
            Context context = getActivity();
            int inactiveColor = ContextCompat.getColor(context, R.color.colorGreyLight);
            int currentColor = ContextCompat.getColor(context, R.color.colorBlueLight);
            mSystolicContainer.getBackground().setColorFilter(currentColor, PorterDuff.Mode.SRC_ATOP);
            mSystolicEditText.setTextColor(currentColor);
            mDiastolicContainer.getBackground().setColorFilter(inactiveColor, PorterDuff.Mode.SRC_ATOP);
            mDiastolicEditText.setTextColor(inactiveColor);
            mPulseContainer.getBackground().setColorFilter(inactiveColor, PorterDuff.Mode.SRC_ATOP);
            mPulseEditText.setTextColor(inactiveColor);
            mHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_HINT_AUTO_1)));
            mSubhintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_HINT_AUTO_2)));
            mButtonHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BUTTON_HINT_NEXT)));

            mStepIndicator.setStep1State(StepIndicator.State.ACTIVE);
            mStepIndicator.setStep2State(StepIndicator.State.INACTIVE);
            mStepIndicator.setStep3State(StepIndicator.State.INACTIVE);

            mNumberPad.setVisibility(View.GONE);

            state(AutomaticMeasurementState.MEASURING);
        }
    }

    /**
     * Subclass for usage with manual measurement methods
     */
    public static class ManualMeasurement extends BloodpressureMeasurementController {

        private enum ManualMeasurementState implements State {
            SYSTOLIC {
                @Override
                public void onState(StateContext context) {
                    ManualMeasurement measurementController = (ManualMeasurement) context;
                    switch (MeasurementValidator.BloodPressureValidator.validateSystolic(measurementController.mSystolicEditText.getText().toString())) {
                        case EMPTY:
                            return;
                        case HIGH: {
                            BulkyAlertDialog alertDialog = new BulkyAlertDialog(measurementController.getActivity(), "Systolic value too high.");
                            alertDialog.show();
                        }
                        break;
                        case LOW: {
                            BulkyAlertDialog alertDialog = new BulkyAlertDialog(measurementController.getActivity(), "Systolic value too low.");
                            alertDialog.show();
                        }
                        break;
                        case OK:
                            measurementController.setLayoutForState(DIASTOLIC);
                            context.state(DIASTOLIC);
                            break;
                    }
                }
            }, DIASTOLIC {
                @Override
                public void onState(StateContext context) {
                    ManualMeasurement measurementController = (ManualMeasurement) context;
                    switch (MeasurementValidator.BloodPressureValidator.validateDiastolic(measurementController.mDiastolicEditText.getText().toString())) {
                        case EMPTY:
                            return;
                        case HIGH: {
                            BulkyAlertDialog alertDialog = new BulkyAlertDialog(measurementController.getActivity(), "Diastolic value too high.");
                            alertDialog.show();
                        }
                        break;
                        case LOW: {
                            BulkyAlertDialog alertDialog = new BulkyAlertDialog(measurementController.getActivity(), "Diastolic value too low.");
                            alertDialog.show();
                        }
                        break;
                        case OK:
                            measurementController.setLayoutForState(PULSE);
                            context.state(PULSE);
                            break;
                    }
                }
            }, PULSE {
                @Override
                public void onState(StateContext context) {
                    ManualMeasurement measurementController = (ManualMeasurement) context;
                    switch (MeasurementValidator.BloodPressureValidator.validatePulse(measurementController.mPulseEditText.getText().toString())) {
                        case EMPTY:
                            return;
                        case HIGH: {
                            BulkyAlertDialog alertDialog = new BulkyAlertDialog(measurementController.getActivity(), "Pulse value too high.");
                            alertDialog.show();
                        }
                        break;
                        case LOW: {
                            BulkyAlertDialog alertDialog = new BulkyAlertDialog(measurementController.getActivity(), "Pulse value too low.");
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

        @Override
        protected void onViewBound(@NonNull View view) {
            super.onViewBound(view);
            state(ManualMeasurementState.SYSTOLIC);
            setLayoutForState(ManualMeasurementState.SYSTOLIC);
        }

        // Override this, so no Bluetooth availability is checked
        @Override
        protected void onAttach(@NonNull View view) {}

        @Override
        public void processMeasurementResult(final BloodPressure bloodPressure) {}
        @Override
        public void processMeasurementFailed() {}

        protected final void setLayoutForState(final ManualMeasurementState newState) {
            Context context = getActivity();
            int inactiveColor = ContextCompat.getColor(context, R.color.colorGreyLight);
            int currentColor = ContextCompat.getColor(context, R.color.colorBlueLight);
            int finishedColor = ContextCompat.getColor(context, R.color.colorGreenLight);
            switch (newState) {
                case SYSTOLIC:
                    mSystolicContainer.getBackground().setColorFilter(currentColor, PorterDuff.Mode.SRC_ATOP);
                    mSystolicEditText.setTextColor(currentColor);
                    mDiastolicContainer.getBackground().setColorFilter(inactiveColor, PorterDuff.Mode.SRC_ATOP);
                    mDiastolicEditText.setTextColor(inactiveColor);
                    mPulseContainer.getBackground().setColorFilter(inactiveColor, PorterDuff.Mode.SRC_ATOP);
                    mPulseEditText.setTextColor(inactiveColor);
                    mHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_SYSTOLIC_HINT_1)));
                    mSubhintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_SYSTOLIC_HINT_2)));
                    mButtonHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BUTTON_HINT_NEXT)));
                    mNumberPad.attachTargetEditText(mSystolicEditText);
                    mStepIndicator.setStep1State(StepIndicator.State.ACTIVE);
                    mStepIndicator.setStep2State(StepIndicator.State.INACTIVE);
                    mStepIndicator.setStep3State(StepIndicator.State.INACTIVE);
                    break;
                case DIASTOLIC:
                    mSystolicContainer.getBackground().setColorFilter(finishedColor, PorterDuff.Mode.SRC_ATOP);
                    mSystolicEditText.setTextColor(finishedColor);
                    mDiastolicContainer.getBackground().setColorFilter(currentColor, PorterDuff.Mode.SRC_ATOP);
                    mDiastolicEditText.setTextColor(currentColor);
                    mPulseContainer.getBackground().setColorFilter(inactiveColor, PorterDuff.Mode.SRC_ATOP);
                    mPulseEditText.setTextColor(inactiveColor);
                    mHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_DIASTOLIC_HINT_1)));
                    mSubhintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_DIASTOLIC_HINT_2)));
                    mButtonHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BUTTON_HINT_NEXT)));
                    mNumberPad.attachTargetEditText(mDiastolicEditText);
                    mStepIndicator.setStep1State(StepIndicator.State.FINISHED);
                    mStepIndicator.setStep2State(StepIndicator.State.ACTIVE);
                    mStepIndicator.setStep3State(StepIndicator.State.INACTIVE);
                    break;
                case PULSE:
                    mDiastolicContainer.getBackground().setColorFilter(finishedColor, PorterDuff.Mode.SRC_ATOP);
                    mDiastolicEditText.setTextColor(finishedColor);
                    mPulseContainer.getBackground().setColorFilter(currentColor, PorterDuff.Mode.SRC_ATOP);
                    mPulseEditText.setTextColor(currentColor);
                    mHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_PULSE_HINT_1)));
                    mSubhintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BP_PULSE_HINT_2)));
                    mButtonHintTextView.setText(Html.fromHtml(context.getString(R.string.MEASUREMENT_TEXT_BUTTON_HINT_SEND)));
                    mStepButton.setText(context.getString(R.string.MEASUREMENT_BUTTON_SEND));
                    mNumberPad.attachTargetEditText(mPulseEditText);
                    mStepIndicator.setStep1State(StepIndicator.State.FINISHED);
                    mStepIndicator.setStep2State(StepIndicator.State.FINISHED);
                    mStepIndicator.setStep3State(StepIndicator.State.ACTIVE);
                    break;
            }
        }

        @Override
        protected void measurementDone() {
            Context context = getActivity();

            int finishedColor = ContextCompat.getColor(context, R.color.colorGreenLight);
            mPulseContainer.getBackground().setColorFilter(finishedColor, PorterDuff.Mode.SRC_ATOP);
            mPulseEditText.setTextColor(finishedColor);
            mStepIndicator.setStep1State(StepIndicator.State.FINISHED);
            mStepIndicator.setStep2State(StepIndicator.State.FINISHED);
            mStepIndicator.setStep3State(StepIndicator.State.FINISHED);

            if (mSystolicEditText.length() != 0 && mDiastolicEditText.length() != 0 && mPulseEditText.length() != 0) {
                Long systolic = 0L;
                Long diastolic = 0L;
                Long pulse = 0L;
                try {
                    systolic = Long.parseLong(mSystolicEditText.getText().toString());
                    diastolic = Long.parseLong(mDiastolicEditText.getText().toString());
                    pulse = Long.parseLong(mPulseEditText.getText().toString());
                } catch (NumberFormatException e) {
                    storeResultFailed();
                    return;
                }
                getPresenter().storeMeasurement(systolic, diastolic, pulse, Result.Method.MANUAL);
            }
        }
    }
}
