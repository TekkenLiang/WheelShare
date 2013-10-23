package com.example.wheelshare;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import static com.example.wheelshare.Cache.SENDER_ID;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService
{

    static int REGULAR_NOTIFICATION = 0;
    static int INVITE_NOTIFICATION = 1;
    static int REPLY_NOTIFICATION = 2;

    public GCMIntentService()
    {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId)
    {
    }

    @Override
    protected void onUnregistered(Context context, String registrationId)
    {
    }

    @Override
    protected void onMessage(Context context, Intent intent)
    {
        int t;
        try
        {
            t = Integer.parseInt(intent.getStringExtra("type"));
        } catch (Exception e)
        {
            t = 0;
        }
        generateNotification(context, intent, t);

    }

    @Override
    protected void onDeletedMessages(Context context, int total)
    {
    }

    @Override
    public void onError(Context context, String errorId)
    {
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId)
    {
        return super.onRecoverableError(context, errorId); //To change body of generated methods, choose Tools | Templates.
    }

    private static void generateNotification(Context context, Intent intent, int type)
    {
        String message = "message: " + intent.getStringExtra("message");
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);

        Intent notificationIntent;
        if (type == REPLY_NOTIFICATION)
        {
            notificationIntent = new Intent(context, ReceiveMessageActivity.class);
            notificationIntent.putExtra("from", intent.getStringExtra("from_user"));
            notificationIntent.putExtra("message", intent.getStringExtra("message_data"));
        } else if (type == INVITE_NOTIFICATION)
        {
            String id = intent.getStringExtra("id");
            HTTPConnection con = new HTTPConnection("/getridebyid", "rideID");
            con.execute(id);
            String result = null;
            Ride ride = null;
            notificationIntent = new Intent(context, ShowRideActivity.class);
            try
            {
                result = con.get(5, TimeUnit.SECONDS);
                ride = Ride.jsonToRides(result)[0];

                notificationIntent.putExtra("rideID", id);
                notificationIntent.putExtra("date", ride.date);
                notificationIntent.putExtra("time", ride.time);
                notificationIntent.putExtra("from", ride.startCity);
                notificationIntent.putExtra("to", ride.destCity);
                notificationIntent.putExtra("owner", ride.owner);
                notificationIntent.putExtra("id", ride.id);
                notificationIntent.putExtra("isInvited", true);
                notificationIntent.putExtra("price", ((Integer) (ride.price)).toString());
                notificationIntent.putExtra("seats", ((Integer) (ride.seats)).toString());
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (ExecutionException e)
            {
                e.printStackTrace();
            } catch (TimeoutException e)
            {
                e.printStackTrace();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        } else
        {
            notificationIntent = new Intent(context, ManageActivity.class);
        }

        //notificationIntent.putExtra("rideID", rideID);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent2 =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, message, intent2);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}
