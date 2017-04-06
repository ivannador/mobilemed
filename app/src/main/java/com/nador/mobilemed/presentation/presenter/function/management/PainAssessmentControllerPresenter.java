package com.nador.mobilemed.presentation.presenter.function.management;

import com.nador.mobilemed.data.entity.measurement.PainPoint;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.presentation.function.management.IPainAssessmentController;
import com.nador.mobilemed.presentation.presenter.base.SubscribingPresenter;

import java.util.ArrayList;

/**
 * Created by nador on 25/07/16.
 */
public class PainAssessmentControllerPresenter extends SubscribingPresenter<IPainAssessmentController> {

    private ArrayList<PainPoint> mPainPoints = new ArrayList<>();

    public void addPainPoint(final PainPoint painPoint) {
        mPainPoints.add(painPoint);
    }

    public ArrayList<PainPoint> getPainPoints() {
        return mPainPoints;
    }

    public ArrayList<Result> getPainPointsAsResult() {
        ArrayList<Result> resultList = new ArrayList<>();
        for (Result result : mPainPoints) {
            resultList.add(result);
        }
        return resultList;
    }
}
