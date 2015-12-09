package com.bosch.si.emobility.bstp.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by sgp0458 on 8/12/15.
 */
public class Application extends android.app.Application {

    private static Application ourInstance;

    public static boolean PRODUCTION = false;

    public static boolean isDevEnvironment() {
        return !Application.PRODUCTION;
    }

    public static Application getInstance() {
        return ourInstance;
    }

    public Application() {
        super();
        ourInstance = this;
    }

    protected Activity currentContext;

    public Activity getCurrentContext() {
        return currentContext;
    }

    private final class ActivityLifecycleCallbacks implements android.app.Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(final Activity activity, Bundle bundle) {
            currentContext = activity;
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            currentContext = activity;
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }
    }
}