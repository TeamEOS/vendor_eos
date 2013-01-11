
package org.teameos.apps.stingrayactivationhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

public class ActivationReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "EosStingrayActivationHelper";
    private static final int CDMA_SUBSCRIPTION_RUIM_SIM = 0;
    private static final int MAX_CYCLES = 5;

    private Phone mPhone;
    private CdmaSubscriptionButtonHandler mHandler;
    private int mCycles = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPhone = PhoneFactory.getDefaultPhone();
        mHandler = new CdmaSubscriptionButtonHandler();

        int currentMode = Settings.Global.getInt(mPhone.getContext().getContentResolver(),
                Settings.Global.CDMA_SUBSCRIPTION_MODE, 1);

        // either device is already activated or will activate successfully
        if (currentMode == CDMA_SUBSCRIPTION_RUIM_SIM)
            return;

        mPhone.setCdmaSubscription(CDMA_SUBSCRIPTION_RUIM_SIM, mHandler
                .obtainMessage(CdmaSubscriptionButtonHandler.MESSAGE_SET_CDMA_SUBSCRIPTION,
                        String.valueOf(CDMA_SUBSCRIPTION_RUIM_SIM)));
        Log.i(LOG_TAG, "Request sent to ril");
    }

    private class CdmaSubscriptionButtonHandler extends Handler {

        static final int MESSAGE_SET_CDMA_SUBSCRIPTION = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SET_CDMA_SUBSCRIPTION:
                    handleSetCdmaSubscriptionMode(msg);
                    break;
            }
        }

        private void handleSetCdmaSubscriptionMode(Message msg) {
            AsyncResult ar = (AsyncResult) msg.obj;
            Log.i(LOG_TAG, "message received from ril");

            if (ar.exception == null) {
                int cdmaSubscriptionMode = Integer.valueOf((String) ar.userObj).intValue();
                if (cdmaSubscriptionMode == CDMA_SUBSCRIPTION_RUIM_SIM) {
                    Settings.Global.putInt(mPhone.getContext().getContentResolver(),
                            Settings.Global.CDMA_SUBSCRIPTION_MODE,
                            cdmaSubscriptionMode);
                } else {
                    if (mCycles == MAX_CYCLES) {
                        return;
                    } else {
                        mCycles++;
                        mPhone.setCdmaSubscription(
                                CDMA_SUBSCRIPTION_RUIM_SIM,
                                mHandler.obtainMessage(
                                                CdmaSubscriptionButtonHandler.MESSAGE_SET_CDMA_SUBSCRIPTION,
                                                String.valueOf(CDMA_SUBSCRIPTION_RUIM_SIM)));
                    }
                }
            } else {
                if (mCycles == MAX_CYCLES) {
                    return;
                } else {
                    Log.e(LOG_TAG, "Setting Cdma subscription source failed");
                    mCycles++;
                    mPhone.setCdmaSubscription(
                            CDMA_SUBSCRIPTION_RUIM_SIM,
                            mHandler.obtainMessage(
                                    CdmaSubscriptionButtonHandler.MESSAGE_SET_CDMA_SUBSCRIPTION,
                                    String.valueOf(CDMA_SUBSCRIPTION_RUIM_SIM)));
                }
            }
        }
    }
}
