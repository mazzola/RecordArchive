package com.recordarchive.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class Main extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Initialize buttons
		final Button addButton = (Button) findViewById(R.id.button_add);
		final Button collectionButton = (Button) findViewById(R.id.button_collection);
		
		final FragmentManager manager = getSupportFragmentManager();
		
		// Give database access to contentresolver and context
		DatabaseTask.setContentResolver(getContentResolver());
		DatabaseTask.setContext(this);
		
		if (savedInstanceState == null) {
			manager.beginTransaction().add(R.id.actionContent, new AddRecordsFragment()).commit();
		}
		
		collectionButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.replace(R.id.actionContent, new ViewCollectionFragment());
				transaction.commit();
			}
		});
		
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				transaction.replace(R.id.actionContent, new AddRecordsFragment());
				transaction.commit();
			}
		});
		
	}
	
	
}
