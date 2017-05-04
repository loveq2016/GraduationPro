package com.hyphenate.easeui.idosomething;

/**
 * Created by GZYY on 17/1/10.
 */

public class EaseSingleton {

    private static EaseSingleton singleton;

    public static EaseSingleton getInstance() {

        return SingletonHolder.mEaseSingleton;
    }

    private static class SingletonHolder {
        private static final EaseSingleton mEaseSingleton = new EaseSingleton();
    }




}
