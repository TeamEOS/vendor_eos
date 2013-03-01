
package org.eos.controlcenter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PackageServerActivity extends Activity {

    /* callback when packages are loaded */
    public interface OnPackagesLoadedListener {
        public void onPackagesUpdated(int percent);

        public void onPackagesLoaded(boolean loaded);
    }

    private static ArrayList<OnPackagesLoadedListener> mListeners = new ArrayList<OnPackagesLoadedListener>();
    private static ArrayList<AppPackage> components = new ArrayList<AppPackage>();
    private static PackageManager mPm = null;
    private static boolean loaded = false;
    private static boolean isRunning = false;
    private static boolean shouldContinue = true;
    private static int numPackages = 0;
    private static int percentLoaded = 0;

    public static void startPackageServer(PackageManager pm) {
        if (mPm == null) {
            mPm = pm;
        }
        new PackageLoader().execute();
    }

    public static ArrayList<AppPackage> getPackageList() {
        return components;
    }

    public static void setOnPackagesLoadedListener(OnPackagesLoadedListener listener) {
        listener.onPackagesLoaded(loaded);
        listener.onPackagesUpdated(percentLoaded);
        mListeners.add(listener);
    }

    private static void notifyPackagesLoaded(boolean loaded) {
        for (OnPackagesLoadedListener listener : mListeners) {
            listener.onPackagesLoaded(loaded);
        }
    }

    private static void notifyPackagesUpdated(int percent) {
        for (OnPackagesLoadedListener listener : mListeners) {
            listener.onPackagesUpdated(percent);
        }
    }

    @Override
    public void onDestroy() {
        shouldContinue = false;
        isRunning = false;
        loaded = false;
        components = null;
        mPm = null;
        mListeners.clear();
        super.onDestroy();
    }

    private static class PackageLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (!shouldContinue) {
                return null;
            }
            if (!loaded && !isRunning) {
                isRunning = true;
                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> activities = mPm.queryIntentActivities(intent, 0);
                numPackages = activities.size();
                for (ResolveInfo info : activities) {
                    AppPackage ap = new AppPackage(info, mPm);
                    components.add(ap);
                    percentLoaded = Math.round(((components.size() / numPackages) * 100));
                    notifyPackagesUpdated(percentLoaded);
                }
                // sort the app packages by simple name
                Collections.sort(components, new Comparator<AppPackage>() {
                    public int compare(AppPackage ap1, AppPackage ap2) {
                        return ap1.getName().compareToIgnoreCase(ap2.getName());
                    }
                });
                loaded = true;
                notifyPackagesLoaded(loaded);
                isRunning = false;
            }
            return null;
        }
    }
}
