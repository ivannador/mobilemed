package com.nador.mobilemed.presentation.function.management;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by nador on 18/07/16.
 */
public interface IAppointmentRequestController extends MvpView {
    void setDoctorList(final List<String> doctorList);
}
