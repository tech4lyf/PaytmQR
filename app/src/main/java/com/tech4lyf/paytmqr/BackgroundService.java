package com.tech4lyf.paytmqr;

import android.app.Service;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    String OID;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
//                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
                JSONParser jsonParser=new JSONParser();
                JSONObject jsonObject=jsonParser.getJSONFromUrl("https://clients.tech4lyf.com/quicup/txnstatus.php?orderid="+OID);


                try {
                    String test=jsonObject.getString("body");
                    Log.e("Response",test);
//                    Toast.makeText(context, test, Toast.LENGTH_SHORT).show();

                    try {
                       JSONObject jsonObject1 = new JSONObject(test);
//                       Log.e("JSONRESP",jsonObject1.getString("resultInfo"));

                       JSONObject jsonObject2=new JSONObject(jsonObject1.getString("resultInfo"));
                        Log.e("JSONRESPONSE",jsonObject2.getString("resultStatus"));
                        String resp=jsonObject2.getString("resultStatus");

                        Log.e("TXNSTATUS",resp);
                        if(resp.equals("TXN_SUCCESS"))
                        {
//                            
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        }

                        else if(resp.equals("TXN_FAILURE"))
                        {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException err){
                    Log.d("Error", err.toString());
            }

                    } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(runnable, 2000);
            }
        };

        handler.postDelayed(runnable, 10000);


    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            OID = intent.getStringExtra("OID");
        }
        Log.e("ServiceStatus","Service started by user");
        return START_STICKY;
    }
}