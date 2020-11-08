package com.tech4lyf.paytmqr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Bitmap qrbmp;
    ImageView imageView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        imageView=(ImageView)findViewById(R.id.imgQr);
        progressDialog=new ProgressDialog(this);

        new GenQR().execute();

    }

    public class GenQR extends AsyncTask<Void,String,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... voids) {

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrl("https://clients.tech4lyf.com/quicup/?amount=" + 1);

            try {
                String test = jsonObject.getString("body");
                Log.e("Response", test);

                String OID = "";

                OID = StringUtils.substringBetween(test, "&tr=", "&cu=");

                OID = OID.substring(0, 15);
                Log.e("OID", OID);

                Intent i = new Intent(getApplicationContext(), BackgroundService.class);
                i.putExtra("OID", OID);
                startService(i);

                JSONObject obj = new JSONObject(test);

                Log.d("Test", obj.toString());
//            Toast.makeText(this, ""+obj.toString(), Toast.LENGTH_SHORT).show();

                JSONObject obj1 = new JSONObject(obj.toString());

                Log.d("Test", obj1.toString());

                String qrData = obj1.getString("image");
                String qrDataUPI = obj1.getString("qrData");
                Log.e("QR", qrData);
                Log.e("QRData", qrDataUPI);

               qrbmp=StringToBitMap(qrData);


            } catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            imageView.setImageBitmap(qrbmp);
            progressDialog.dismiss();
        }
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}