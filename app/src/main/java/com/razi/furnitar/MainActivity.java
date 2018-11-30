package com.razi.furnitar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Item> items;
    private RecyclerViewAdapter adapter;
    private Button logout;
    FirebaseAuth gAuth;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener aL;
    private Context c;

    @Override
    protected void onStart() {
        super.onStart();
        gAuth.addAuthStateListener(aL);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logout = findViewById(R.id.logout);
        gAuth = FirebaseAuth.getInstance();
        c = this;
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signOut();
            }
        });
        items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ImageView imageId = new ImageView(this);
            imageId.setImageResource(R.drawable.chair);
            Item t = new Item("Furniture No. " + i, i + 200.0, imageId, true);
            items.add(t);
        }

        initRecyclerView();
        aL = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        };
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i("OK", "NOT OK");
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.list);
        adapter = new RecyclerViewAdapter(items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //put something you want to happen here eg.
                        startActivity(new Intent(MainActivity.this, Login.class));
                    }
                });
    }

    public void getDataOfUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }
}
