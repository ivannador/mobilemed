package com.nador.mobilemed.presentation.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.nador.mobilemed.data.entity.measurement.BloodGlucoseLevel;
import com.nador.mobilemed.data.entity.measurement.ECGGraph;
import com.nador.mobilemed.data.entity.measurement.ECGMeasurements;
import com.nador.mobilemed.data.entity.measurement.Temperature;
import com.nador.mobilemed.data.entity.measurement.Weight;
import com.nador.mobilemed.presentation.function.base.FunctionController;
import com.nador.mobilemed.presentation.history.adapter.HistoryValueAdapter;
import com.nador.mobilemed.presentation.utils.DateUtil;
import com.nador.mobilemed.presentation.widget.BulkyAlertDialog;
import com.nador.mobilemed.presentation.widget.DividerItemDecoration;
import com.nador.mobilemed.presentation.widget.TrendlineBarChart;
import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.measurement.BloodPressure;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.presentation.MainActivity;
import com.nador.mobilemed.presentation.presenter.history.HistoryControllerPresenter;
import com.nador.mobilemed.presentation.widget.ecg.ECGLineChart;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by nador on 29/07/16.
 */
public abstract class HistoryController<V extends IHistoryController, P extends HistoryControllerPresenter<V>> extends FunctionController<V, P>
        implements IHistoryController {

    @Bind(R.id.dayButton)
    Button mDayButton;
    @Bind(R.id.weekButton)
    Button mWeekButton;
    @Bind(R.id.monthButton)
    Button mMonthButton;
    @Bind(R.id.dateSpinner)
    Spinner mDateSpinner;

    @Bind(R.id.unitContainer)
    ViewGroup mUnitContainer;
    @Bind(R.id.unitTextView1)
    TextView mUnitTextView1;
    @Bind(R.id.unitTextView2)
    TextView mUnitTextView2;
    @Bind(R.id.unitTextView3)
    TextView mUnitTextView3;

    @Bind(R.id.diagramContainer)
    LinearLayout mDiagramContainer;
    @Bind(R.id.valueListContainer)
    ViewGroup mValueListContainer;
    @Bind(R.id.valueListView)
    RecyclerView mValueListView;

    @Bind(R.id.ecgButtonContainer)
    ViewGroup mEcgButtonContainer;

    @OnClick(R.id.dayButton)
    protected void onDayButtonTapped() {}
    @OnClick(R.id.weekButton)
    protected void onWeekButtonTapped() {}
    @OnClick(R.id.monthButton)
    protected void onMonthButtonTapped() {}

    protected HistoryValueAdapter mAdapter;

    private ArrayAdapter<String> mSpinnerAdapter;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_history, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mEcgButtonContainer.setVisibility(View.GONE);
        initLayout();

        mSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_history_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        List<String> dateList = new ArrayList<>();
        dateList.add("Last 7 days");
        dateList.add("Last 14 days");
        dateList.add("Last 30 days");
        mSpinnerAdapter.addAll(dateList);
        mDateSpinner.setAdapter(mSpinnerAdapter);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        showLoading();
        getPresenter().getResultList(false);
    }

    @Override
    public void setResultList(List<Result> resultList) {
        hideLoading();

        if (resultList.isEmpty()) {
            Context context = getActivity();
            BulkyAlertDialog dialog = new BulkyAlertDialog(context, context.getString(R.string.HISTORY_ALERT_NO_RESULTS));
            dialog.addAffirmativeAction(context.getString(R.string.MEASUREMENT_DIALOG_BUTTON_OK), v -> {
               onBackButtonTapped();
                dialog.dismiss();
            });
            dialog.show();
        } else {
            setChartData(resultList);
            mAdapter.setDataList(resultList);
        }
    }

    @Override
    public void userReauthNeeded() {
        ((MainActivity) getActivity()).showSplashForReauth();
    }

    protected abstract void initLayout();
    protected abstract void setChartData(final List<Result> resultList);

    public static class BloodPressureHistoryController extends HistoryController<IHistoryController, HistoryControllerPresenter.BloodpressureHistoryControllerPresenter> {

        private BarChart mChart;

        @NonNull
        @Override
        public HistoryControllerPresenter.BloodpressureHistoryControllerPresenter createPresenter() {
            return new HistoryControllerPresenter.BloodpressureHistoryControllerPresenter(getActivity());
        }

        @Override
        protected void setMainTitle() {
            mFunctionTitleTextView.setText(getActivity().getString(R.string.HISTORY_TITLE_BLOOD_PRESSURE));
        }

        @Override
        protected void initLayout() {
            Context context = getActivity();
            mUnitTextView1.setText(context.getString(R.string.MEASUREMENT_TEXT_BP_SYSTOLIC));
            mUnitTextView2.setText(context.getString(R.string.MEASUREMENT_TEXT_BP_DIASTOLIC));
            mUnitTextView3.setText(context.getString(R.string.MEASUREMENT_TEXT_BP_PULSE));

            mValueListView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.teldoc_divider_simple)));
            mValueListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mAdapter = new HistoryValueAdapter.BloodpressureHistoryAdapter(getActivity());
            mValueListView.setAdapter(mAdapter);

            mChart = new BarChart(getActivity());
            XAxis xAxis = mChart.getXAxis();
            xAxis.setTextSize(8f);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter((value, axis) -> {
                if ((int) value >= getPresenter().getResultList().size()) {
                    return "";
                }
                return DateUtil.getDateString(getPresenter().getResultList().get((int) value).getDate(), "MM-dd-yyyy");
            });
            YAxis yAxisLeft = mChart.getAxisLeft();
            yAxisLeft.setDrawZeroLine(true);
            yAxisLeft.setDrawGridLines(true);
            yAxisLeft.setDrawAxisLine(false);
            mChart.getAxisRight().setEnabled(false);
            mChart.setDescription(null);
            mChart.getLegend().setEnabled(false);

            mDiagramContainer.removeAllViews();
            mChart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
            mDiagramContainer.addView(mChart);
        }

        @Override
        protected void setChartData(List<Result> resultList) {
            List<BarEntry> systolicGroup = new ArrayList<>();
            List<BarEntry> diastolicGroup = new ArrayList<>();

            for (int i = 0; i < resultList.size(); i++) {
                final BloodPressure bloodPressure = (BloodPressure) resultList.get(i);
                systolicGroup.add(new BarEntry(i, bloodPressure.getSystolicValue()));
                diastolicGroup.add(new BarEntry(i, bloodPressure.getDiastolicValue()));
            }

            Context context = getActivity();
            BarDataSet systolicSet = new BarDataSet(systolicGroup, context.getString(R.string.MEASUREMENT_TEXT_BP_SYSTOLIC));
            BarDataSet diastolicSet = new BarDataSet(diastolicGroup, context.getString(R.string.MEASUREMENT_TEXT_BP_DIASTOLIC));
            systolicSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorRedMedium));
            diastolicSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorGreenLight));

            float groupSpace = 0.6f;
            float barSpace = 0.04f;
            float barWidth = 0.16f;
            // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

            BarData barData = new BarData(systolicSet, diastolicSet);
            barData.setBarWidth(barWidth);
//            barData.setDrawValues(false);
            mChart.getXAxis().setAxisMinimum(0.0f);
            mChart.getXAxis().setAxisMaximum(resultList.size() * 1.0f);
            mChart.getXAxis().setGranularity(1.f);
            mChart.setData(barData);
            mChart.groupBars(0.0f, groupSpace, barSpace);
            mChart.invalidate();
        }
    }

    public static class WeightHistoryController extends HistoryController<IHistoryController, HistoryControllerPresenter.WeightHistoryControllerPresenter> {

        private TrendlineBarChart mChart;

        @NonNull
        @Override
        public HistoryControllerPresenter.WeightHistoryControllerPresenter createPresenter() {
            return new HistoryControllerPresenter.WeightHistoryControllerPresenter(getActivity());
        }

        @Override
        protected void setMainTitle() {
            mFunctionTitleTextView.setText(getActivity().getString(R.string.HISTORY_TITLE_WEIGHT));
        }

        @Override
        protected void initLayout() {
            Context context = getActivity();
            mUnitTextView1.setText(context.getString(R.string.MEASUREMENT_TEXT_WEIGHT));

            mUnitTextView2.setVisibility(View.GONE);
            mUnitTextView3.setVisibility(View.GONE);

            mValueListView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.teldoc_divider_simple)));
            mValueListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mAdapter = new HistoryValueAdapter.WeightHistoryAdapter(getActivity());
            mValueListView.setAdapter(mAdapter);

            mChart = new TrendlineBarChart(getActivity());

            mChart.setDrawOrder(new CombinedChart.DrawOrder[] { CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE });

            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(7);
            xAxis.setTextSize(8f);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter((value, axis) -> DateUtil.getDateString(getPresenter().getResultList().get((int) value).getDate(), "MM-dd-yyyy"));
            YAxis yAxis = mChart.getAxisLeft();
            yAxis.setDrawZeroLine(true);
            yAxis.setDrawGridLines(true);
            yAxis.setDrawAxisLine(false);
            mChart.getAxisRight().setEnabled(false);
            mChart.setDescription(null);
            mChart.setFitBars(true);
            mChart.getLegend().setEnabled(false);

            mDiagramContainer.removeAllViews();
            mChart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
            mDiagramContainer.addView(mChart);
        }

        @Override
        protected void setChartData(List<Result> resultList) {
            List<Entry> weightEntries = new ArrayList<>();
            List<BarEntry> barEntries = new ArrayList<>();

            for (int i = 0; i < resultList.size(); i++) {
                final Weight weight = (Weight) resultList.get(i);
                weightEntries.add(new Entry(i, weight.getValue()));
                barEntries.add(new BarEntry(i, weight.getValue()));
            }

            CombinedData data = new CombinedData();

            Context context = getActivity();
            BarDataSet tempSet = new BarDataSet(barEntries, context.getString(R.string.MEASUREMENT_TEXT_WEIGHT));
            tempSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorBlueMedium));
            LineDataSet lineSet = new LineDataSet(weightEntries, context.getString(R.string.MEASUREMENT_TEXT_WEIGHT));
            lineSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            float barWidth = 0.4f;
            BarData barData = new BarData(tempSet);
            barData.setBarWidth(barWidth);
            barData.setDrawValues(false);
            LineData lineData = new LineData(lineSet);
            lineData.addDataSet(lineSet);

            data.setData(barData);
            data.setData(lineData);

            mChart.setData(data);
            mChart.invalidate();
        }
    }

    public static class TemperatureHistoryController extends HistoryController<IHistoryController, HistoryControllerPresenter.TemperatureHistoryControllerPresenter> {

        private TrendlineBarChart mChart;

        @NonNull
        @Override
        public HistoryControllerPresenter.TemperatureHistoryControllerPresenter createPresenter() {
            return new HistoryControllerPresenter.TemperatureHistoryControllerPresenter(getActivity());
        }

        @Override
        protected void setMainTitle() {
            mFunctionTitleTextView.setText(getActivity().getString(R.string.HISTORY_TITLE_TEMPERATURE));
        }

        @Override
        protected void initLayout() {
            Context context = getActivity();
            mUnitTextView1.setText(context.getString(R.string.MEASUREMENT_TEXT_TEMPERATURE));

            mUnitTextView2.setVisibility(View.GONE);
            mUnitTextView3.setVisibility(View.GONE);

            mValueListView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.teldoc_divider_simple)));
            mValueListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mAdapter = new HistoryValueAdapter.TemperatureHistoryAdapter(getActivity());
            mValueListView.setAdapter(mAdapter);

            mChart = new TrendlineBarChart(getActivity());

            mChart.setDrawOrder(new CombinedChart.DrawOrder[] { CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE });

            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(7);
            xAxis.setTextSize(8f);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter((value, axis) -> DateUtil.getDateString(getPresenter().getResultList().get((int) value).getDate(), "MM-dd-yyyy"));
            YAxis yAxis = mChart.getAxisLeft();
            yAxis.setDrawZeroLine(true);
            yAxis.setDrawGridLines(true);
            yAxis.setDrawAxisLine(false);
            mChart.getAxisRight().setEnabled(false);
            mChart.setDescription(null);
            mChart.setFitBars(true);
            mChart.getLegend().setEnabled(false);

            mDiagramContainer.removeAllViews();
            mChart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
            mDiagramContainer.addView(mChart);
        }

        @Override
        protected void setChartData(List<Result> resultList) {
            List<Entry> temperatureEntries = new ArrayList<>();
            List<BarEntry> barEntries = new ArrayList<>();

            for (int i = 0; i < resultList.size(); i++) {
                final Temperature temperature = (Temperature) resultList.get(i);
                temperatureEntries.add(new Entry(i, temperature.getValue()));
                barEntries.add(new BarEntry(i, temperature.getValue()));
            }

            CombinedData data = new CombinedData();

            Context context = getActivity();
            BarDataSet tempSet = new BarDataSet(barEntries, context.getString(R.string.MEASUREMENT_TEXT_TEMPERATURE));
            tempSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorBlueMedium));
            LineDataSet lineSet = new LineDataSet(temperatureEntries, context.getString(R.string.MEASUREMENT_TEXT_TEMPERATURE));
            lineSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            float barWidth = 0.4f;
            BarData barData = new BarData(tempSet);
            barData.setBarWidth(barWidth);
            barData.setDrawValues(false);
            LineData lineData = new LineData(lineSet);
            lineData.addDataSet(lineSet);

            data.setData(barData);
            data.setData(lineData);

            mChart.setData(data);
            mChart.invalidate();
        }
    }

    public static class GlucometerHistoryController extends HistoryController<IHistoryController, HistoryControllerPresenter.GlucometerHistoryControllerPresenter> {

        private TrendlineBarChart mChart;

        @NonNull
        @Override
        public HistoryControllerPresenter.GlucometerHistoryControllerPresenter createPresenter() {
            return new HistoryControllerPresenter.GlucometerHistoryControllerPresenter(getActivity());
        }

        @Override
        protected void setMainTitle() {
            mFunctionTitleTextView.setText(getActivity().getString(R.string.HISTORY_TITLE_GLUCOMETER));
        }

        @Override
        protected void initLayout() {
            Context context = getActivity();
            mUnitTextView1.setText(context.getString(R.string.MEASUREMENT_TEXT_GLUCOMETER));

            mUnitTextView2.setVisibility(View.GONE);
            mUnitTextView3.setVisibility(View.GONE);

            mValueListView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.teldoc_divider_simple)));
            mValueListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mAdapter = new HistoryValueAdapter.GlucometerHistoryAdapter(getActivity());
            mValueListView.setAdapter(mAdapter);

            mChart = new TrendlineBarChart(getActivity());

            mChart.setDrawOrder(new CombinedChart.DrawOrder[] { CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE });

            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(7);
            xAxis.setTextSize(8f);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter((value, axis) -> DateUtil.getDateString(getPresenter().getResultList().get((int) value).getDate(), "MM-dd-yyyy"));
            YAxis yAxis = mChart.getAxisLeft();
            yAxis.setDrawZeroLine(true);
            yAxis.setDrawGridLines(true);
            yAxis.setDrawAxisLine(false);
            mChart.getAxisRight().setEnabled(false);
            mChart.setDescription(null);
            mChart.setFitBars(true);
            mChart.getLegend().setEnabled(false);

            mDiagramContainer.removeAllViews();
            mChart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
            mDiagramContainer.addView(mChart);
        }

        @Override
        protected void setChartData(List<Result> resultList) {
            List<Entry> levelEntries = new ArrayList<>();
            List<BarEntry> barEntries = new ArrayList<>();

            for (int i = 0; i < resultList.size(); i++) {
                final BloodGlucoseLevel glucoseLevel = (BloodGlucoseLevel) resultList.get(i);
                levelEntries.add(new Entry(i, glucoseLevel.getGlucoseLevel()));
                barEntries.add(new BarEntry(i, glucoseLevel.getGlucoseLevel()));
            }

            CombinedData data = new CombinedData();

            Context context = getActivity();
            BarDataSet tempSet = new BarDataSet(barEntries, context.getString(R.string.MEASUREMENT_TEXT_GLUCOMETER));
            tempSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorBlueMedium));
            LineDataSet lineSet = new LineDataSet(levelEntries, context.getString(R.string.MEASUREMENT_TEXT_GLUCOMETER));
            lineSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            float barWidth = 0.4f;
            BarData barData = new BarData(tempSet);
            barData.setBarWidth(barWidth);
            barData.setDrawValues(false);
            LineData lineData = new LineData(lineSet);
            lineData.addDataSet(lineSet);
            lineData.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format("%.2f", value));

            data.setData(barData);
            data.setData(lineData);

            mChart.setData(data);
            mChart.invalidate();
        }
    }

    public static class ECGHistoryController extends HistoryController<IECGHistoryController, HistoryControllerPresenter.ECGHistoryControllerPresenter>
            implements HistoryValueAdapter.ResultSelectedListener, IECGHistoryController {

        @Bind(R.id.measurementButtonContainer)
        ViewGroup mMeasurementButtonContainer;

        @Bind(R.id.pWaveButton)
        ToggleButton mPWaveButton;
        @Bind(R.id.prSegmentButton)
        ToggleButton mPRSegmentButton;
        @Bind(R.id.qrsComplexButton)
        ToggleButton mQRSComplexButton;
        @Bind(R.id.stSegmentButton)
        ToggleButton mSTSegmentButton;
        @Bind(R.id.tWaveButton)
        ToggleButton mTWaveButton;
        @Bind(R.id.uWaveButton)
        ToggleButton mUWaveButton;

        @OnClick(R.id.pWaveButton)
        protected void onPWaveButtonTapped() {
            measurementButtonTapped(mPWaveButton, ECGMeasurements.SegmentType.PWave);
        }
        @OnClick(R.id.prSegmentButton)
        protected void onPRSegmentButtonTapped() {
            measurementButtonTapped(mPRSegmentButton, ECGMeasurements.SegmentType.PRSegment);
        }
        @OnClick(R.id.qrsComplexButton)
        protected void onQRSComplexButtonTapped() {
            measurementButtonTapped(mQRSComplexButton, ECGMeasurements.SegmentType.QRSComplex);
        }
        @OnClick(R.id.stSegmentButton)
        protected void onSTSegmentButtonTapped() {
            measurementButtonTapped(mSTSegmentButton, ECGMeasurements.SegmentType.STSegment);
        }
        @OnClick(R.id.tWaveButton)
        protected void onTWaveButtonTapped() {
            measurementButtonTapped(mTWaveButton, ECGMeasurements.SegmentType.TWave);
        }
        @OnClick(R.id.uWaveButton)
        protected void onUWaveButtonTapped() {
            measurementButtonTapped(mUWaveButton, ECGMeasurements.SegmentType.UWave);
        }

        @OnClick(R.id.registerButton)
        protected void onRegisterButtonTapped() {
            showLoading();
            getPresenter().recordECGMeasurement(mCurrentGraphName);
        }

        private String mCurrentGraphName;

        @NonNull
        @Override
        public HistoryControllerPresenter.ECGHistoryControllerPresenter createPresenter() {
            return new HistoryControllerPresenter.ECGHistoryControllerPresenter(getActivity());
        }

        @Override
        protected void setMainTitle() {
            mFunctionTitleTextView.setText(getActivity().getString(R.string.HISTORY_TITLE_ECG));
        }

        @Override
        public void setResultList(List<Result> resultList) {
            hideLoading();
            mAdapter.setDataList(resultList);
            if (resultList.size() > 0) {
                onResultSelected(resultList.get(0));
            } else {
                Context context = getActivity();
                BulkyAlertDialog dialog = new BulkyAlertDialog(context, context.getString(R.string.HISTORY_ALERT_NO_RESULTS));
                dialog.addAffirmativeAction(context.getString(R.string.MEASUREMENT_DIALOG_BUTTON_OK), v -> {
                    onBackButtonTapped();
                    dialog.dismiss();
                });
                dialog.show();
            }
        }

        @Override
        protected void initLayout() {
            mEcgButtonContainer.setVisibility(View.VISIBLE);

//            mPButton.setTextColor(ECGHighlight.Type.P.getColor(getActivity()));
//            mQButton.setTextColor(ECGHighlight.Type.Q.getColor(getActivity()));
//            mRButton.setTextColor(ECGHighlight.Type.R.getColor(getActivity()));
//            mSButton.setTextColor(ECGHighlight.Type.S.getColor(getActivity()));
//            mTButton.setTextColor(ECGHighlight.Type.T.getColor(getActivity()));
//            mR2Button.setTextColor(ECGHighlight.Type.R2.getColor(getActivity()));

            mUnitTextView1.setText(getActivity().getString(R.string.HISTORY_TEXT_LIST_ECG_VALUES));

            mUnitTextView2.setVisibility(View.GONE);
            mUnitTextView3.setVisibility(View.GONE);

            mValueListView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.teldoc_divider_simple)));
            mValueListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mAdapter = new HistoryValueAdapter.ECGHistoryAdapter(getActivity());
            mAdapter.setResultSelectedListener(this);
            mValueListView.setAdapter(mAdapter);
        }

        @Override
        public void onResultSelected(Result result) {
            showLoading();
            mCurrentGraphName = ((ECGGraph) result).getName();
            setChartList(((ECGGraph) result).plotData(getActivity()));
            uncheckButtons();
        }

        @Override
        protected void setChartData(List<Result> resultList) {}

        private void setChartList(final List<LineChart> chartList) {
            mDiagramContainer.removeAllViews();
            for (LineChart chart : chartList) {
                chart.setMaxHighlightDistance(300f);

                chart.getXAxis().setDrawGridLines(true);
                chart.getXAxis().setDrawAxisLine(false);
                chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                chart.getXAxis().setValueFormatter((value, axis) -> String.format("%.2f", value) + " s");
                chart.getAxisLeft().setDrawZeroLine(true);
                chart.getAxisLeft().setDrawGridLines(true);
                chart.getAxisLeft().setDrawAxisLine(false);
                chart.getAxisLeft().setValueFormatter((value, axis) -> String.format("%.0f", value) + " mV");
                chart.getAxisRight().setEnabled(false);
                chart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                chart.setDescription(null);
                chart.getLegend().setEnabled(false);
                mDiagramContainer.addView(chart);
//                chart.invalidate();

                ((ECGLineChart) chart).setMeasurementsListener(measurements -> {
                    mAdapter.notifyItemChanged(mAdapter.getCurrentSelectedPos(), measurements);
                    getPresenter().storeECGMeasurement(mCurrentGraphName, measurements);
                });
            }
            hideLoading();
        }

        private void measurementButtonTapped(ToggleButton button, ECGMeasurements.SegmentType segmentType) {
            if (button.isChecked()) {
                for (int i = 0; i < mMeasurementButtonContainer.getChildCount(); i++) {
                    ToggleButton childButton = (ToggleButton) mMeasurementButtonContainer.getChildAt(i);
                    if (childButton.getId() == button.getId()) {
                        continue;
                    }
                    childButton.setChecked(false);
                }
                for (int i = 0; i < mDiagramContainer.getChildCount(); ++i) {
                    ECGLineChart chart = (ECGLineChart) (mDiagramContainer.getChildAt(i));
                    chart.setCurrentHighlightSelection(segmentType);
                }
            } else {
                for (int i = 0; i < mDiagramContainer.getChildCount(); ++i) {
                    ECGLineChart chart = (ECGLineChart) (mDiagramContainer.getChildAt(i));
                    chart.setCurrentHighlightSelection(null);
                }
            }
        }

        private void uncheckButtons() {
            for (int i = 0; i < mMeasurementButtonContainer.getChildCount(); i++) {
                ((ToggleButton) mMeasurementButtonContainer.getChildAt(i)).setChecked(false);
            }
            for (int i = 0; i < mDiagramContainer.getChildCount(); ++i) {
                ECGLineChart chart = (ECGLineChart) (mDiagramContainer.getChildAt(i));
                chart.setCurrentHighlightSelection(null);
            }
        }

        @Override
        public void recordECGMeasurementsDone() {
            hideLoading();
        }

        @Override
        public void recordECGMeasurementsError() {
            hideLoading();
        }

        @Override
        public void pulseCalculated() {
            mAdapter.notifyDataSetChanged();
        }
    }
}
