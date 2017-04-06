package com.nador.mobilemed.presentation.presenter.function.management;

import com.nador.mobilemed.presentation.function.management.IAppointmentRequestController;
import com.nador.mobilemed.presentation.presenter.base.SubscribingPresenter;

import java.util.ArrayList;

/**
 * Created by nador on 18/07/16.
 */
public class AppointmentRequestControllerPresenter extends SubscribingPresenter<IAppointmentRequestController> {

    public void getDoctorList() {

        ArrayList<String> doctorList = new ArrayList<>();
        doctorList.add("Test Doctor 1");
        doctorList.add("Test Doctor 2");
        doctorList.add("Long Name Test Doctor 3");

        if (isViewAttached()) {
            getView().setDoctorList(doctorList);
        }
    }
}
