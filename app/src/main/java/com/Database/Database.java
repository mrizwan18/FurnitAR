package com.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Pair;

import com.razi.furnitar.order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "furnitar.db";
    private static final int DB_VERSION = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public List<order> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlselect = {"id", "price", "quantity", "name"};
        String sqlTable = "order";
        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlselect, null, null, null, null, null);
        final List<order> result = new ArrayList<>();
        if (c.moveToNext()) {
            do {
                result.add(new order(c.getString(c.getColumnIndex("id")),
                        c.getString(c.getColumnIndex("name")),
                        c.getDouble(c.getColumnIndex("price")),
                        c.getInt(c.getColumnIndex("quantity"))));
            }
            while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Insert into order(id, price, quantity, name) VALUES('%s','%s', '%s', '%s')",
                order.getId(),
                order.getPrice(),
                order.getQuantity(),
                order.getName());
        db.execSQL(query);
    }

    public Pair<String,Integer> removeFromCart(order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlselect = {"id", "quantity"};
        String sqlTable = "order";
        qb.setTables(sqlTable);
        String[] s = {order.getId()};
        Cursor c = qb.query(db, sqlselect, "id", s , null, null, null);
        ArrayList<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>();
        if (c.moveToNext()) {
            do {
                result.add(new Pair(c.getString(c.getColumnIndex("id")),
                        c.getInt(c.getColumnIndex("quantity"))));
            }
            while (c.moveToNext());
        }
        query = String.format("DELETE FROM order where id='%s';", order.getId());
        db.execSQL(query);
        return result.get(0);
    }

    public void clearCart() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM order");
        db.execSQL(query);
    }
}
