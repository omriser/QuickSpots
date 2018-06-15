package com.example.omri.placesretrofit20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class FacebookLoginActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 5000;
    LoginButton loginButton;
    public static CallbackManager callbackManager;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public static LoginManager loginManager;
    static GoogleSignInClient mGoogleSignInClient;
    Button googleLogout;
    ImageView userImage;
    TextView userName;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_login);

        MyDrawerManager.addDrawer(this);

        // Find the toolbar view inside the activity layout
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        userImage = findViewById(R.id.facebook_login_image);
        userName = findViewById(R.id.facebook_login_name);
        linearLayout = findViewById(R.id.detilasLayout);
        linearLayout.setVisibility(View.GONE);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Picasso.with(getApplicationContext()).load(profile.getProfilePictureUri(800,800)).into(userImage);
            userName.setText(profile.getName());
            linearLayout.setVisibility(View.VISIBLE);
        }

        loginButton =findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        loginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Bundle params = new Bundle();
                        params.putString("fields", "id,name,email,gender,cover,picture.type(large)");
                        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                            new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            if (response != null) {
                                try {
                                    JSONObject data = response.getJSONObject();
                                    if (data.has("picture")) {
                                        String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                        String profileUserName = data.getString("name");
                                        MyDataManager.facebookPhotoUrl = profilePicUrl;
                                        MyDataManager.facebookUserName = profileUserName;
                                        Picasso.with(getApplicationContext()).load(profilePicUrl).resize(800,800).into(userImage);
                                        userName.setText(profileUserName);
                                        linearLayout.setVisibility(View.VISIBLE);
                                        Log.d("profilePicUrl",profilePicUrl);
                                        MyDataManager.profileDrawerItem.withIcon(profilePicUrl).withName(profileUserName);
                                        MyDrawerManager.headerResult.addProfiles(MyDataManager.profileDrawerItem);

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).executeAsync();
                }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("facebook", exception.toString());
                    }
                });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        MyDataManager.saveGoogleDetails(this);
        MyDataManager.saveFacebookDetails(this);
        super.onStop();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String googleUserName = account.getDisplayName();
            Uri googlePhotoUri= account.getPhotoUrl();


            MyDataManager.googlePhotoUrl = googlePhotoUri;
            MyDataManager.googleUserName = googleUserName;
           // Log.d("profilePicUrl",profilePicUrl);
            MyDataManager.profileDrawerItem.withIcon(googlePhotoUri).withName(googleUserName);
            MyDrawerManager.headerResult.addProfiles(MyDataManager.profileDrawerItem);
            MyDataManager.saveGoogleDetails(this);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
           // Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
           // updateUI(null);
        }
    }
}
