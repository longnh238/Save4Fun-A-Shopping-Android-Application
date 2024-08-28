package com.example.save4fun.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.save4fun.model.Product;
import com.example.save4fun.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DBProductsHelper extends SQLiteOpenHelper {

    public static final String PRODUCTS_TABLE = "products";
    public static final String LIST_PRODUCT_TABLE = "list_product_relationships";
    public static final String FAVOURITE_TABLE = "favourite";
    public static final String USERS_TABLE = "users";

    public DBProductsHelper(@Nullable Context context) {
        super(context, Constant.DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @SuppressLint("Range")
    public List<Product> getPopularProducts(String username) {
        List<Product> popularProducts = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor cursor = db.rawQuery("select * from products", null);

        String selectQuery = "SELECT *" +
                " FROM " + PRODUCTS_TABLE + " LEFT JOIN " + FAVOURITE_TABLE +
                " ON " + PRODUCTS_TABLE + "." + "id" + " = " + FAVOURITE_TABLE + "." + "product_id" +
                " AND " + FAVOURITE_TABLE + "." + "username" + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{username});

        int n = Constant.NUM_OF_POPULAR_PRODUCTS;
        int minRange = 0;
        int maxRange = cursor.getCount();

        List<Integer> randomIndexes = new ArrayList<>();
        if (maxRange > n) {
            randomIndexes = getRandomNumbers(minRange, maxRange, n);
        } else {
            for (int i = 0; i < maxRange; i++) {
                randomIndexes.add(i);
            }
        }

        for (int index : randomIndexes) {
            if (cursor.moveToPosition(index)) {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndex("id")));
                product.setName(cursor.getString(cursor.getColumnIndex("name")));
                product.setCategory(cursor.getString(cursor.getColumnIndex("category")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                product.setImage(cursor.getString(cursor.getColumnIndex("image")));

                boolean isFavourite = cursor.getString(cursor.getColumnIndex("username")) != null;
                product.setIsFavourite(isFavourite);

                popularProducts.add(product);
            }
        }

        return popularProducts;
    }

    @SuppressLint("Range")
    public List<Product> getProductsByCategory(String category, String username) {
        List<Product> popularProducts = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor cursor = db.rawQuery("select * from products where category =?", new String[]{category});

        String selectQuery = "SELECT *" +
                " FROM " + PRODUCTS_TABLE + " LEFT JOIN " + FAVOURITE_TABLE +
                " ON " + PRODUCTS_TABLE + "." + "id" + " = " + FAVOURITE_TABLE + "." + "product_id" +
                " AND " + FAVOURITE_TABLE + "." + "username" + " = ?" +
                " WHERE " + PRODUCTS_TABLE + "." + "category" + " =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{username, category});

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndex("id")));
                product.setName(cursor.getString(cursor.getColumnIndex("name")));
                product.setCategory(cursor.getString(cursor.getColumnIndex("category")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                product.setImage(cursor.getString(cursor.getColumnIndex("image")));

                boolean isFavourite = cursor.getString(cursor.getColumnIndex("username")) != null;
                product.setIsFavourite(isFavourite);

                popularProducts.add(product);
            } while (cursor.moveToNext());
        }

        return popularProducts;
    }

    private List<Integer> getRandomNumbers(int minRange, int maxRange, int n) {
        List<Integer> randomNumbers = new ArrayList<>();

        Random random = new Random(42);
        for (int i = 0; i < n; i++) {
            int randomNumber = random.nextInt((maxRange - minRange) + 1) + minRange;
            if (randomNumbers.contains(randomNumber)) {
                i--;
            } else {
                randomNumbers.add(randomNumber);
            }
        }

        return randomNumbers;
    }

    @SuppressLint("Range")
    public List<Product> getProductsByListId(int listId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT *" +
                " FROM " + PRODUCTS_TABLE + " INNER JOIN " + LIST_PRODUCT_TABLE +
                " ON " + PRODUCTS_TABLE + "." + "id" + " = " + LIST_PRODUCT_TABLE + "." + "product_id" +
                " WHERE " + LIST_PRODUCT_TABLE + "." + "list_id" + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(listId)});

        // Loop through cursor and add product names and quantities to map
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    product.setName(cursor.getString(cursor.getColumnIndex("name")));
                    product.setCategory(cursor.getString(cursor.getColumnIndex("category")));
                    product.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                    product.setImage(cursor.getString(cursor.getColumnIndex("image")));
                    product.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));

                    products.add(product);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        // Close the database connection
        db.close();

        // Return the map of products and quantities
        return products;
    }

    @SuppressLint("Range")
    public boolean addOrUpdateProductByList(int listId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("list_id", listId);
        contentValues.put("product_id", productId);
        contentValues.put("quantity", quantity);

        Cursor cursor = db.rawQuery("SELECT * FROM " + LIST_PRODUCT_TABLE +
                " WHERE " + "list_id" + "=? AND " + "product_id" + "=?", new String[]{String.valueOf(listId), String.valueOf(productId)});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int currentQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));
            contentValues.put("quantity", currentQuantity + quantity);

            int affectedRows = db.update(LIST_PRODUCT_TABLE, contentValues, "list_id" + "=? AND " + "product_id" + "=?", new String[]{String.valueOf(listId), String.valueOf(productId)});
            cursor.close();
            return affectedRows > 0;
        } else {
            long result = db.insert(LIST_PRODUCT_TABLE, null, contentValues);
            cursor.close();
            return result != -1;
        }
    }

    @SuppressLint("Range")
    public List<Product> getFavouriteProductsByUsername(String username) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT *" +
                " FROM " + PRODUCTS_TABLE + " INNER JOIN " + FAVOURITE_TABLE +
                " ON " + PRODUCTS_TABLE + "." + "id" + " = " + FAVOURITE_TABLE + "." + "product_id" +
                " WHERE " + FAVOURITE_TABLE + "." + "username" + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{username});

        // Loop through cursor and add product names and quantities to map
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    product.setName(cursor.getString(cursor.getColumnIndex("name")));
                    product.setCategory(cursor.getString(cursor.getColumnIndex("category")));
                    product.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                    product.setImage(cursor.getString(cursor.getColumnIndex("image")));

                    products.add(product);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        // Close the database connection
        db.close();

        // Return the map of products and quantities
        return products;
    }

    @SuppressLint("Range")
    public void addOrRemoveFavouriteProduct(String username, int productId, boolean isAdded) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (isAdded) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("username", username);
            contentValues.put("product_id", productId);

            db.insert(FAVOURITE_TABLE, null, contentValues);
        } else { // To remove
            db.delete(FAVOURITE_TABLE, "username=? and product_id=?", new String[]{username, String.valueOf(productId)});
        }
    }

    public void deleteProductInList(int listId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LIST_PRODUCT_TABLE, "list_id=? and product_id=?", new String[]{String.valueOf(listId), String.valueOf(productId)});
    }
}
