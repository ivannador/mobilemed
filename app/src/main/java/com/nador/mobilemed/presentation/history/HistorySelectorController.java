package com.nador.mobilemed.presentation.history;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nador.mobilemed.presentation.dashboard.DashboardController;
import com.nador.mobilemed.presentation.presenter.history.HistorySelectorControllerPresenter;
import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.FunctionWithLastUsed;
import com.nador.mobilemed.data.manager.FunctionAvailabilityManager;
import com.nador.mobilemed.presentation.base.BaseController;

import java.util.ArrayList;
import java.util.WeakHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by nador on 28/07/16.
 */
public class HistorySelectorController extends BaseController<IHistorySelectorController, HistorySelectorControllerPresenter> implements IHistorySelectorController {

    public static final String TAG = "HistorySelectorController";

    @Bind(R.id.hintTextView)
    TextView mHintTextView;

    @Bind(R.id.glucometerButton)
    Button mGlucometerButton;
    @Bind(R.id.bloodpressureButton)
    Button mBloodpressureButton;
    @Bind(R.id.oximeterButton)
    Button mOximeterButton;
    @Bind(R.id.assessmentButton)
    Button mAssessmentButton;
    @Bind(R.id.apneaButton)
    Button mApneaButton;
    @Bind(R.id.weightButton)
    Button mWeightButton;
    @Bind(R.id.medicationButton)
    Button mMedicationButton;
    @Bind(R.id.temperatureButton)
    Button mTemperatureButton;
    @Bind(R.id.ecgButton)
    Button mEcgButton;

    @OnClick(R.id.glucometerButton)
    protected void onGlucometerButtonTapped() {
        navigateToHistoryView(new HistoryController.GlucometerHistoryController());
    }
    @OnClick(R.id.bloodpressureButton)
    protected void onBloodpressureButtonTapped() {
        navigateToHistoryView(new HistoryController.BloodPressureHistoryController());
    }
    @OnClick(R.id.oximeterButton)
    protected void onOximeterButtonTapped() {}
    @OnClick(R.id.assessmentButton)
    protected void onAssessmentButtonTapped() {}
    @OnClick(R.id.apneaButton)
    protected void onApneaButtonTapped() {}
    @OnClick(R.id.weightButton)
    protected void onWeightButtonTapped() {
        navigateToHistoryView(new HistoryController.WeightHistoryController());
    }
    @OnClick(R.id.medicationButton)
    protected void onMedicationButtonTapped() {}
    @OnClick(R.id.temperatureButton)
    protected void onTemperatureButtonTapped() {
        navigateToHistoryView(new HistoryController.TemperatureHistoryController());
    }
    @OnClick(R.id.ecgButton)
    protected void onEcgButtonTapped() {
        navigateToHistoryView(new HistoryController.ECGHistoryController());
    }

    private WeakHashMap<String, Button> mFunctionIdMap;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_history_selector, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        initLayout();
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

        for (Button button : mFunctionIdMap.values()) {
            button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.teldoc_button_grey));
        }

        ArrayList<FunctionWithLastUsed> functionList = getPresenter().getFunctionList();
        for (FunctionWithLastUsed function : functionList) {
            if (function.isEnabled()) {
                mFunctionIdMap.get(function.getId()).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.teldoc_button_blue));
            }
        }
    }

    @NonNull
    @Override
    public HistorySelectorControllerPresenter createPresenter() {
        return new HistorySelectorControllerPresenter(getActivity());
    }

    private void initLayout() {
        mHintTextView.setText(Html.fromHtml(getActivity().getString(R.string.HISTORY_TEXT_SELECTOR_TITLE)));

        mFunctionIdMap = new WeakHashMap<>();
        mFunctionIdMap.put(FunctionAvailabilityManager.GLUCOMETER_FUNCTION_ID, mGlucometerButton);
        mFunctionIdMap.put(FunctionAvailabilityManager.BLOODPRESSURE_FUNCTION_ID, mBloodpressureButton);
        mFunctionIdMap.put(FunctionAvailabilityManager.OXIMETER_FUNCTION_ID, mOximeterButton);
        mFunctionIdMap.put(FunctionAvailabilityManager.ASSESSMENT_FUNCTION_ID, mAssessmentButton);
        mFunctionIdMap.put(FunctionAvailabilityManager.WEIGHT_FUNCTION_ID, mWeightButton);
        mFunctionIdMap.put(FunctionAvailabilityManager.TEMPERATURE_FUNCTION_ID, mTemperatureButton);
        mFunctionIdMap.put(FunctionAvailabilityManager.ECG_FUNCTION_ID, mEcgButton);
        mFunctionIdMap.put(FunctionAvailabilityManager.APNEA_FUNCTION_ID, mApneaButton);
        mFunctionIdMap.put(FunctionAvailabilityManager.MEDICATION_FUNCTION_ID, mMedicationButton);

    }

    private void navigateToHistoryView(HistoryController controller) {
        ((DashboardController) getParentController()).navigateToController(controller);
    }
}
