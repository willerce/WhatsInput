package com.buscode.whatsinput;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.inputmethodservice.InputMethodService;
import android.os.*;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.buscode.whatsinput.beans.*;
import com.buscode.whatsinput.common.Net;
import com.buscode.whatsinput.server.BackServiceBinder;
import com.buscode.whatsinput.server.BackServiceListener;
import com.buscode.whatsinput.server.ExHttpConfig;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * User: fanxu
 * Date: 12-10-26
 */
public class WifiInputMethod extends InputMethodService {

    //logger
    private Logger logger = Logger.getLogger("WifiInputMethod");

    private boolean mIsEditing = false;
    private TextView tvAddress;


    public boolean isEditing() {
        return mIsEditing;
    }
    private MsgParser mMsgParser = new MsgParser(logger);

    private BackServiceListener.Stub mBackServiceListener = new BackServiceListener.Stub() {
        @Override
        public void onStart() throws RemoteException {
            logger.debug("HttpServiceListener.onStart: ");
        }

        @Override
        public void onStop() throws RemoteException {
            logger.debug("HttpServiceListener.onStop: ");
        }

        @Override
        public void onMessage(String msg) throws RemoteException {
//            logger.debug("onMessage: " + msg);
            mMsgParser.onMessage(WifiInputMethod.this, msg);
        }

        @Override
        public void onOpen() throws RemoteException {
            if (isEditing()) {
                InputStart msg = new InputStart();
                msg.text = getText();

                sendMessage(msg.toJson());
            }
        }
    };

    public void sendMessage(String msg){
        if (mBinder == null) {
            return;
        }
        try {
            mBinder.sendMessage(msg);
        } catch (Exception e) {

        }
    }

    private BackServiceBinder mBinder = null;
    private void registerBackServiceListener() {
        if (mBinder == null) {
            return;
        }
        try {
            mBinder.registerListener(mBackServiceListener);
            logger.debug("registerBackServiceListener: succeed.");
        } catch (RemoteException e) {
            e.printStackTrace();
            logger.error("onServiceConnected: Bind failed....", e);
        }
    }
    private void unRegisterBackServiceListener() {
        if (mBinder == null) {
            return;
        }
        try {
            mBinder.registerListener(null);
            logger.debug("unRegisterBackServiceListener: ");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            logger.debug("onServiceConnected: ");
            mBinder = BackServiceBinder.Stub.asInterface(iBinder);
            registerBackServiceListener();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            logger.debug("onServiceDisconnected: ");
            mBinder = null;
        }
    };
    private void bindBackService() {
        Intent intent = new Intent(this, BackService.class);
        bindService(intent, mServiceConn, BIND_AUTO_CREATE);
    }
    private void unbindService() {
        unRegisterBackServiceListener();
        unbindService(mServiceConn);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        bindBackService();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindService();
    }

    private View mContent;

    private View ivRestart;

    @Override
    public View onCreateInputView() {

        mContent = LayoutInflater.from(this).inflate(R.layout.wifi_input_method_view, null);
        tvAddress = (TextView) mContent.findViewById(R.id.tvIP);
        tvAddress.setText(ExHttpConfig.getInstance().getLocalAddress());

        ivRestart = mContent.findViewById(R.id.ivRestart);
        ivRestart.setOnClickListener(mOnRestartClicked);

        return mContent;
    }

    private Handler mHandler = new Handler();

    void postRunnable(Runnable r) {
        mHandler.post(r);
    }
    private View.OnClickListener mOnRestartClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	if (Build.VERSION.SDK_INT >= 11) {
        		showPopupMenu(view);
        	} else {
        		showAlertDialogList();
        	}
        }
    };
    
    private void showAlertDialogList() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setItems(R.array.popup_list, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) { //restart server
					restartBackService();
				} else if (which == 1) { //change input
					changeInputMethod();
				}
			}
		});
    	builder.setPositiveButton(R.string.cancel, null);
    	AlertDialog ad = builder.create();
    	ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
        ad.setCanceledOnTouchOutside(false);    
        ad.show();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showPopupMenu(View view) {
		PopupMenu pm = new PopupMenu(WifiInputMethod.this, view);
    	pm.getMenuInflater().inflate(R.menu.popup, pm.getMenu());
    	pm.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.restart:
				{
					restartBackService();
				}
					break;
				case R.id.change: {
					changeInputMethod();
				}
				default:
					break;
				}
				return false;
			}

			
		});
    	pm.show();
	}
    private void changeInputMethod() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showInputMethodPicker();
	}
    private void restartBackService() {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (mBinder != null) {
                        mBinder.stopBackService();

                        Thread.sleep(1000);

                        mBinder.startBackService();

                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WifiInputMethod.this, R.string.restart_finished, Toast.LENGTH_LONG).show();
                                tvAddress.setText(ExHttpConfig.getInstance().getLocalAddress());
                            }
                        });
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    logger.debug(e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        mIsEditing = true;
        acquireLock();

        if (Net.WiFi.isWifiConnected(this)) {
            String address = ExHttpConfig.getInstance().getLocalAddress();
            if (TextUtils.isEmpty(address)) {
                restartBackService();
                tvAddress.setText(R.string.restarting);
            } else {
                tvAddress.setText(address);
            }
        } else {
            tvAddress.setText(R.string.only_work_in_wifi_);
        }

        String text = getText();
        makeToast("onStartInputView: text--" + text);

        InputStart msg = new InputStart();
        msg.text = text;
        sendMessage(msg.toJson());
    }

    private PowerManager.WakeLock mWakeLock;
    public static final String TAG_LOCK = "InputMethod";

    synchronized void acquireLock() {
        if (mWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG_LOCK);
        mWakeLock.acquire();
    }
    synchronized void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
    public String getText() {
        String text = "";
        try {
            InputConnection conn = getCurrentInputConnection();
            ExtractedTextRequest req = new ExtractedTextRequest();
            req.hintMaxChars = 1000000;
            req.hintMaxLines = 10000;
            req.flags = 0;
            req.token = 1;
            text = conn.getExtractedText(req, 0).text.toString();
        } catch (Throwable t) {
        }
        return text;
    }
    public boolean setText(String text) {
        // FIXME: need feedback if the input was lost
        InputConnection conn = getCurrentInputConnection();
        if (conn == null) {
//      Debug.d("connection closed");
            return false;
        }

        conn.beginBatchEdit();
        // FIXME: hack
        conn.deleteSurroundingText(100000, 100000);
        conn.commitText(text, text.length());
        conn.endBatchEdit();
        return true;
    }
    ExtractedTextRequest req = new ExtractedTextRequest();
    {
        req.hintMaxChars = 100000;
        req.hintMaxLines = 10000;
    }
    void makeToast(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        mIsEditing = false;
        releaseWakeLock();

        makeToast("onFinishInputView" + finishingInput);
        sendMessage(new InputFinish().toJson());
    }
}
class MsgParser {

    public MsgParser(Logger logger) {
        this.logger = logger;
    }
    //logger
    private Logger logger;

    public void onMessage(WifiInputMethod service, String msg) {

        if (service == null || TextUtils.isEmpty(msg)) {
            return;
        }
//        logger.debug("onMessage: " + msg);//
        try {
            String type = getType(msg);
//            logger.debug("New Msg: " + type);
            Jsoner jsoner = Jsoner.getInstance();
            if (InputEdit.TYPE.equals(type)) {
                InputEdit ie = jsoner.fromJson(msg, InputEdit.class);
                ie.onMessage(service);
            } else if (InputUpdate.TYPE.equals(type)) {
                InputUpdate iu = jsoner.fromJson(msg, InputUpdate.class);
                iu.onMessage(service);
            } else if (InputKey.TYPE.equals(type)) {
                InputKey ik = jsoner.fromJson(msg, InputKey.class);
                ik.onMessage(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
    }

    public String getType(String msg) throws Exception{
        JSONObject jo = new JSONObject(msg);
        return jo.optString("type", "");
    }
}
