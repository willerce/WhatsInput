package com.buscode.whatsinput.common;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;

public class ServicePieceManager implements ServicePiece, ServicePieceContainer {

	@Override
	public void onCreate() {
		for (ServicePiece piece : mPieces) {
			piece.onCreate();
		}
	}

	@Override
	public void onDestroy() {
		for (ServicePiece piece : mPieces) {
			piece.onDestroy();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		for (ServicePiece piece : mPieces) {
			IBinder binder = piece.onBind(intent);
			
			if (binder != null) {
				return binder;
			}
		}
		return null;
	}

	private Service mService;
	
	@Override
	public void setService(Service service) {
		mService = service;
		for (ServicePiece piece : mPieces) {
			piece.setService(service);
		}
	}

	private ArrayList<ServicePiece> mPieces = new ArrayList<ServicePiece>();
	@Override
	public void addPiece(ServicePiece piece) {
		if (mService != null) {
			piece.setService(mService);
		}
		mPieces.add(piece);
	}
	@Override
	public void removePiece(ServicePiece piece) {
		mPieces.remove(piece);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		for (ServicePiece piece : mPieces) {
			if (piece.isTarget(intent)) {
				return piece.onStartCommand(intent, flags, startId);
			}
		}
		return Service.START_NOT_STICKY;
	}

	@Override
	public boolean isTarget(Intent intent) {
		return false;
	}
}
