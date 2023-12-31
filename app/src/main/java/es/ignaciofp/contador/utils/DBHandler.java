package es.ignaciofp.contador.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import es.ignaciofp.contador.models.User;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inflationgame.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "users";
    public static final String ID_COLUMN = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PASSWORD_COLUMN = "password";
    public static final String LAST_SAVE_DATE_COLUMN = "lastSaveDate";
    public static final String COINS_COLUMN = "coins";
    private static final String CLICK_VALUE_COLUMN = "clickValue";
    private static final String AUTO_CLICK_VALUE_COLUMN = "autoClickValue";
    private static final String BASIC_PRICE_COLUMN = "basicPrice";
    private static final String MEGA_PRICE_COLUMN = "megaPrice";
    private static final String AUTO_PRICE_COLUMN = "autoPrice";
    private static final String MEGA_AUTO_PRICE_COLUMN = "megaAutoPrice";
    private static final String HAS_MAX_VALUE_COLUMN = "hasMaxValue";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME_COLUMN + " TEXT, " + PASSWORD_COLUMN + " TEXT, " + LAST_SAVE_DATE_COLUMN + " TEXT, " + COINS_COLUMN + " TEXT, " + CLICK_VALUE_COLUMN + " TEXT, " + AUTO_CLICK_VALUE_COLUMN + " TEXT, " + BASIC_PRICE_COLUMN + " TEXT, " + MEGA_PRICE_COLUMN + " TEXT, " + AUTO_PRICE_COLUMN + " TEXT, " + MEGA_AUTO_PRICE_COLUMN + " TEXT," + HAS_MAX_VALUE_COLUMN + " TEXT" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addUser(User user) {
        user.updateUserLastUpdate();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME_COLUMN, user.getName());
        values.put(PASSWORD_COLUMN, user.getPassword());
        values.put(LAST_SAVE_DATE_COLUMN, user.getLastSaveDate());
        values.put(COINS_COLUMN, user.getCoins().toString());
        values.put(CLICK_VALUE_COLUMN, user.getClickValue().toString());
        values.put(AUTO_CLICK_VALUE_COLUMN, user.getAutoClickValue().toString());
        values.put(BASIC_PRICE_COLUMN, user.getBasicPrice().toString());
        values.put(MEGA_PRICE_COLUMN, user.getMegaPrice().toString());
        values.put(AUTO_PRICE_COLUMN, user.getAutoPrice().toString());
        values.put(MEGA_AUTO_PRICE_COLUMN, user.getMegaAutoPrice().toString());
        values.put(HAS_MAX_VALUE_COLUMN, user.getHasMaxValue().toString());

        long id = db.insert(TABLE_NAME, null, values);
        if (id == -1)
            return false; // If there has been an error then doesn't set the user id and return false
        user.setId(id);
        db.close();
        return true;
    }

    public ArrayList<User> readUsers(String filter) {
        filter = filter == null ? "1=1" : filter;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + filter;

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<User> users = new ArrayList<>();

        // Setting cursor to first position
        if (cursor.moveToFirst()) {
            do {
                // Adding users to the list
                users.add(new User(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), new CustomBigInteger(cursor.getString(4)), new CustomBigInteger(cursor.getString(5)), new CustomBigInteger(cursor.getString(6)), new CustomBigInteger(cursor.getString(7)), new CustomBigInteger(cursor.getString(8)), new CustomBigInteger(cursor.getString(9)), new CustomBigInteger(cursor.getString(10)), Boolean.parseBoolean(cursor.getString(11))));
            } while (cursor.moveToNext()); // Setting cursor to next position
        }
        cursor.close();
        db.close();
        return users;
    }

    public boolean updateUser(User user) {
        user.updateUserLastUpdate();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME_COLUMN, user.getName());
        values.put(LAST_SAVE_DATE_COLUMN, user.getLastSaveDate());
        values.put(COINS_COLUMN, user.getCoins().toString());
        values.put(CLICK_VALUE_COLUMN, user.getClickValue().toString());
        values.put(AUTO_CLICK_VALUE_COLUMN, user.getAutoClickValue().toString());
        values.put(BASIC_PRICE_COLUMN, user.getBasicPrice().toString());
        values.put(MEGA_PRICE_COLUMN, user.getMegaPrice().toString());
        values.put(AUTO_PRICE_COLUMN, user.getAutoPrice().toString());
        values.put(MEGA_AUTO_PRICE_COLUMN, user.getMegaAutoPrice().toString());
        values.put(HAS_MAX_VALUE_COLUMN, user.getHasMaxValue().toString());

        int result = db.update(TABLE_NAME, values, ID_COLUMN + " = " + user.getId(), null);

        db.close();
        return result > 0;
    }

}
