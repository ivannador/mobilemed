package com.nador.mobilemed.presentation.presenter.history;

import android.content.Context;

import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.data.entity.FunctionWithLastUsed;
import com.nador.mobilemed.data.manager.FunctionAvailabilityManager;
import com.nador.mobilemed.presentation.history.IHistorySelectorController;
import com.nador.mobilemed.presentation.presenter.base.SubscribingPresenter;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by nador on 28/07/16.
 */
public class HistorySelectorControllerPresenter extends SubscribingPresenter<IHistorySelectorController> {

    @Inject
    FunctionAvailabilityManager mFunctionAvailabilityManager;

    public HistorySelectorControllerPresenter(Context context) {
        MobilemedApp.getUserComponent(context).inject(this);
    }

    public ArrayList<FunctionWithLastUsed> getFunctionList() {
        return mFunctionAvailabilityManager.getFunctionList();
    }
}
