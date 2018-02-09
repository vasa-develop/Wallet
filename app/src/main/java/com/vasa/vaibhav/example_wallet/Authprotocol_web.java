package com.vasa.vaibhav.example_wallet;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.PublicKey;
import java.util.StringTokenizer;

import tech.gusavila92.websocketclient.WebSocketClient;

public class Authprotocol_web extends AppCompatActivity {

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView barcodeValue;

    private String WebSocketURL;
    private int socketindex;

    private String cipher="certificate";
    private PublicKey publicKey;

    private boolean lock = true;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);


        /*try {
            EncoderDecoder.main();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/


        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    //noinspection MissingPermission
                    if (ActivityCompat.checkSelfPermission(Authprotocol_web.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update barcode value to TextView
                            barcodeValue.setText(barcodes.valueAt(0).displayValue);
                            if(lock){

                                //System.out.println(info[1]);
                                Decoder decoder = new Decoder();
                                decoder.hex = barcodes.valueAt(0).displayValue;
                                decoder.execute();
                                //createWebSocketClient(Integer.parseInt(barcodes.valueAt(0).displayValue));
                                lock = false;
                            }


                        }
                    });
                }
            }
        });

    }




    class Decoder extends AsyncTask<String, Void, Boolean> {
        String hex;
        String response = "";

        @Override
        protected Boolean doInBackground(String... strings) {
            //admin_panel/authprotocol/decoder.php
            URL url = null;
            try {
                url = new URL("http://162.144.124.122/~lokasotech/stage_v2/app_panel/admin/admin_panel/authprotocol/decoder.php?hex="+hex);
                //url = new URL("http://162.144.124.122/~lokasotech/stage_v2/app_panel/admin/admin_panel/authprotocol/decoder.php?crypted="+crypted+"&pubkey="+pubkey);
                System.out.println(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                conn.setRequestMethod("GET");
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            }

            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    response = sb.toString();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                br.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            System.out.println(response);
            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            String info [] = new String[2];
            StringTokenizer st = new StringTokenizer(response,"|");
            int count = 0;
            while (st.hasMoreTokens()) {
                info[count] = st.nextToken();
                count += 1;
            }
            WebSocketURL = info[1];
            socketindex = Integer.parseInt(info[0]);
            createWebSocketClient(socketindex,WebSocketURL);

        }
    }


    class Encoder extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    /*public String convertHexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }
        //System.out.println("Decimal : " + temp.toString());

        return sb.toString();
    }*/

    private WebSocketClient webSocketClient;

    private void createWebSocketClient(int index,String url) {

        URI uri;
        try {
            uri = new URI(url);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {

            @Override
            public void onOpen() {
                 {
                    System.out.println("onOpen");
                    webSocketClient.send("Hello, World!");

                }
            }

            @Override
            public void onTextReceived(String message) {
                System.out.println("onTextReceived");
                if(message != null){
                    System.out.println("message: "+message);
                }
            }



            @Override
            public void send(String message) {
                JSONObject json = new JSONObject();
                try {
                    String addr;
                    String ipv4addr = com.vasa.vaibhav.example_wallet.Utils.getIPAddress(true);
                    String ipv6addr = com.vasa.vaibhav.example_wallet.Utils.getIPAddress(false);
                    if(!(ipv4addr.isEmpty())){
                        addr = ipv4addr;
                    }
                    else {
                        addr = ipv6addr;
                    }


                    json.put("ip", addr);
                    json.put("index", index);
                    json.put("cert",cipher);
                    json.put("pubkey",publicKey);
                    //json.put("color", "FF7000");
                    //json.put("socket", socketAddress);
                    message = json.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.send(message);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                System.out.println("onBinaryReceived");
                if(data != null){
                    System.out.println("message: "+data);
                }
            }

            @Override
            public void onPingReceived(byte[] data) {
                System.out.println("onPingReceived");
                if(data != null){
                    System.out.println("message: "+data);
                }
            }

            @Override
            public void onPongReceived(byte[] data) {
                System.out.println("onPongReceived");
                if(data != null){
                    System.out.println("message: "+data);
                }
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                System.out.println("onCloseReceived");
                lock = true;
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.addHeader("Origin", "http://developer.example.com");
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        barcodeDetector.release();
    }
}
