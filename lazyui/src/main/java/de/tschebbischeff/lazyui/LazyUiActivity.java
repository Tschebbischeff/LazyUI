package de.tschebbischeff.lazyui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * The laziest kind of implementing lazy UI. Just extend from this class and implement the abstract methods.
 */
public class LazyUiActivity extends AppCompatActivity implements ILazyUiCallbacks, NavigationView.OnNavigationItemSelectedListener {

    private ContentPageLibrary contentPageLibrary;

    protected LazyUiActivity() {
        this.contentPageLibrary = new ContentPageLibrary(this.getWrapperLayout(),
                this.getLayoutInflater(),
                this.getContentPageSharedData(),
                this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getActivityLayout());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        registerReceiver(broadcastReceiver, new IntentFilter());

        this.createContentPages(this.contentPageLibrary);
    }

    /**
     * Replaces the intent filter of the broadcast receiver with the current contents filter
     * Called automatically when the content changes.
     */
    public void refreshBroadcastReceiverFilter(String[] filterActions) {
        IntentFilter filter = new IntentFilter();
        for (String action : filterActions) {
            filter.addAction(action);
        }
        unregisterReceiver(broadcastReceiver);
        registerReceiver(broadcastReceiver, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.contentPageLibrary.dispatchOnResume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
        this.contentPageLibrary.dispatchOnPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Overrides default back-button behaviour with one that closes the navigation drawer if it is open
     * and uses the default behaviour as fallback when the navigation drawer is not open.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Delegates the changing of the layout content to the content class: {@link MainContentLayouts}.
     * And closes the navigation drawer afterwards.
     *
     * @param item The selected item, supplied by listener interface
     * @return false, the selected item is changed by the {@link MainContentLayouts} class and should not be marked selected by the drawer.
     * @see MainContentLayouts#changeToId(Activity, int)
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        MainContentLayouts.changeToId(this, item.getItemId());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * Handles and dispatches button clicks
     *
     * @param view The clicked view
     */
    public void onClick(View view) {
        MainContentLayouts.dispatchOnClick(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainContentLayouts.dispatchOnRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainContentLayouts.dispatchOnReceive(context, intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
