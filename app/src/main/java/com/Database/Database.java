package com.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Pair;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.razi.furnitar.common;
import com.razi.furnitar.order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database extends SQLiteAssetHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cartDB.db";
    FirebaseFirestore cloud = FirebaseFirestore.getInstance();

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<order> getCarts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT* FROM cart where userID='" + common.currentUser.getId() + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        final List<order> result = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                result.add(new order(cursor.getString(cursor.getColumnIndex("userID")),
                        cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getDouble(cursor.getColumnIndex("price")),
                        cursor.getInt(cursor.getColumnIndex("quantity"))));
            }
            while (cursor.moveToNext());
        }
        return result;
    }

    public void addToCart(order order) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "select quantity from cart where id = ?";
            int quantity = 0;
            Cursor c = db.rawQuery(query, new String[]{order.getId()});
            while (c.moveToNext()) {
                quantity = c.getInt(c.getColumnIndex("quantity"));
            }
            if (quantity > 0) {
                query = "update cart set quantity = ? where id = ?";
                db.execSQL(query, new Object[]{quantity + order.getQuantity(), order.getId()});
                final String[] id = new String[1];
                int finalQuantity = quantity;
                cloud.collection("cart").whereEqualTo("id", order.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        id[0] = queryDocumentSnapshots.getDocuments().get(0).getId();
                        cloud.collection("cart").document(id[0]).update("quantity", finalQuantity + order.getQuantity());
                    }
                });

                return;
            }
            ContentValues values = new ContentValues();
            values.put("id", order.getId());
            values.put("name", order.getName());
            values.put("price", order.getPrice());
            values.put("quantity", order.getQuantity());
            values.put("userID", order.getUserid());
            db.insertWithOnConflict("cart", null, values, SQLiteDatabase.CONFLICT_NONE);
            Map<String, Object> cart_item = new HashMap<>();
            cart_item.put("id", order.getId());
            cart_item.put("user", order.getUserid());
            cart_item.put("quantity", order.getQuantity());
            cloud.collection("cart").add(cart_item);
        } catch (Exception e) {

        }

    }

    public Pair<String, Integer> removeFromCart(order order) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlselect = {"id", "quantity"};
        String sqlTable = "cart";
        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlselect, "id = ?", new String[]{order.getId()}, null, null, null);
        ArrayList<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>();
        if (c.moveToNext()) {
            do {
                result.add(new Pair(c.getString(c.getColumnIndex("id")),
                        c.getInt(c.getColumnIndex("quantity"))));
            }
            while (c.moveToNext());
        }
        query = String.format("DELETE FROM cart where id='%s';", order.getId());
        db.execSQL(query);
        final String[] id = new String[1];
        cloud.collection("cart").whereEqualTo("id", order.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                id[0] = queryDocumentSnapshots.getDocuments().get(0).getId();
                cloud.collection("cart").document(id[0]).delete();
            }
        });

        return result.get(0);
    }

    public void clearCart() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("DELETE FROM cart");
        db.execSQL(query);
    }
}
