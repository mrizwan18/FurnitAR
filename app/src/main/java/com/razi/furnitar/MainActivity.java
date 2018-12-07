package com.razi.furnitar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private RecyclerViewAdapter adapter, ARadapter, NonARadapter;
    FirebaseAuth gAuth;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener aL;
    private Context c;
    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onStart() {
        super.onStart();
        gAuth.addAuthStateListener(aL);
        mGoogleApiClient.connect();
        adapter.startListening();
    }
    @BindView(R.id.toolbar_main)
    public Toolbar toolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolBar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolBar);

        DrawerUtil.getDrawer(this, toolBar);
        gAuth = FirebaseAuth.getInstance();
        c = this;


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Query query;
        query = db.collection("items");
        FirestoreRecyclerOptions<Item> options;
        options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();
        adapter = new RecyclerViewAdapter(options);

        query = db.collection("items").whereEqualTo("isAR", true);

        options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();
        ARadapter = new RecyclerViewAdapter(options);

        query = db.collection("items").whereEqualTo("isAR", false);

        options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();
        NonARadapter = new RecyclerViewAdapter(options);


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
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i("OK", "NOT OK");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }


    private void initRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
                Bundle bundle = new Bundle();
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                String path = documentSnapshot.getReference().getPath();
                String id = documentSnapshot.getId();
                String name = documentSnapshot.get("name").toString();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                Intent intent = new Intent(getApplicationContext(), ItemDetail.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }


    public void getDataOfUser() {
        return;
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
