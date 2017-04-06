package com.nador.mobilemed.presentation.function.management;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.nador.mobilemed.data.entity.Appointment;

import java.util.ArrayList;

/**
 * Created by nador on 18/07/16.
 */
public interface IAppointmentFunctionController extends MvpView {
    void setAppointmentList(final ArrayList<Appointment> appointmentList);
    void getAppointmentListFailed();

    void editAppointmentSuccessful();
    void editAppointmentFailed();
}
