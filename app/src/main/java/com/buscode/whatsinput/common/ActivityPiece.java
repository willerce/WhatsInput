package com.buscode.whatsinput.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public interface ActivityPiece {

	/**
	 * Set this activity
	 * @param activity
	 */
	public void setActivity(Activity activity);
	
	/**
	 * @param savedInstanceState
	 * @see {@link android.app.Activity#onCreate(android.os.Bundle savedInstanceState)}
	 */
	public void onCreate(Bundle savedInstanceState);

	/**
	 * @see {@link android.app.Activity#onDestroy()}
	 */
	public void onDestroy();
	/**
	 * @see {@link android.app.Activity#onResume()}
	 */
	public void onResume();
	/**
	 * @see {@link android.app.Activity#onPause()}
	 */
	public void onPause();
	/**
	 * @see {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)}
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data);
}

interface ActivityPieceContainer {
	/**
	 * Add piece
	 * @param piece
	 */
	public void addPiece(ActivityPiece piece);
	/**
	 * remove piece
	 * @param piece
	 */
	public void removePiece(ActivityPiece piece);
}