package com.leigo.android.controller.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.leigo.android.controller.adapter.FeedTypeAdapter;
import com.leigo.android.mimi.R;
import com.leigo.android.model.domain.FeedType;
import com.leigo.android.util.Logger;
import com.leigo.android.util.Utils;
import com.leigo.android.view.XListView;


public class MainActivity extends Activity {

    private static final Logger logger = new Logger(MainActivity.class);

    private DisplayMetrics displayMetrics;

    private Animation fadeIn;
    private Animation fadeOut;

    private FeedTypeAdapter feedTypeAdapter;
    private TextView feedTypeName;
    private PopupWindow feedTypePopup;

    private FeedType lastFeedType;

    private XListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isAuthentication = false;
//        if (!isAuthentication) {
//            AccountGuidanceActivity.startFrom(this);
//        }
        displayMetrics = getResources().getDisplayMetrics();

        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        View view = LayoutInflater.from(this).inflate(R.layout.feed_type_view, null);
        feedTypeName = (TextView) view.findViewById(R.id.feed_type_name);
        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
        feedTypeAdapter = new FeedTypeAdapter(this);
        lastFeedType = FeedType.ALL;
        feedTypeName.setText(lastFeedType.titleResId());

        listView = (XListView) findViewById(R.id.list_view);
        listView.enablePullRefresh(false);
    }

    public void clickOnFeedType(View v) {
        if (feedTypePopup == null) {
            initFeedTypePopup();
        }
        if (feedTypePopup.isShowing()) {
            feedTypePopup.dismiss();
        } else {
            feedTypePopup.showAsDropDown(v, -(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, displayMetrics), -(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0F, displayMetrics));
        }
    }

    private Animation getFadeInAnimation() {
        if (fadeIn == null) {
            fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        }
        return fadeIn;
    }

    private Animation getFadeOutAnimation() {
        if (fadeOut == null) {
            fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        }
        return fadeOut;
    }

    private void initFeedTypePopup() {
        ListView listView = (ListView) LayoutInflater.from(this).inflate(R.layout.drop_down_list, null);
        Utils.disableOverScroll(listView);
        listView.setAdapter(feedTypeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeFeedType(FeedType.values()[position]);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        feedTypePopup.dismiss();
                    }
                }, 50L);
            }
        });
        feedTypePopup = new PopupWindow(listView, displayMetrics.widthPixels / 3, -2, true);
        feedTypePopup.setBackgroundDrawable(new BitmapDrawable());
        feedTypePopup.setOutsideTouchable(true);
    }

    private void changeFeedType(FeedType feedType) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        MenuItem newSecretItem = menu.findItem(R.id.action_new_secret);

        final MenuItem notificationItem = menu.findItem(R.id.action_notification);
        View notificationActionView = notificationItem.getActionView();
        notificationActionView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showActionMenuItemTitle(notificationItem);
                return true;
            }
        });

        final MenuItem chatItem = menu.findItem(R.id.action_chat);
        View chatActionView = chatItem.getActionView();
        chatActionView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showActionMenuItemTitle(chatItem);
                return true;
            }
        });

        MenuItem moreItem = menu.findItem(R.id.action_more);
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                return true;
            case R.id.action_settings:
                SettingsActivity.startFrom(this);
                return true;
            case R.id.action_new_secret:
                clickOnPublishAnonymously(null);
                return true;
            case R.id.action_invite:
                startActivity(new Intent(this, InviteActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickOnPublishAnonymously(View v) {
        startActivityForResult(new Intent(this, PublishSecretActivity.class), 0);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
