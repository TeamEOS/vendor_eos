
package org.eos.controlcenter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class TextInfoFragment extends DialogFragment {
    String mContent;
    static String mTitle;
    static int mContentRes;

    public static TextInfoFragment newInstance(Bundle b) {
        mTitle = b.getString(Utils.TEXT_FRAGMENT_TITLE_KEY);
        mContentRes = b.getInt(Utils.TEXT_FRAGMENT_TEXT_RES_KEY);
        return new TextInfoFragment();
    }

    public TextInfoFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContent = Utils.readRawTextFile(getActivity(), mContentRes);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        ScrollView vScroll = new ScrollView(getActivity());
        HorizontalScrollView hScroll = new HorizontalScrollView(getActivity());
        TextView mText = new TextView(getActivity());
        mText.setPadding(20, 10, 10, 10);
        mText.setText(mContent);
        hScroll.addView(mText);
        vScroll.addView(hScroll);
        builder.setView(vScroll);
        return builder.create();
    }
}
