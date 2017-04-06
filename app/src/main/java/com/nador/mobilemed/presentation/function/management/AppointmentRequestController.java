package com.nador.mobilemed.presentation.function.management;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.Appointment;
import com.nador.mobilemed.presentation.base.BaseController;
import com.nador.mobilemed.presentation.presenter.function.management.AppointmentRequestControllerPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by nador on 18/07/16.
 */
public class AppointmentRequestController extends BaseController<IAppointmentRequestController, AppointmentRequestControllerPresenter> implements IAppointmentRequestController {

    private static final int COMMENT_LIMIT = 150;

    @Bind(R.id.doctorSpinner)
    Spinner mDoctorSpinner;
    @Bind(R.id.commentEditText)
    EditText mCommentEditText;
    @Bind(R.id.characterCountTextView)
    TextView mCharacterCountTextView;

    @OnClick(R.id.sendButton)
    protected void onSendButtonTapped() {
        requestDone();
    }

    private ArrayAdapter<String> mSpinnerAdapter;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_appointment_request, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDoctorSpinner.setAdapter(mSpinnerAdapter);
        mDoctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(COMMENT_LIMIT);
        mCommentEditText.setFilters(FilterArray);
        mCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mCharacterCountTextView.setText(COMMENT_LIMIT - s.toString().length() + "/" + String.valueOf(COMMENT_LIMIT));
            }
        });
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        getPresenter().getDoctorList();
    }

    @NonNull
    @Override
    public AppointmentRequestControllerPresenter createPresenter() {
        return new AppointmentRequestControllerPresenter();
    }

    @Override
    public void setDoctorList(List<String> doctorList) {
        if (mSpinnerAdapter != null) {
            mSpinnerAdapter.addAll(doctorList);
            mSpinnerAdapter.notifyDataSetChanged();
        }
    }

    private void requestDone() {
        getFunctionController().requestDone(
                new Appointment(
                        Appointment.AppointmentStatus.AWAITING_DOCTOR,
                        mDoctorSpinner.getSelectedItem().toString(),
                        mCommentEditText.getText().toString(),
                        System.currentTimeMillis()));
    }

    private AppointmentFunctionController getFunctionController() {
        return (AppointmentFunctionController) getParentController();
    }
}
