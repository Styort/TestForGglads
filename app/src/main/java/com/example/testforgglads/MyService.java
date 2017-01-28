package com.example.testforgglads;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.testforgglads.models.Product;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    NotificationManager nm;
    int notifID = 33;
    boolean isNotificActive = false;
    int newPosts = 0, productCount;
    String name, category;
    private String access_token = "591f99547f569b05ba7d8777e2e0824eea16c440292cce1f8dfb3952cc9937ff";

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("MyService","Сервис запущен");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        productCount = intent.getIntExtra("elementCount",0);
        category = intent.getStringExtra("category");
        Log.d("COUNT: ", String.valueOf(productCount));
        Log.d("CATEGORY: ", category);
        checkPost();
        return super.onStartCommand(intent, flags, startId);
   }

    //проверка каждые 15 мин
    private void checkPost() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    sendNotif(productCount, category);
                    TimeUnit.MINUTES.sleep(15);
                    checkPost();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendNotif(final int prodCount, String category) {
        Ion.with(getApplicationContext())
                .load("https://api.producthunt.com/v1/categories/"+category+"/posts")
                .setHeader("Authorization", "Bearer " + access_token)
                .setLogging("Ion", Log.DEBUG)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.w("links", result.toString());
                        if (result != null && result.has("posts")) {
                            JsonArray categArrJSON = result.getAsJsonArray("posts");
                            if (categArrJSON.size()>prodCount){
                                newPosts = categArrJSON.size() - prodCount;
                            }
                            if(newPosts == 1){
                                JsonObject obj = categArrJSON.get(categArrJSON.size()-1).getAsJsonObject();
                                name = obj.get("name").toString().replace("\"", "");
                            }
                        }
                    }
                });

        //если 1 новый пост, то показывать в уведомлении его название, иначе кол-во новых постов
        if(newPosts>0){
            NotificationCompat.Builder notifBuilder;
            if(newPosts == 1){
                notifBuilder = new
                        NotificationCompat.Builder(this)
                        .setContentTitle(name)
                        .setContentText("Подробнее...")
                        .setSmallIcon(R.drawable.ic_comment_black_24dp);
            }else {
                notifBuilder = new
                        NotificationCompat.Builder(this)
                        .setContentTitle(newPosts + "новых постов")
                        .setContentText("Подробнее...")
                        .setSmallIcon(R.drawable.ic_comment_black_24dp);
            }

            Intent moreInfoNotif = new Intent(this, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(moreInfoNotif);

            PendingIntent pendingInteng = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            notifBuilder.setContentIntent(pendingInteng);

            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(notifID,notifBuilder.build());
            isNotificActive = true;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
