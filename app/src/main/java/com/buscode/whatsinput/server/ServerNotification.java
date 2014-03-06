package com.sand.airinput.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.buscode.whatsinput.MainActivity;
import com.buscode.whatsinput.server.ExHttpConfig;
import com.buscode.whatsinput.R;

public class ServerNotification {


    public static final int ID_SPS_HTTP = 100;

    public static synchronized void showServerNotification(Context context) {

        ExHttpConfig config = ExHttpConfig.getInstance();
        String contentText = config.getLocalAddress();

        Notification notification = makeSPSNotification(context, contentText, true);
        notify(context, notification);
    }

    public static synchronized void cancelAll(Context context) {
        NotificationManager nm = getNotificationManager(context);
        nm.cancelAll();
    }

    private static void notify(Context context, Notification notification) {
        NotificationManager nm = getNotificationManager(context);
        nm.notify(ID_SPS_HTTP, notification);
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static Notification makeSPSNotification(Context context, String contentText, boolean notice) {
        Builder builder = new Builder(context);

        builder.setIcon(R.drawable.icon);
        builder.setTickerText("Sps ScreenShot");
        builder.setWhen(System.currentTimeMillis());
        builder.addFlags(Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT);

        //Latest Event Info
        String contentTitle = "Sps ScreenShot";
        PendingIntent contentIntent = getNotificationPendingIntent(context);

        builder.setLatestEventInfo(contentTitle, contentText, contentIntent);
        builder.setDefaults(notice ? Notification.DEFAULT_SOUND : 0);

        return builder.build();
    }
    private static PendingIntent getNotificationPendingIntent(Context context) {
        return PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
    }
    public static class Builder {

        private Context mContext;

        private Notification mNotification;

        public Builder(Context context) {

            mContext = context;
            mNotification = new Notification();
        }

        public Builder setIcon(int icon) {

            mNotification.icon = icon;
            return this;
        }
        public Builder setTickerText(String text) {
            mNotification.tickerText = text;
            return this;
        }

        public Builder setWhen(long when) {
            mNotification.when = when;
            return this;
        }

        public Builder setContentIntent(PendingIntent contentIntent) {
            mNotification.contentIntent = contentIntent;
            return this;
        }

        public Builder addFlags(int flags) {
            mNotification.flags |= flags;
            return this;
        }

        /**
         *
         * Sound, VIBRATE
         * Notification.DEFAULT_SOUND,
         * Notification.DEFAULT_VIBRATE
         *
         * @param defaults
         * @return
         */
        public Builder setDefaults(int defaults) {

            mNotification.defaults = defaults;
            return this;
        }

        public Builder setLatestEventInfo(String contentTitle, String contentText, PendingIntent contentIntent) {

            mNotification.setLatestEventInfo(mContext, contentTitle, contentText, contentIntent);
            return this;
        }

        public Notification build() {
            return mNotification;
        }
    }
}
