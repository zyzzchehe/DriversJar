package com.comm.utils;

import android.util.Log;

import java.util.Observable;
import java.util.Observer;

/**
 * 观察者
 */
public class DataObserver implements Observer {
    private final static String TAG = "DataObserver";
    private ICommonDataObservable iCommonDataObservable;

    public interface ICommonDataObservable {
        void commonDataObservable(CommonDataObservable commonDataObservable);
    }

    public void setiCommonDataObservable(ICommonDataObservable iCommonDataObservable) {
        this.iCommonDataObservable = iCommonDataObservable;
    }

    @Override
    public void update(Observable o, Object obj) {
        Log.i(TAG, "update: o = "+o+" ;obj = "+obj);
        if (o instanceof CommonDataObservable) {
            CommonDataObservable commonDataObservable = (CommonDataObservable) o;
            if (iCommonDataObservable != null) {
                iCommonDataObservable.commonDataObservable(commonDataObservable);
            }
        }

    }
}
