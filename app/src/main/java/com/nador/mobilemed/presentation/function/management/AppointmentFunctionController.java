package com.nador.mobilemed.presentation.function.management;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.nador.mobilemed.data.entity.Appointment;
import com.nador.mobilemed.presentation.function.base.FunctionController;
import com.nador.mobilemed.presentation.function.management.adapter.AppointmentListAdapter;
import com.nador.mobilemed.presentation.function.management.adapter.AppointmentParentListItem;
import com.nador.mobilemed.presentation.presenter.function.management.AppointmentFunctionControllerPresenter;
import com.nador.mobilemed.presentation.widget.BulkyAlertDialog;
import com.nador.mobilemed.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by nador on 18/07/16.
 */
public class AppointmentFunctionController extends FunctionController<IAppointmentFunctionController, AppointmentFunctionControllerPresenter> implements IAppointmentFunctionController {

    @Bind(R.id.noAppointmentLayout)
    ViewGroup mNoAppointmentLayout;
    @Bind(R.id.appointmentListView)
    RecyclerView mAppointmentListView;

    @OnClick(R.id.emptyRequestButton)
    protected void onEmptyRequestButtonTapped() {
        navigateToRequest();
    }
    @OnClick(R.id.requestButton)
    protected void onRequestButtonTapped() {
        navigateToRequest();
    }

    private AppointmentListAdapter mAdapter;

    private boolean mAddAppointmentVisible = false;

    AppointmentListAdapter.AppointmentStatusListener appointmentStatusListener = new AppointmentListAdapter.AppointmentStatusListener() {
        @Override
        public void onAcceptClicked(Appointment appointment) {
            appointment.setAppointmentStatus(Appointment.AppointmentStatus.ACCEPTED);
            getPresenter().editAppointment(appointment);
        }

        @Override
        public void onCancelClicked(Appointment appointment) {
            appointment.setAppointmentStatus(Appointment.AppointmentStatus.CANCELED);
            getPresenter().editAppointment(appointment);
        }

        @Override
        public void onRejectClicked(Appointment appointment) {
            appointment.setAppointmentStatus(Appointment.AppointmentStatus.REJECTED);
            getPresenter().editAppointment(appointment);
        }
    };

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_appointment, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mAppointmentListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        showLoading();
        getPresenter().getAppointmentList(false);
    }

    @NonNull
    @Override
    public AppointmentFunctionControllerPresenter createPresenter() {
        return new AppointmentFunctionControllerPresenter(getActivity());
    }

    @Override
    protected void setMainTitle() {
        mFunctionTitleTextView.setText(getActivity().getString(R.string.FUNCTION_TITLE_APPOINTMENT));
    }

    @Override
    public void setAppointmentList(ArrayList<Appointment> appointmentList) {
        hideLoading();
        if (appointmentList.isEmpty()) {
            mNoAppointmentLayout.setVisibility(View.VISIBLE);
        } else {
            mNoAppointmentLayout.setVisibility(View.GONE);
            mAdapter = new AppointmentListAdapter(getActivity(), appointmentStatusListener, wrapAppointmentList(appointmentList));
            mAppointmentListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void getAppointmentListFailed() {
        hideLoading();
        Context context = getActivity();
        BulkyAlertDialog alertDialog = new BulkyAlertDialog(context, context.getString(R.string.GLOBAL_ALERT_FAILURE));
        alertDialog.show();
    }

    @Override
    public void editAppointmentSuccessful() {
        getPresenter().getAppointmentList(true);
        if (mAddAppointmentVisible) {
            mAddAppointmentVisible = false;
            onBackButtonTapped();
        }
    }

    @Override
    public void editAppointmentFailed() {
        hideLoading();
        Context context = getActivity();
        BulkyAlertDialog alertDialog = new BulkyAlertDialog(context, context.getString(R.string.GLOBAL_ALERT_FAILURE));
        alertDialog.show();
    }

    public void requestDone(final Appointment appointment) {
        showLoading();
        getPresenter().editAppointment(appointment);
    }

    private void navigateToRequest() {
        mBackButton.setText(getActivity().getString(R.string.FUNCTION_BUTTON_BACK));
        mAddAppointmentVisible = true;
        getChildRouter(mContainerLayout, null).setPopsLastView(true).pushController(RouterTransaction.with(new AppointmentRequestController())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    private ArrayList<AppointmentParentListItem> wrapAppointmentList(final List<Appointment> appointmentList) {
        ArrayList<AppointmentParentListItem> appointmentParentListItems = new ArrayList<>();
        for (Appointment appointment : appointmentList) {
            appointmentParentListItems.add(new AppointmentParentListItem(appointment));
        }
        return appointmentParentListItems;
    }
}
