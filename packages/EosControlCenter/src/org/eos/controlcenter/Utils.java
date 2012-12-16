
package org.eos.controlcenter;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.IWindowManager;
import android.view.WindowManager;

import com.android.internal.telephony.RILConstants;

import java.util.List;

public final class Utils {

    public static boolean hasTorch() {
        Camera mCamera = null;
        Parameters parameters;
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            parameters = mCamera.getParameters();
            List<String> flashModes = parameters.getSupportedFlashModes();
            if (flashModes.contains(Parameters.FLASH_MODE_TORCH))
                return true;
        } catch (RuntimeException e) {
            Log.i("EosInterfaceSettings",
                    "Unable to acquire camera or failed to check if device is Torch capable");
        } finally {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
        return false;
    }

    public static boolean hasData(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.isNetworkSupported(ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isCdma(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return (tm.getCurrentPhoneType() == TelephonyManager.PHONE_TYPE_CDMA);
    }

    public static boolean isCdmaLTE(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLteOnCdmaMode() == RILConstants.LTE_ON_CDMA_TRUE;
    }

    public static boolean isHybridUI(Context context) {
        return context.getResources()
                .getBoolean(com.android.internal.R.bool.config_isHybridUiDevice);
    }

    public static boolean hasNavBar(Context context) {
        boolean mHasNavBar = false;
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));
        try {
            mHasNavBar = mWindowManager.hasNavigationBar();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mHasNavBar;
    }
}
