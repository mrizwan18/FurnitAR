package com.razi.furnitar;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Database.Database;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.ArCoreApk;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import butterknife.BindView;

public class Item_Detail extends AppCompatActivity {
    TextView item_details_name, item_details_price, item_details_description;
    ImageView item_detail_image;
    CollapsingToolbarLayout toolbar;
    FloatingActionButton fab;
    ElegantNumberButton picker;
    private static GoogleApiClient mGoogleApiClient;
    internetConnectivity it;
    private static Context c;
    String itemId = "";
    FirebaseFirestore db;
    DocumentReference itemRef;
    Button view_ar;
    Item item;
    private FirebaseAnalytics mFirebaseAnalytics;
    @BindView(R.id.toolbar)
    public Toolbar toolBar;

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item__detail);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        itemRef = db.document(path);
        item_details_name = findViewById(R.id.item_name);
        item_details_price = findViewById(R.id.item_price);
        item_details_description = findViewById(R.id.item_description);
        item_detail_image = findViewById(R.id.detail_image);
        fab = findViewById(R.id.cart_btn);
        picker = findViewById(R.id.quantity_picker);
        toolbar = findViewById(R.id.collapse);
        toolbar.setExpandedTitleTextAppearance(R.style.appbar);
        toolbar.setCollapsedTitleTextAppearance(R.style.Collapsed);
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this, toolBar);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        view_ar = findViewById(R.id.view_AR);
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        it = new internetConnectivity();
        registerReceiver(it, in);

        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                item = task.getResult().toObject(Item.class);
                Picasso.get()
                        .load(item.getImages().get(0))
                        .into(item_detail_image);
                item_details_name.setText(item.getName());
                item_details_price.setText("$ " + item.getPrice());
                item_details_description.setText(item.getDescription());
                if (item.getIsAR() && maybeEnableArButton()) {
                    view_ar.setVisibility(View.VISIBLE);
                    view_ar.setEnabled(true);
                } else {
                    view_ar.setVisibility(View.INVISIBLE);
                    if (item.getIsAR()) {
                        view_ar.setVisibility(View.VISIBLE);
                        view_ar.setEnabled(false);
                        view_ar.setText("AR Not Supported");
                        view_ar.setTextSize(12f);
                    }
                }
                picker.setRange(1, item.getQuantity());
                toolbar.setTitle(item.getName());
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> Log.i("OK", "NOT OK"))
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        try {
            MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            adView.loadAd(adRequest);
        } catch (Exception e) {
            Log.d("qwerty", "ad:" + e.getMessage());
        }
    }

    protected void onDestroy() {
        unregisterReceiver(it);
        super.onDestroy();
    }

    boolean maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Re-query at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            return true;
        }
        return false;
    }


    public void viewInAR(View view) {
        view_ar.setEnabled(true);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        String id = itemRef.getId();
        String name = item.getName();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name + ": View in AR");
        String asset = item.getImages().get(1);
        Intent intent = new Intent(getApplicationContext(), ARactivity.class);
        intent.putExtra("asset", asset);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        startActivity(new Intent(Item_Detail.this, MainActivity.class));
    }

    public void addToCart(View view) {
        itemRef.update("quantity", item.getQuantity() - Integer.parseInt(picker.getNumber())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                try {
                    new Database((getBaseContext()))
                            .addToCart(new order(common.currentUser.getId(),
                                    itemRef.getId(),
                                    item.getName(),
                                    item.getPrice(),
                                    Integer.parseInt(picker.getNumber())));
                    Toast.makeText(Item_Detail.this, "Item Added to Cart",
                            Toast.LENGTH_SHORT).show();
                    picker.setRange(1, item.getQuantity() - Integer.parseInt(picker.getNumber()));
                } catch (Exception e) {
                    itemRef.update("quantity", item.getQuantity() + Integer.parseInt(picker.getNumber()));
                    Toast.makeText(Item_Detail.this, "Something went Wrong",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                status -> c.startActivity(new Intent(c, Login.class)));
    }

}
