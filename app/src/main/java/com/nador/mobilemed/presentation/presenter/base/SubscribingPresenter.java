package com.nador.mobilemed.presentation.presenter.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import rx.subscriptions.CompositeSubscription;

/**
 * Base presenter that stores Observable subscriptions
 * and unsubscribes when presenter goes out of scope
 * Use this if You subscribe to Observables in the presenter
 *
 * Created by nador on 20/06/16.
 */
public abstract class SubscribingPresenter<V extends MvpView> extends MvpBasePresenter<V> {

    protected CompositeSubscription mSubscriptions;

    @Override
    public void attachView(V view) {
        super.attachView(view);
        if (mSubscriptions == null) {
            mSubscriptions = new CompositeSubscription();
        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (mSubscriptions != null && !retainInstance) {
            mSubscriptions.unsubscribe();
            mSubscriptions = null;
        }
    }
}
