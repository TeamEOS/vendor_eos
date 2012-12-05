
package org.eos.controlcenter;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class AppPackage {
    private ComponentName component;
    private String appName;
    private Drawable icon;

    AppPackage(ResolveInfo ri, PackageManager pm) {
        component = new ComponentName(ri.activityInfo.packageName,
                ri.activityInfo.name);
        appName = ri.activityInfo.loadLabel(pm).toString();
        icon = ri.activityInfo.loadIcon(pm);
    }

    String getComponentName() {
        return component.flattenToString();
    }

    Drawable getIcon() {
        return icon;
    }

    String getName() {
        return appName;
    }
}
