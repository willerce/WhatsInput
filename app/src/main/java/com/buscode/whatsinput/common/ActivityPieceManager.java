package com.buscode.whatsinput.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;


public class ActivityPieceManager implements ActivityPiece, ActivityPieceContainer {

	private ArrayList<ActivityPiece> mPieces = new ArrayList<ActivityPiece>();
	@Override
	public void addPiece(ActivityPiece piece) {
		if (mActivity != null) {
			piece.setActivity(mActivity);			
		}
		mPieces.add(piece);
	}
	@Override
	public void removePiece(ActivityPiece piece) {
		mPieces.remove(piece);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		for (ActivityPiece piece : mPieces) {
			piece.onCreate(savedInstanceState);
		}
	}

	@Override
	public void onDestroy() {
		for (ActivityPiece piece : mPieces) {
			piece.onDestroy();
		}
		mPieces.clear();
	}

	@Override
	public void onResume() {
		for (ActivityPiece piece : mPieces) {
			piece.onResume();
		}
	}

	@Override
	public void onPause() {
		for (ActivityPiece piece : mPieces) {
			piece.onPause();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		for (ActivityPiece piece : mPieces) {
			piece.onActivityResult(requestCode, resultCode, data);
		}
	}

	private Activity mActivity;
	@Override
	public void setActivity(Activity activity) {
		mActivity = activity;
		for (ActivityPiece piece : mPieces) {
			piece.setActivity(activity);
		}
	}

}
