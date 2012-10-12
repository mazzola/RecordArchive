package com.recordarchive.android;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import static android.provider.BaseColumns._ID;

public class RecordsDatabase extends SQLiteOpenHelper {

	final static String DATABASE_NAME = "records.db";
	final static String TABLE_NAME = "records";
	public static final String ARTIST = "artist";
	public static final String ALBUM = "album";
	public static final String YEAR = "year";
	public static final String IMG_URL = "img_url";
	public static final String COVER_ART_URL = "coverArtUrl";
	public static final String TYPE = "type";
	
	
	public static final String AUTHORITY = "com.recordarchive.android";
	public static final Uri CONTENT_URI = Uri.parse("content://"
	         + AUTHORITY + "/" + TABLE_NAME);
	final static int DATABASE_VERSION = 1;
	
	final static String CREATE_DATABASE = "CREATE TABLE " + TABLE_NAME + " ("
			 + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + ARTIST + " TEXT NOT NULL, "
			 + ALBUM + " TEXT NOT NULL, "
			 + TYPE + " INTEGER NOT NULL, "
			 + IMG_URL + " TEXT"
			 + ");";
	
	public static final String TYPE_COLLECTION = "0";
	public static final String TYPE_WISHLIST = "1";
	
	public RecordsDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	     onCreate(db);
	}
	
	/**
	 * Adds a record to the database
	 * @param album The album name of the record
	 * @param artist The artist name
	 * @param type Either TYPE_COLLECTION or TYPE_WISHLIST
	 * @return true if the record is valid, false if otherwise
	 */
	public static boolean addRecord(ContentResolver r, String album, 
			String artist, String type) {
		// Check if type is valid
		if (type.equals(RecordsDatabase.TYPE_COLLECTION) ||
				type.equals(RecordsDatabase.TYPE_WISHLIST)) {
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(RecordsDatabase.ALBUM, album);
		values.put(RecordsDatabase.ARTIST, artist);
		values.put(RecordsDatabase.TYPE, type);
		r.insert(RecordsDatabase.CONTENT_URI, values);
		return true;
	}
}
