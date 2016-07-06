package com.ladwa.aditya.twitone.mainscreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ladwa.aditya.twitone.R;
import com.ladwa.aditya.twitone.data.local.models.User;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import timber.log.Timber;

/**
 * This is The Launcher Activity of the App
 */
public class MainScreen extends AppCompatActivity implements Drawer.OnDrawerItemClickListener, MainScreenFragment.DrawerCallback {

    private Toolbar toolbar;
    private AccountHeader headerResult;
    private ProfileDrawerItem profileDrawerItem;
    private Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void setupDrawer(String screenName) {

        final PrimaryDrawerItem timeline = new PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_reorder).
                withIdentifier(1).withName(R.string.drawer_timeline);
        final PrimaryDrawerItem interaction = new PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_people).withIdentifier(2).withName(R.string.drawer_interaction);
        final PrimaryDrawerItem message = new PrimaryDrawerItem()
                .withIcon(GoogleMaterial.Icon.gmd_question_answer).withIdentifier(3).withName(R.string.drawer_message);
        final PrimaryDrawerItem settings = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.drawer_setting);

        profileDrawerItem = new ProfileDrawerItem()
                .withName(screenName)
                .withEmail("Email");

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.leak_canary_icon)
                .addProfiles(profileDrawerItem).build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        timeline,
                        interaction,
                        message,
                        new DividerDrawerItem(),
                        settings
                )
                .withOnDrawerItemClickListener(this)
                .build();


    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Toast.makeText(MainScreen.this, "Selected on " + position, Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    public void setProfile(String screenName) {
        Timber.d("Profile set");
        setupDrawer(screenName);
    }

    @Override
    public void updateProfile(User user) {
        Timber.d(user.getName() + user.getScreenName());
        profileDrawerItem.withName(user.getName()).withEmail(user.getScreenName()).withIcon(user.getProfileUrl());
        ImageView headerBackgroundView = headerResult.getHeaderBackgroundView();
        Glide.with(this).load(user.getBannerUrl()).into(headerBackgroundView);
        headerResult.updateProfile(profileDrawerItem);
    }

}
