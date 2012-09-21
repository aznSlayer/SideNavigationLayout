SideNavigationLayout
====================

A layout that contains two children, a navigation view to the left and a main view to the right. The main view can be panned to show/hide the navigation view.

![1] -> ![2]

**Download:** [JAR library](https://github.com/downloads/herroWorld/SideNavigationLayout/sidenavigationlayout_v1.1.jar)

## Usage
When using this library, simply add the JAR to your project:

```
Right click on project --> Properties --> Java Build Path --> Libraries --> Add External JARs
```

### Example

#### XML File
```
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >

    <!-- Note main_layout and navigation_layout --!>
    <com.herroworld.sidenavigationlayout.SideNavigationLayout
        android:id="@+id/layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:main_layout="@layout/main"
        app:navigation_layout="@layout/navigation" />

</RelativeLayout>
```

#### Code
```
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

/**
 * Update the right pan bound with the width of the navigation view and the
 * left pan bound to 0 to hide the navigation view completely when the main
 * view is viewable.
 */
mLayout.setRightPanBound(displaymetrics.widthPixels - view.getWidth());
mLayout.setLeftPanBound(0);

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

```

## To Do
* Add functionality to support panning of both views.

## License
* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

 [1]: https://github.com/downloads/herroWorld/SideNavigationLayout/sideNavigationLayoutExample1.png
 [2]: https://github.com/downloads/herroWorld/SideNavigationLayout/sideNavigationLayoutExample2.png
