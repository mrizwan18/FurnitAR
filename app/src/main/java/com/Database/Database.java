package com.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.util.Pair;

import com.razi.furnitar.order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "myDB.db";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<order> getCarts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT* FROM cart";
        Cursor cursor = db.rawQuery(selectQuery, null);
        final List<order> result = new ArrayList<>();
        Log.i("Yes", "Haan");
        if (cursor.moveToNext()) {
            Log.i("Yes", cursor.getInt(cursor.getColumnIndex("quantity")) + "");
            do {
                result.add(new order(cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getDouble(cursor.getColumnIndex("price")),
                        cursor.getInt(cursor.getColumnIndex("quantity"))));
            }
            while (cursor.moveToNext());
        }
        return result;
    }

    public void addToCart(order order) {
        SQLiteDatabase db = this.getReadableDatabase();
      /*  String query = String.format("Insert into cart(id, name, price, quantity) VALUES(%s,%s, '%s', '%s')",
                order.getId(),
                order.getName(),
                order.getPrice(),
                order.getQuantity()
        );
        db.execSQL(query);*/
        ContentValues values = new ContentValues();
        values.put("id", order.getId());
        values.put("name", order.getName());
        values.put("price", order.getPrice());
        values.put("quantity", order.getQuantity());
        db.insertWithOnConflict("cart", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Pair<String, Integer> removeFromCart(order order) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlselect = {"id", "quantity"};
        String sqlTable = "[order]";
        qb.setTables(sqlTable);
        String[] s = {order.getId()};
        Cursor c = qb.query(db, sqlselect, "id", s, null, null, null);
        ArrayList<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>();
        if (c.moveToNext()) {
            do {
                result.add(new Pair(c.getString(c.getColumnIndex("id")),
                        c.getInt(c.getColumnIndex("quantity"))));
            }
            while (c.moveToNext());
        }
        query = String.format("DELETE FROM [order] where id='%s';", order.getId());
        db.execSQL(query);
        return result.get(0);
    }

    public void clearCart() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("DELETE FROM cart");
        db.execSQL(query);
        Log.i("OK", "OK");
    }
}
