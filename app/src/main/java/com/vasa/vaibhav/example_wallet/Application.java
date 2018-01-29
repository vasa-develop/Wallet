package com.vasa.vaibhav.example_wallet;

/**
 * Created by vaibhav on 5/1/18.
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.web3j.abi.datatypes.Bool;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;
//import org.web3j.sample.contracts.generated.Greeter;


/**
 * A simple web3j application that demonstrates a number of core features of web3j:
 *
 * <ol>
 *     <li>Connecting to a node on the Ethereum network</li>
 *     <li>Loading an Ethereum wallet file</li>
 *     <li>Sending Ether from one address to another</li>
 *     <li>Deploying a smart contract to the network</li>
 *     <li>Reading a value from the deployed smart contract</li>
 *     <li>Updating a value in the deployed smart contract</li>
 *     <li>Viewing an event logged by the smart contract</li>
 * </ol>
 *
 * <p>To run this demo, you will need to provide:
 *
 * <ol>
 *     <li>Ethereum client (or node) endpoint. The simplest thing to do is
 *     <a href="https://infura.io/register.html">request a free access token from Infura</a></li>
 *     <li>A wallet file. This can be generated using the web3j
 *     <a href="https://docs.web3j.io/command_line.html">command line tools</a></li>
 *     <li>Some Ether. This can be requested igInteger val;
                try {
                    //val = BigInteger.valueOf((long) Double.parseDouble(transfer_val.getText().toString().trim()));
                }catch (Exception e){
                    //val = BigInteger.ONE;
                }from the
 *     <a href="https://www.rinkeby.io/#faucet">Rinkeby Faucet</a></li>
 * </ol>
 *
 * <p>For further background information, refer to the project README.
 */
public class Application extends AppCompatActivity {

    private Web3j web3j;
    private String WalletFile;
    private String reciever_address;
    private BigInteger value;

    private Credentials credentials;
    private String CONTRACT_ADDR = "0x2005e078693f182ce912550eb095e9f4e47900e5";
    private Test_solc_vasacoin test_solc_vasacoin;

    private TextView network,coinname,balance;
    private Button send ,recieve,scan_qrcode;
    private EditText transfer_addr,transfer_value;
    private ImageView qrcode;

    private String bal,name;

    private String NETWORK_URL;
    private String RINKEBY = "https://rinkeby.infura.io/MIY5d592BKY8caiAK2TJ";
    private String ROPSTEN = "https://ropsten.infura.io/MIY5d592BKY8caiAK2TJ";
    private String KOVAN = "https://kovan.infura.io/MIY5d592BKY8caiAK2TJ";

    private String COIN_CONTRACT_ADDR;
    private String VASACOIN = "0x2005e078693f182ce912550eb095e9f4e47900e5";


    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView barcodeValue;

    private LinearLayout wallet_layout;
    private RelativeLayout scanner_layout,qrcode_layout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application);

        NETWORK_URL = RINKEBY;

        COIN_CONTRACT_ADDR = VASACOIN;


        network = (TextView) findViewById(R.id.network);
        coinname = (TextView) findViewById(R.id.coin_name);
        balance = (TextView) findViewById(R.id.balance);

        send = (Button) findViewById(R.id.send);
        recieve = (Button) findViewById(R.id.recieve);
        scan_qrcode = (Button) findViewById(R.id.scan_qrcode);

        transfer_addr = (EditText) findViewById(R.id.transfer_addr);
        transfer_value = (EditText) findViewById(R.id.transfer_value);

        qrcode = (ImageView) findViewById(R.id.qrcode);

        wallet_layout = (LinearLayout) findViewById(R.id.wallet_layout);
        scanner_layout = (RelativeLayout) findViewById(R.id.scanner_layout);
        qrcode_layout = (RelativeLayout) findViewById(R.id.qrcode_layout); 

        scanner_layout.setVisibility(View.GONE);
        qrcode_layout.setVisibility(View.GONE);


        network.setText(NETWORK_URL);


        new ConnectTestnet().execute();
        try {
            CreateWallet();
        } catch (Exception e) {
            Log.d("wallet_create_err: ", String.valueOf(e));
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    reciever_address = transfer_addr.getText().toString();
                    value = BigInteger.valueOf(Long.parseLong(transfer_value.getText().toString()));
                    Toast.makeText(getApplicationContext(),"Sending "+value+" vasacoins to "+reciever_address,Toast.LENGTH_LONG);
                    new Transfer().execute();
                } catch (Exception e) {
                    Log.d("wallet_transfer_err: ", String.valueOf(e));
                }
            }
        });

        recieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_layout.setVisibility(View.GONE);
                scanner_layout.setVisibility(View.GONE);
                qrcode_layout.setVisibility(View.VISIBLE);
            }
        });

        scan_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    wallet_layout.setVisibility(View.GONE);
                    scanner_layout.setVisibility(View.VISIBLE);
                    qrcode_layout.setVisibility(View.GONE);

                    cameraView = (SurfaceView) findViewById(R.id.surface_view);
                    barcodeValue = (TextView) findViewById(R.id.barcode_value);

                    barcodeDetector = new BarcodeDetector.Builder(Application.this)
                            .setBarcodeFormats(Barcode.ALL_FORMATS)
                            .build();

                    cameraSource = new CameraSource.Builder(Application.this, barcodeDetector)
                            .setRequestedPreviewSize(1600, 1024)
                            .setAutoFocusEnabled(true) //you should add this feature
                            .build();

                    cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                        @Override
                        public void surfaceCreated(SurfaceHolder holder) {
                            try {
                                //noinspection MissingPermission
                                if (ActivityCompat.checkSelfPermission(Application.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                                        //barcodeValue.setText(barcodes.valueAt(0).displayValue);
                                        scanner_layout.setVisibility(View.GONE);
                                        wallet_layout.setVisibility(View.VISIBLE);
                                        qrcode_layout.setVisibility(View.GONE);
                                        transfer_addr.setText(barcodes.valueAt(0).displayValue);
                                    }
                                });
                            }
                        }
                    });

                } catch (Exception e) {
                    new ConnectTestnet().execute();
                    Log.d("wallet_recieve_err: ", String.valueOf(e));
                }
            }
        });


    }

    class ConnectTestnet extends AsyncTask<String, Void, Web3j> {
        String transfer_to;
        BigInteger val;
        @Override
        protected Web3j doInBackground(String... strings) {

            web3j = Web3jFactory.build(new HttpService(
                    NETWORK_URL));
            try {
                Log.d("wallet_network_log: ","Connected to Ethereum client version: "
                        + web3j.web3ClientVersion().send().getWeb3ClientVersion());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return web3j;
        }
    }

    class InitiateCoinContract extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            test_solc_vasacoin = Test_solc_vasacoin.load(COIN_CONTRACT_ADDR,web3j,credentials,ManagedTransaction.GAS_PRICE,Contract.GAS_LIMIT);
            Log.d("wallet_coinctrct_init: ","SUCCESS");
            try {
                name = test_solc_vasacoin.name().send();
                bal = test_solc_vasacoin.balanceOf(credentials.getAddress()).send().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                return test_solc_vasacoin.name().send();
            } catch (Exception e) {
                Log.d("wallet_coin_name_err: ", String.valueOf(e));
                return "vasacoin";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            String addr = credentials.getAddress().toString();
            try {
                qrcode.setImageBitmap(new QrCodeGenerator().generateQrCode(addr,190));
                balance.setText(bal);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            coinname.setText(name);
            super.onPostExecute(s);
        }
    }

    private String CreateWallet() throws Exception{

        File f = new File("/data/user/0/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore");
        String[] d = f.list();
        if(f.list()==null) {

            Toast.makeText(getApplicationContext(), "Please wait...Creating a wallet for you.", Toast.LENGTH_LONG);
            WalletUtils.generateLightNewWalletFile("borntochange", new File("/data/user/0/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore"));
            Toast.makeText(getApplicationContext(), "Wallet created successfully!!", Toast.LENGTH_LONG);
            WalletFile = d[0];
            Log.d("wallet_name: ", String.valueOf(d[0]));
            new InitiateWallet().execute();
            new InitiateCoinContract().execute();
            return WalletFile;
        }
        else {
            WalletFile = d[0];
            Log.d("wallet_name: ", String.valueOf(d[0]));
            new InitiateWallet().doInBackground();
            new InitiateCoinContract().execute();
            return d[0];
        }

    }

    class InitiateWallet extends AsyncTask<String, Void, Credentials> {

        @Override
        protected Credentials doInBackground(String... strings) {
            try {
                credentials = WalletUtils.loadCredentials("borntochange","/data/user/0/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore/"+WalletFile);
            } catch (Exception e) {
                Log.d("wallet_init_err: ", String.valueOf(e));
            }
            Log.d("wallet_initiation: ","SUCCESS");
            return credentials;
        }
    }

    class Transfer extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            /*try {

            }catch (NullPointerException e){
                //reciever_address = "0xa840135b540365af23a6a7189097d2e779bf9783";
                Log.d("wallet_transfer_err:", String.valueOf(e));
            }*/

            Log.d("wallet_transaction: ","STARTED");
            String hash = null;
            try {
                hash = test_solc_vasacoin.transfer(reciever_address, value).send().getTransactionHash();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //String hash = test_solc_vasacoin.transfer(reciever_address,BigInteger.valueOf(100)).send().getTransactionHash();
            //Toast.makeText(getApplicationContext(),"Transaction Completed.",Toast.LENGTH_LONG).show();
            Log.d("wallet_transaction: ","Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
                    + hash);
            try {
                bal = test_solc_vasacoin.balanceOf(credentials.getAddress()).send().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            balance.setText(bal);
            Log.d("wallet_balance: ",bal);
            super.onPostExecute(aBoolean);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        barcodeDetector.release();
    }

    @Override
    public void onBackPressed() {
        wallet_layout.setVisibility(View.VISIBLE);
        scanner_layout.setVisibility(View.GONE);
        qrcode_layout.setVisibility(View.GONE);
    }
}