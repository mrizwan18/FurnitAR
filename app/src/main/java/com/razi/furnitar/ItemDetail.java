package com.razi.furnitar;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.ArCoreApk;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ItemDetail extends AppCompatActivity {

    FirebaseFirestore db;
    DocumentReference itemRef;
    ImageView itemImages;
    TextView itemName, itemPrice, errorText;
    Button btn;
    NumberPicker numberPicker;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        itemImages = findViewById(R.id.itemImages);
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        btn = findViewById(R.id.viewInAR);
        numberPicker = findViewById(R.id.quantity);
        errorText = findViewById(R.id.errorText);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        db = FirebaseFirestore.getInstance();
        itemRef = db.document(path);
        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                item = task.getResult().toObject(Item.class);
                Picasso.get()
                        .load(item.getImages().get(0))
                        .into(itemImages);
                itemName.setText(item.getName());
                itemPrice.setText("Rs. " + item.getPrice());
                if(item.getIsAR() && maybeEnableArButton()){
                    btn.setVisibility(View.VISIBLE);
                }
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(item.getQuantity());
            }
        });
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
        } else { // Unsupported or unknown.
            errorText.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public void viewInAR(View view) {
        String asset = item.getImages().get(1);
        Intent intent = new Intent(getApplicationContext(), ARactivity.class);
        intent.putExtra("asset", asset);
        startActivity(intent);
    }
}
