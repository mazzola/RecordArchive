package com.recordarchive.android;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.recordarchive.android.DatabaseTask.Operation;

import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class DiscogClient extends AsyncTask<ApiCall, Void, String[]> {
	final static String TAG = "DiscogClient";
	final static String USER_AGENT = "Vinylogue/0.1";
	// Errors Codes
	private static final String[] CLIENT_EXCEPTION = {"1"};
	private static final String[] IO_EXCEPTION = {"2"};
	private static final String[] HTTP_ERROR = {"3"};
	
	// Discogs API url
	private static String base_url = "api.discogs.com";
	private static String search_path = "database/search";
	// Type of resource
	public static final int SEARCH = 0;
	public static final int IMAGE = 1;
	// Optional parameters for search
	public static final String TYPE = "type";
	public static final String RELEASE = "release";
	public static final String ARTIST = "artist";
	public static final String TITLE = "release_title";
	
	// Album details
	private String title;
	private String artist;

	static HttpClient mHttpClient = AndroidHttpClient.newInstance(USER_AGENT);
	
	@Override
	protected String[] doInBackground(ApiCall... call) {
		switch (call[0].action) {
		case SEARCH:
			// Build uri to search for album art
			Uri.Builder uri = new Uri.Builder();
			uri.scheme("http");
			uri.authority(base_url);
			uri.appendEncodedPath(search_path);
			// Set album details for later use
			title = call[0].getTitle();
			artist = call[0].getArtist();
			for (String[] params : call[0].params) {
				Log.d(TAG, params[0] + " " + params[1]);
				uri.appendQueryParameter(params[0], params[1]);			
			}
			Log.d(TAG, "Sending search request to: " + uri.build().toString());
			try {
				HttpGet request = new HttpGet(new URI(uri.build().toString()));
				HttpResponse response = mHttpClient.execute(request);
				Log.d(TAG, "HTTP Status: " + response.getStatusLine());	
				// Check if we get an OK response from the server
				if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
					return HTTP_ERROR;
				}
				String content = EntityUtils.toString(response.getEntity());
				Log.d(TAG, "RESPONSE: \n" + content);
				return parseResponse(content);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return CLIENT_EXCEPTION;
			} catch (IOException e) {
				e.printStackTrace();
				return IO_EXCEPTION;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			break;
		case IMAGE:
			// Do some stuff to get an image
			break;
		default:
			break;
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String result[]) {
		if (CLIENT_EXCEPTION.equals(result)) {
			// TODO: show a message to the user
		}
		else if (IO_EXCEPTION.equals(result)) {
			// TODO: show a message to the user			
		}
		else if (result.length < 4) {
			// TODO: show a message to the user
		}
		else {
			Log.d(TAG, "RESULT: " + result[0] + " " + result[1] + " " + result[3]);
			new DatabaseTask(Operation.CREATE).execute(result);
		}
	}
	
	/**
	 * Gets the album information from a json response
	 * @param response a json response from the discogs api
	 * @return String array [Title, Artist, Type, ImageURL]
	 */
	private String[] parseResponse(String response) {
		final String[] noResult = new String[]{""};
		try {
			JSONArray jsonResults = new JSONObject(response).getJSONArray("results");
			if (jsonResults.length() == 0) return noResult;
			for (int i = 0; i < jsonResults.length(); i++) {
				JSONObject album = jsonResults.getJSONObject(i);
				if ("Vinyl".equals(album.getJSONArray("format").get(0))) {
					// Album information string in format "[Artist] - [Title]"
					String info = album.getString("title");
					Integer separator = info.indexOf("-");
					return new String[] {
							info.substring(separator + 1).trim(),
							info.substring(0, separator).trim(),
							RecordsDatabase.TYPE_COLLECTION,
							album.getString("thumb")
					};
				}
			}
			// If we don't find the vinyl record, just return no result
			return noResult;
		} catch (JSONException e) {
			e.printStackTrace();
			return noResult;
		}
	}
	
}

