package com.recordarchive.android;

import static android.provider.BaseColumns._ID;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class RecordProvider extends ContentProvider {
	private static final int RECORDS = 1;
	private static final int RECORDS_ID = 2;
	
	/** The MIME type of a directory of records */
	private static final String CONTENT_TYPE
	= "vnd.android.cursor.dir/vnd.example." + RecordsDatabase.TABLE_NAME;
	
	/** The MIME type of a single record */
	private static final String CONTENT_ITEM_TYPE
	= "vnd.android.cursor.item/vnd.example." + RecordsDatabase.TABLE_NAME;
	
	private static RecordsDatabase mRecordsDatabase;
	private static UriMatcher mUriMatcher;
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mRecordsDatabase.getWritableDatabase();
		int count;
		switch (mUriMatcher.match(uri)) {
		case RECORDS:
			count = db.delete(RecordsDatabase.TABLE_NAME, selection, selectionArgs);
			break;
		case RECORDS_ID:
			long id = Long.parseLong(uri.getPathSegments().get(1));
			count = db.delete(RecordsDatabase.TABLE_NAME, appendRowId(selection, id),
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case RECORDS:
			return CONTENT_TYPE;
		case RECORDS_ID:
			return CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mRecordsDatabase.getWritableDatabase();
		
		// Validate the requested uri
		if (mUriMatcher.match(uri) != RECORDS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		// Insert into database
		long id = db.insertOrThrow(RecordsDatabase.TABLE_NAME, null, values);
		
		// Notify any watchers of the change
		Uri newUri = ContentUris.withAppendedId(RecordsDatabase.CONTENT_URI, id);
		getContext().getContentResolver().notifyChange(newUri, null);
		return newUri;
	}

	@Override
	public boolean onCreate() {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(RecordsDatabase.AUTHORITY, 
			RecordsDatabase.TABLE_NAME, RECORDS);
		mUriMatcher.addURI(RecordsDatabase.AUTHORITY, 
			RecordsDatabase.TABLE_NAME + "/#", RECORDS_ID);
		mRecordsDatabase = new RecordsDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (mUriMatcher.match(uri) == RECORDS_ID) {
			long id = Long.parseLong(uri.getPathSegments().get(1));
			selection = appendRowId(selection, id);
		}
		
		// Get the database and run the query
		SQLiteDatabase db = mRecordsDatabase.getReadableDatabase();
		Cursor cursor = db.query(RecordsDatabase.TABLE_NAME, projection, selection,
				selectionArgs, null, null, sortOrder);
		
		// Tell the cursor what uri to watch, so it knows when its
		// source data changes
		cursor.setNotificationUri(getContext().getContentResolver(),
				uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mRecordsDatabase.getWritableDatabase();
		int count;
		switch (mUriMatcher.match(uri)) {
		case RECORDS:
			count = db.update(RecordsDatabase.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case RECORDS_ID:
			long id = Long.parseLong(uri.getPathSegments().get(1));
			count = db.update(RecordsDatabase.TABLE_NAME, values, appendRowId(
					selection, id), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	/** Append an id test to a SQL selection expression */
	   private String appendRowId(String selection, long id) {
	      return _ID + "=" + id
	            + (!TextUtils.isEmpty(selection)
	                  ? " AND (" + selection + ')'
	                  : "");
	   }

}
