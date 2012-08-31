
package com.herroworld.sidenavigationlayoutexample;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.herroworld.panview.PanView.OnPanListener;
import com.herroworld.sidenavigationlayout.SideNavigationLayout;

/**
 * This sample application contains a list of website names on the navigation
 * view and, when clicked, will display the contents of the url on the main
 * view.
 */
public class SideNavigationLayoutExample extends Activity implements OnPanListener {
    private SideNavigationLayout mLayout;
    private Button mPanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example);

        // Listening on pan events for the main view
        mLayout = (SideNavigationLayout) findViewById(R.id.layout);
        mLayout.setOnPanListener(this);

        // Setting up the main view containing a webview and a button for
        // automatic panning
        final WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        mPanButton = (Button) findViewById(R.id.panButton);
        mPanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLayout.isNavigationViewVisible()) {
                    mLayout.showMainView();
                } else {
                    mLayout.showNavigationView();
                }
            }
        });

        // Setting up the navigation view
        final ListView listView = (ListView) findViewById(R.id.listview);
        final ListAdapter listAdapter = new ListAdapter(this, R.array.website_names,
                R.array.website_urls);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                webView.loadUrl(listAdapter.getItem(arg2));
                mLayout.showMainView();
            }
        });

        webView.loadUrl(listAdapter.getItem(0));
    }

    /**
     * Update the right pan bound with the width of the navigation view.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        final View view = findViewById(R.id.listview);
        mLayout.setRightPanBound(displaymetrics.widthPixels - view.getWidth());
    }

    /**
     * Adapter for the site names and urls.
     */
    public static class ListAdapter extends BaseAdapter {
        private final Context mContext;
        private final String[] mNames;
        private final String[] mUrls;

        public ListAdapter(Context context, int namesResId, int urlsResId) {
            mContext = context;
            final Resources res = mContext.getResources();

            mNames = res.getStringArray(namesResId);
            mUrls = res.getStringArray(urlsResId);
        }

        @Override
        public int getCount() {
            return mUrls.length;
        }

        @Override
        public String getItem(int position) {
            return mUrls[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView textView;
            if (convertView instanceof TextView) {
                textView = (TextView) convertView;
            } else {
                textView = new TextView(mContext);
                textView.setTextColor(Color.WHITE);
                convertView = textView;
            }

            textView.setText(mNames[position]);

            return convertView;
        }
    }

    @Override
    public void onPanStart() {
    }

    /**
     * Listening onPanEnd notifications to change button text.
     */
    @Override
    public void onPanEnd() {
        if (mLayout.isNavigationViewVisible()) {
            mPanButton.setText(getResources().getString(R.string.unpan));
        } else {
            mPanButton.setText(getResources().getString(R.string.pan));
        }
    }
}
