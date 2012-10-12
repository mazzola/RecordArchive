package com.recordarchive.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddRecordsFragment extends Fragment {
	/** Mode whether we are adding to the collection or wishlist */
    private String modeType;
	
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// Inflate the layout for this fragment
    	View view = inflater.inflate(R.layout.add_records, container, false);
    	// Set add mode type to default: collection
    	modeType = RecordsDatabase.TYPE_COLLECTION;
    	//Initialize elements
        final Button addRecordButton = (Button) view.findViewById(R.id.button_add);
        final EditText artistText = (EditText) view.findViewById(R.id.editText_artist);
        final EditText albumText = (EditText) view.findViewById(R.id.editText_album);
        
        // Define add button listener
        addRecordButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String artist = artistText.getText().toString();
				String album = albumText.getText().toString();
				
				String[][] params = new String[][] {
						{DiscogClient.TYPE, DiscogClient.RELEASE},
						{DiscogClient.ARTIST, artist},
						{DiscogClient.TITLE, album},
				};
				ApiCall apiCall = new ApiCall(DiscogClient.SEARCH, params);
				DiscogClient search = new DiscogClient();
				search.execute(apiCall);
				/*
				DatabaseTask task = new DatabaseTask(Operation.CREATE, 
						getActivity().getContentResolver());
				task.execute(albumText.getText().toString(), 
						artistText.getText().toString(), modeType);
				try {
					if (task.get() == 1) {
						Toast.makeText(v.getContext(),
							albumText.getText().toString() + " added",
							Toast.LENGTH_SHORT).show();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
		});
    	return view;
    }
    
}