
package org.eos.controlcenter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.eos.controlcenter.PackageServerActivity.OnPackagesLoadedListener;

import java.util.ArrayList;
import java.util.List;

public abstract class ActionFragment extends PreferenceFragment implements OnPackagesLoadedListener {

    protected Context mContext;
    protected ContentResolver mResolver;
    protected LayoutInflater mInflate;
    protected AppArrayAdapter mAdapter;
    protected ListView mListView;
    protected Resources mRes;
    protected ArrayList<ActionPreference> mActionPreferenceList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (Context) getActivity();
        mRes = mContext.getResources();
        mResolver = mContext.getContentResolver();
        mActionPreferenceList = new ArrayList<ActionPreference>();
        populateActionAdapter();
    }

    class WidgetListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String tag = (String) v.getTag();
            if (tag != null) {
                for (ActionPreference ap : mActionPreferenceList) {
                    if (tag.equals(ap.getTargetUri())) {
                        callInitDialog(ap);
                        break;
                    }
                }
            }
        }
    }

    protected void addActionPreference(ActionPreference pref) {
        pref.setListener(new WidgetListener());
        mActionPreferenceList.add(pref);
    }

    private void callInitDialog(final ActionPreference preference) {
        final ActionPreference pref = (ActionPreference) preference;
        final CharSequence[] item_entries = mRes
                .getStringArray(R.array.eos_action_dialog_entries);
        final CharSequence[] item_values = mRes
                .getStringArray(R.array.eos_action_dialog_values);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(
                mRes.getString(R.string.eos_interface_activity_dialog_title))
                .setNegativeButton(mRes.getString(com.android.internal.R.string.cancel),
                        new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                .setItems(item_entries, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String pressed = (String) item_values[which];
                        // be sure to keep applications menu as the last
                        // item
                        if (pressed.equals(item_values[item_values.length - 1])) {
                            callActivityDialog(pref);
                        } else {
                            pref.loadCustomApp(pressed);
                            pref.setTargetValue(pressed);

                        }
                    }
                }).create().show();
    }

    private void populateActionAdapter() {
        mAdapter = new AppArrayAdapter(mContext, PackageServerActivity.getPackageList());
        View dialog = View.inflate(mContext, R.layout.activity_dialog, null);
        mListView = (ListView) dialog.findViewById(R.id.eos_dialog_list);
        mListView.setAdapter(mAdapter);
    }

    private void callActivityDialog(final ActionPreference caller) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setAdapter(mListView.getAdapter(), new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                AppPackage app = (AppPackage) mListView.getAdapter().getItem(which);
                caller.setResourcesFromPackage(app);
            }
        })
                .setTitle(
                        mRes.getString(R.string.eos_interface_activity_dialog_title))
                .setNegativeButton(mRes.getString(com.android.internal.R.string.cancel),
                        new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    private class AppArrayAdapter extends ArrayAdapter {
        private final List<AppPackage> apps;
        private final Context mContext;

        public AppArrayAdapter(Context context, List<AppPackage> objects) {
            super(context, R.layout.activity_item, objects);
            this.mContext = context;
            this.apps = objects;
            // TODO Auto-generated constructor stub
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemRow = convertView;
            AppPackage ap = (AppPackage) apps.get(position);

            itemRow = ((LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.activity_item, null);

            ((ImageView) itemRow.findViewById(R.id.icon)).setImageDrawable(ap.getIcon());
            ((TextView) itemRow.findViewById(R.id.title)).setText(ap.getName());

            return itemRow;
        }
    }

}
