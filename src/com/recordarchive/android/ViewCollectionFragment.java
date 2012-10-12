package com.recordarchive.android;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;
import com.recordarchive.android.DatabaseTask.Operation;

public class ViewCollectionFragment extends Fragment {
	private final String TAG = "ViewCollection";
	private ImageLoader mImageLoader;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mImageLoader = new ImageLoader(getActivity());
		GridView gridView = (GridView) inflater.inflate(R.layout.record_collection, container, false);
		CursorLoader mCLoader = new CursorLoader(getActivity(), RecordsDatabase.CONTENT_URI,
				new String[] {"*"}, null, null, null);
		gridView.setAdapter(new CoverArtCursorAdapter(getActivity(), mCLoader.loadInBackground()));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO: something cool
			}
		});
		return gridView;
	}
	
	public class CoverArtCursorAdapter extends CursorAdapter {
		LayoutInflater mInflater;
		
		public CoverArtCursorAdapter(Context context, Cursor c) {
			super(context, c);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ImageView coverArtView = (ImageView) view.findViewById(R.id.coverArt);
			String albumName = cursor.getString(cursor.getColumnIndex(RecordsDatabase.ALBUM));
			coverArtView.setContentDescription(albumName);
			String imgUrl = cursor.getString(cursor.getColumnIndex(RecordsDatabase.IMG_URL));
			Log.d(TAG, "Image URL: " + imgUrl);
			mImageLoader.DisplayImage(imgUrl, coverArtView);
			((TextView) view.findViewById(R.id.album_name)).setText(albumName);
			String artistName = cursor.getString(cursor.getColumnIndex(RecordsDatabase.ARTIST));
			((TextView) view.findViewById(R.id.artist_name)).setText(artistName);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return mInflater.inflate(R.layout.collection_item_album, parent, false);
		}
		
	}
	/*
	public class CoverArtAdapter extends BaseAdapter {
		private LayoutInflater mInflator;
		
		public CoverArtAdapter(Context c) {
			mInflator = LayoutInflater.from(c);
		}
		public int getCount() {
			DatabaseTask task = new DatabaseTask(Operation.COUNT);
			task.execute();
			try {
				return task.get();
			} catch (InterruptedException e) {
				Log.e(TAG,  "COUNT was interrupted! Returning count of 1\n" + e);
				return 1;
			} catch (ExecutionException e) {
				Log.e(TAG,  "COUNT was interrupted! Returning count of 1\n" + e);
				return 1;
			}
		}

		public Object getItem(int position) {
			// TODO something better
			return position;
		}

		public long getItemId(int position) {
			// TODO look into better implementation
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;
			
			if (convertView == null) {
				convertView = mInflator.inflate(R.layout.collection_item_album, null);
				mHolder = new ViewHolder();
				mHolder.coverArt = (ImageView) convertView.findViewById(R.id.coverArt);
				mHolder.artistName = (TextView) convertView.findViewById(R.id.artist_name);
				mHolder.albumName = (TextView) convertView.findViewById(R.id.album_name);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			
//			mHolder.coverArt.setImageResource(R.drawable.record);
//			mHolder.albumName.setText("fdah");
//			mHolder.artistName.setText("fda");
			return convertView;
		}
		
	}
	*/
	
	static class ViewHolder {
		ImageView coverArt;
		TextView albumName;
		TextView artistName;
	}
}
