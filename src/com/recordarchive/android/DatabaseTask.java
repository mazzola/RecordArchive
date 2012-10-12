package com.recordarchive.android;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DatabaseTask extends AsyncTask<String, Void, Integer> {
	
	private static final String TAG = "DATABASE OP";
	public enum Operation { CREATE, READ, UPDATE, DELETE, COUNT };
	private Operation mOp;
	static private ContentResolver mContentResolver;
	static private Context mContext;
	// An identification string for the album that is being operated on
	private String aid;
	private String album;
	
	/**
	 * Creates a new database task to be executed
	 * @param op The operation to be performed
	 * @param resolver The content resolver for the database<br>
	 * For CREATE: args[Title, Artist, Type, ImageURL]<br>
	 * For DELETE: args[AlbumID]<br>
	 * For COUNT: args[null]
	 */
	public DatabaseTask(Operation op) {
		mOp = op;
	}
	
	public static void setContentResolver(ContentResolver cresolver) {
		mContentResolver = cresolver;
	}
	
	public static void setContext(Context context) {
		mContext = context;
	}
	
	@Override
	protected Integer doInBackground(String... args) {
		// Check if type is valid
		switch (mOp) {
		case CREATE:
			album = args[0];
			aid = album;
			String artist = args[1];
			String type = args[2];
			String url = args[3];
			if (!type.equals(RecordsDatabase.TYPE_COLLECTION) &&
					!type.equals(RecordsDatabase.TYPE_WISHLIST)) {
				Log.e(TAG, "INCORRECT Record type: " + type + " Type must be " 
					+ RecordsDatabase.TYPE_COLLECTION + " or "
					+ RecordsDatabase.TYPE_WISHLIST);
				cancel(false);
			}
			ContentValues values = new ContentValues();
			values.put(RecordsDatabase.ALBUM, album);
			values.put(RecordsDatabase.ARTIST, artist);
			values.put(RecordsDatabase.TYPE, type);
			values.put(RecordsDatabase.IMG_URL, url);
			mContentResolver.insert(RecordsDatabase.CONTENT_URI, values);
			Log.d(TAG, "Record \"" + album + "\" successfully " + mOp + "ed");
			return 1;
		case COUNT:
			Cursor c = mContentResolver.query(RecordsDatabase.CONTENT_URI,
					new String[] {"COUNT(*)"}, null, null, null);
			Log.d("DATABASE", DatabaseUtils.dumpCursorToString(c));
			if (c.moveToFirst()) {
				int count = c.getInt(0);
				c.close();
				return count;
			} else {
				// Query was unsuccessful
				return 0;				
			}
			
		default:
			break;
		}
		return 0;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		switch (result) {
		case 1:
			Toast.makeText(mContext, "\"" + album + "\" was added to your collection.", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		Log.e(TAG, "Could not " + mOp + " album " + (aid == null ? "" : aid));
	}

}
