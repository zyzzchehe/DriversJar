package com.comm.utils;

import java.util.Observable;
import java.util.Observer;

public class DataObserver implements Observer {

    private ICommonDataObservable iCommonDataObservable;

    public interface ICommonDataObservable {
        void commonDataObservable(CommonDataObservable commonDataObservable);
    }

    public void setiCommonDataObservable(ICommonDataObservable iCommonDataObservable) {
        this.iCommonDataObservable = iCommonDataObservable;
    }

    @Override
    public void update(Observable o, Object obj) {
        if (o instanceof CommonDataObservable) {
            CommonDataObservable commonDataObservable = (CommonDataObservable) o;
            if (iCommonDataObservable != null) {
                iCommonDataObservable.commonDataObservable(commonDataObservable);
            }
        }

    }
}
