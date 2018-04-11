package com.example.ekene.managemeds;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ekene.managemeds.data.model.User;
import com.example.ekene.managemeds.settings.Settings;
import com.example.ekene.managemeds.ui.auth.viewmodel.AddUserViewModel;
import com.example.ekene.managemeds.ui.main.ui.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

public class AuthActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //@BindView (R.id.loginButton)
    Button loginButton;

    private GoogleApiClient googleApiClient;
    private static final int ReqCode = 90;
    private AddUserViewModel addUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        loginButton = findViewById(R.id.loginButton);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
       // ButterKnife.bind(this);

        addUserViewModel = ViewModelProviders.of(this).get(AddUserViewModel.class);


        //build google sign in options
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();


        //sign-in user on button click

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    public void signIn(){

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, ReqCode);

    }
    public void handleResult(GoogleSignInResult result){

        if (result.isSuccess()) {
            GoogleSignInAccount userAccount = result.getSignInAccount();

            String userName = userAccount.getDisplayName();
            String userImage_Url = userAccount.getPhotoUrl().toString();

            addUserViewModel.addUser(new User(
                    "1",
                    userName,
                    userImage_Url
            ));
           // persist data if logged in
            Settings.setLoggedInSharedPref(true);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Settings.isLoggedIn()) {
            Intent auth = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(auth);
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqCode){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}
