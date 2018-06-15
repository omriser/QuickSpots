package com.example.omri.placesretrofit20;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.Profile;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialize.holder.ImageHolder;
import com.squareup.picasso.Picasso;

import java.util.Random;

/**
 * Created by omri on 27/01/2018.
 */

public class MyDrawerManager {




    static SharedPreferences sp;


    static Drawer drawer;
    static Toolbar toolbar;

    static AccountHeader headerResult;
    static ProfileDrawerItem profileDrawerItem = MyDataManager.profileDrawerItem;

    public static void addDrawer (final Activity activity){
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(MyDataManager.resources.getString(R.string.home)).withIcon(GoogleMaterial.Icon.gmd_home);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(MyDataManager.resources.getString(R.string.favorite_all_caps)).withIcon(GoogleMaterial.Icon.gmd_favorite);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(2).withName(MyDataManager.resources.getString(R.string.search)).withIcon(GoogleMaterial.Icon.gmd_search);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(MyDataManager.resources.getString(R.string.facebook_login)).withIcon(R.drawable.com_facebook_button_icon_blue);
       final SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(4).withName(MyDataManager.resources.getString(R.string.hebrew)).withIcon(R.drawable.israel);
       final SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(5).withName(MyDataManager.resources.getString(R.string.english)).withIcon(R.drawable.usa);
        SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(6).withName(MyDataManager.resources.getString(R.string.km));
        SecondaryDrawerItem item8 = new SecondaryDrawerItem().withIdentifier(7).withName(MyDataManager.resources.getString(R.string.miles));



        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

    /*
    @Override
    public Drawable placeholder(Context ctx) {
        return super.placeholder(ctx);
    }

    @Override
    public Drawable placeholder(Context ctx, String tag) {
        return super.placeholder(ctx, tag);
    }
    */
        });

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
           profileDrawerItem.withName(profile.getName()).withIcon(profile.getProfilePictureUri(500,500));
        }

        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.headar1)
                .withDividerBelowHeader(false)
                .addProfiles(profileDrawerItem)
                .build();


        drawer = new DrawerBuilder()
                .withActivity(activity)

                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),item5,
                        item2,item3,
                         new SectionDrawerItem().withName(MyDataManager.resources.getString(R.string.language_setting)),
                        item4,item6,
                        new SectionDrawerItem().withName(MyDataManager.resources.getString(R.string.km_or_miles)),
                        item7,item8
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 1 :
                                Intent intent = new Intent(activity,MainActivity.class);
                                activity.startActivity(intent);
                                break;
                            case 3 :
                                Intent intent1 = new Intent(activity,SearchResultActivity.class);
                                activity.startActivity(intent1);
                                break;
                            case 4 :
                                Intent intent2 = new Intent(activity,FavoritActivity.class);
                                if(MyDataManager.favorite != null) {
                                    if (MyDataManager.favorite.isEmpty()) {
                                        new MaterialDialog.Builder(activity)
                                                .title(MyDataManager.resources.getString(R.string.no_fav_dialog_title))
                                                .content(MyDataManager.resources.getString(R.string.no_fav_dialog_text))
                                                .positiveText(MyDataManager.resources.getString(R.string.no_fav_dialog_positive))
                                                .show();
                                    } else {
                                        activity.startActivity(intent2);
                                    }
                                }else {
                                    new MaterialDialog.Builder(activity)
                                            .title(MyDataManager.resources.getString(R.string.no_fav_dialog_title))
                                            .content(MyDataManager.resources.getString(R.string.no_fav_dialog_text))
                                            .positiveText(MyDataManager.resources.getString(R.string.no_fav_dialog_positive))
                                            .show();
                                }
                                break;
                            case 5 :
                                Intent intent3 = new Intent(activity,FacebookLoginActivity.class);
                                activity.startActivity(intent3);
                                break;
                            case 7 :
                                MyDataManager.languageStr = "iw";
                                MyDataManager.changeLanguage(activity, "iw");
                                activity.finish();
                                MyDataManager.saveLanguage(activity);
                                Intent i = new Intent(activity, MainActivity.class);

                                activity.startActivity(i);
                                break;
                            case 8 :
                                MyDataManager.languageStr = "en";
                                MyDataManager.changeLanguage(activity, "en");
                                activity.finish();
                                MyDataManager.saveLanguage(activity);
                                Intent i2 = new Intent(activity, MainActivity.class);
                                activity.startActivity(i2);
                                break;
                            case 10 :
                                MyDataManager.milesOrKm = "km";
                                break;
                            case 11 :
                                MyDataManager.milesOrKm = "miles";
                                break;

                        }
                        return false;
                    }
                })
                .build();
    }



}
