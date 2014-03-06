package com.buscode.whatsinput;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.buscode.whatsinput.server.BackServiceBinder;
import com.buscode.whatsinput.server.BackServiceListener;
import com.buscode.whatsinput.server.ExHttpConfig;
import org.apache.log4j.Logger;

/**
 * User: fanxu
 * Date: 12-10-31
 */
public class MainActivity extends Activity {

    //logger
    private Logger logger = Logger.getLogger("MainActivity");

    public Logger getLogger() {
        return logger;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViews();
        initStates();
    }

    private void bindService() {
        Intent intent = new Intent(this, BackService.class);
        bindService(intent, mServiceConn, BIND_AUTO_CREATE);
    }

    private void unbindService() {
        unbindService(mServiceConn);
    }
    private BackServiceBinder mBinder = null;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            logger.debug("onServiceConnected: ");
            mBinder = BackServiceBinder.Stub.asInterface(iBinder);
            registerHttpServiceListener();

            tryToStartHttpService();
            updateState();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            logger.debug("onServiceDisconnected: ");
            mBinder = null;
        }
    };

    private void tryToStartHttpService() {
        ExHttpConfig config = ExHttpConfig.getInstance();
        if (config.state == ExHttpConfig.STATE_STOPPED) {
            startHttpService();
        }
    }

    private void registerHttpServiceListener() {
        if (mBinder == null) {
            return;
        }
        try {
            mBinder.registerListener(mHttpServiceListener);
            logger.debug("registerHttpServiceListener: succeed.");
        } catch (RemoteException e) {
            e.printStackTrace();
            logger.error("onServiceConnected: Bind failed....", e);
        }
    }
    private void unRegisterHttpServiceListener() {
        if (mBinder == null) {
            return;
        }
        try {
            mBinder.registerListener(null);
            logger.debug("unRegisterHttpServiceListener: ");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private BackServiceListener.Stub mHttpServiceListener = new BackServiceListener.Stub() {
        @Override
        public void onStart() throws RemoteException {
            logger.debug("HttpServiceListener.onStart: ");
            updateState();
        }

        @Override
        public void onStop() throws RemoteException {
            logger.debug("HttpServiceListener.onStop: ");
            updateState();
        }

        @Override
        public void onMessage(String msg) throws RemoteException {
        }

        @Override
        public void onOpen() throws RemoteException {

        }
    };

    private void findViews() {
        vfContent = (ViewFlipper) findViewById(R.id.vfContent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService();
        updateState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unRegisterHttpServiceListener();
        unbindService();
    }

    private ViewFlipper vfContent;

    public static final int CI_LOADING = 0;
    public static final int CI_START   = 1;
    public static final int CI_RUNNING = 2;

    public void setDisplayContent(int index) {
        if (index != vfContent.getDisplayedChild()) {
            vfContent.setDisplayedChild(index);
        }
    }

    private Handler mHandler = new Handler();

    public Handler getHandler() {
        return mHandler;
    }

    public boolean startHttpService() {
        if (mBinder == null) {
            return false;
        }
        try {
            mBinder.startBackService();
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean stopHttpService() {
        if (mBinder == null) {
            return false;
        }
        try {
            mBinder.stopBackService();
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ConnectionState mStartState, mLoadingState, mRunningState;

    private void initStates() {
        mStartState = new StartState();
        mLoadingState = new LoadingState();
        mRunningState = new RunningState();

        mStartState.init(this);
        mLoadingState.init(this);
        mRunningState.init(this);
    }
    public void showLoadingState() {
        show(mLoadingState);
    }
    public void showStartState() {
        show(mStartState);
    }
    public void showRunningState() {
       show(mRunningState);
    }
    private ConnectionState mLastState;
    private void show(ConnectionState state) {
        if (state == mLastState) {
            return;
        }
        mLastState = state;
        mLastState.show();
        logger.debug("show: "  + state.toString());
    }
    public synchronized void updateState() {
        int state = ExHttpConfig.getInstance().state;
        if (state == ExHttpConfig.STATE_LISTENING) {
            showRunningState();
        } else {
            showStartState();
        }
    }
}

interface ConnectionState {
    public void init(MainActivity activity);
    public void show();
}

abstract class AbstractConnectionState implements ConnectionState{
    MainActivity mActivity;
    @Override
    public void init(MainActivity activity) {
        mActivity = activity;
    }

    View findViewById( int id ) {
        return mActivity.findViewById(id);
    }
}

class StartState extends AbstractConnectionState implements View.OnClickListener {

    private Button btnStart;

    @Override
    public void init(MainActivity activity) {
        super.init(activity);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mActivity.showLoadingState();
        //OnStart clicked...
        if (!mActivity.startHttpService()) {
            mActivity.getLogger().debug("Failed to start http service...");
            mActivity.showStartState();
            return;
        }
    }

    @Override
    public void show() {
        mActivity.setDisplayContent(MainActivity.CI_START);
    }
}

class LoadingState extends AbstractConnectionState  {

    private Runnable mCheckLater = new Runnable() {
        @Override
        public void run() {
            mActivity.updateState();
        }
    };
    @Override
    public void show() {
        mActivity.setDisplayContent(MainActivity.CI_LOADING);
        mActivity.getHandler().postDelayed(mCheckLater, 1000 * 3);
    }
}

//Have a Stop Button
class RunningState extends AbstractConnectionState implements View.OnClickListener {
    private Button btnStop;
    private TextView tvAddress;

    @Override
    public void init(MainActivity activity) {
        super.init(activity);

        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);

        tvAddress = (TextView) findViewById(R.id.tvAddress);
    }

    @Override
    public void show() {
        mActivity.setDisplayContent(MainActivity.CI_RUNNING);

        tvAddress.setText(ExHttpConfig.getInstance().getLocalAddress());
    }

    @Override
    public void onClick(View view) {
        mActivity.showLoadingState();
        if (!mActivity.stopHttpService()) {
            mActivity.getLogger().debug("Failed to Stop http Service...");
            mActivity.showRunningState();
            return;
        }
    }
}