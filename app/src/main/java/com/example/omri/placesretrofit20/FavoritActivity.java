package com.example.omri.placesretrofit20;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class FavoritActivity extends AppCompatActivity {
    RetrofitMaps myApi;
    Bitmap mBitmap;
    File file;
    String path;
    ImageView img;
    final String key = "AIzaSyBpwvfPnbrz8Es7TF75wgwWsYpeyzcGszk";
    private static final int REQUEST_PHONE_CALL = 1;

    public ArrayList<FavoritePlace> favoritsFromSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorit);

        MyDrawerManager.addDrawer(this);

        // Find the toolbar view inside the activity layout
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        final ListView list = findViewById(R.id.list);
        favoritsFromSave = MyDataManager.ReadFavorites(this);
        if (MyDataManager.favorite.size()==0){
            MyDataManager.favorite.clear();
            for(int i =0; i<favoritsFromSave.size();i++){
                MyDataManager.favorite.add(i,new FavoritePlace(favoritsFromSave.get(i).getName(),favoritsFromSave.get(i).getAddress(),favoritsFromSave.get(i).getPlaceid(),
                              favoritsFromSave.get(i).getNumber(),favoritsFromSave.get(i).getRating(),favoritsFromSave.get(i).getPhotoPath(),favoritsFromSave.get(i).getLat(),
                              favoritsFromSave.get(i).getLng(),favoritsFromSave.get(i).getAddress()));
            }
        }
        final ArrayAdapter<FavoritePlace> adapter = new ArrayAdapter<FavoritePlace>(this, R.layout.favorite_place_item, MyDataManager.favorite) {

            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(FavoritActivity.this).inflate(R.layout.favorite_place_item, null);
                }

                img = convertView.findViewById(R.id.item_image);
                TextView name = convertView.findViewById(R.id.item_name);
                TextView adress = convertView.findViewById(R.id.item_adress);
                final TextView number = convertView.findViewById(R.id.item_num);
                final RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);
                ImageButton phoneCallBtn = convertView.findViewById(R.id.telephone_btn);

                FavoritePlace place = getItem(position);
                name.setText(place.getName());
                adress.setText(place.getAddress());
                number.setText(place.getNumber());
                phoneCallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + number.getText().toString()));
                        if (ContextCompat.checkSelfPermission(FavoritActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(FavoritActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                        }
                        else
                        {
                            if (number.getText().toString() == "No number"){
                                Toast.makeText(FavoritActivity.this, Resources.getSystem().getString(R.string.no_phone_num),Toast.LENGTH_SHORT).show();
                            }else {
                                startActivity(callIntent);
                            }
                        }

                    }
                });
                ratingBar.setRating(place.getRating());
                path =place.getPhotoPath();
                if(path != null) {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        img.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 350, 350, false));
                    }
                }

                //בדיקה אם קיים חיבור אינטרנט
                if(MyDataManager.isConnected(FavoritActivity.this)) {
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FavoritActivity.this, PlaceActivity.class);
                            intent.putExtra("place_index_favorite", position);
                            startActivity(intent);
                        }
                    });
                }
                    convertView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(final View view) {
                            final CharSequence[] items = {MyDataManager.resources.getString(R.string.delete_all), MyDataManager.resources.getString(R.string.delete_only)+" "+
                                    MyDataManager.favorite.get(position).getName().toString() + " "+MyDataManager.resources.getString(R.string.from_fav), MyDataManager.resources.getString(R.string.cancel)};
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                            builder.setTitle(MyDataManager.resources.getString(R.string.select_the_action));
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            int size = MyDataManager.favorite.size();
                                            for (int i = 0; i < size; i++) {
                                                path = MyDataManager.favorite.get(i).getPhotoPath();
                                                File file = new File(path);
                                                boolean deleted = file.delete();
                                            }
                                            MyDataManager.favorite.clear();
                                            notifyDataSetChanged();
                                            break;
                                        case 1:
                                            MyDataManager.favorite.remove(position);
                                            File file = new File(path);
                                            boolean deleted = file.delete();
                                            notifyDataSetChanged();
                                            break;
                                        case 2:

                                            break;
                                        default:
                                            break;
                                    }
                                }
                            });
                            builder.show();
                            return true;
                        }
                    });

                return convertView;
            }

        };
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    //TODO: להגדיר כםתור של תפריט בטול בר
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                if (MyDrawerManager.drawer.isDrawerOpen()){
                    MyDrawerManager.drawer.closeDrawer();
                }
                MyDrawerManager.drawer.openDrawer();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onStop() {
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        super.onPause();
    }
}