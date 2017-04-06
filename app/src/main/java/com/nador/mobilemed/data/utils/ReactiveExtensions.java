package com.nador.mobilemed.data.utils;

import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by nador on 26/05/16.
 */
public final class ReactiveExtensions {

    private ReactiveExtensions() {}

    // Create an Observable transformer for applying worker-thread execution/main-thread result dispatch
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, T> applyIOSchedulers() {
        return tObservable -> tObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    // Create an Observable transformer for applying worker-thread execution/worker-thread result dispatch
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, T> applyIOtoIOSchedulers() {
        return tObservable -> tObservable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    // Create an Observable transformer for applying worker-thread execution/main-thread result dispatch
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, T> applyComputeSchedulers() {
        return tObservable -> tObservable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Create an Observable transformer for applying worker-thread execution/main-thread result dispatch
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, T> applyNewThreadSchedulers() {
        return tObservable -> tObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
