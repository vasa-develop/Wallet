package com.vasa.vaibhav.example_wallet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.SignatureException;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multiaddr.MultiAddress;

public class Wallet extends AppCompatActivity {

    private Web3j web3j;
    private String WalletFile,bal,name;
    private Credentials credentials;
    private Test_solc_vasacoin test_solc_vasacoin;

    private LinearLayout wallet_layout;
    private RelativeLayout send_layout,recieve_layout;

    private TextView network,coin_name,balance;
    private Button send,recieve,transfer,add_to_ipfs;
    private SurfaceView cameraView;
    private EditText transfer_addr,transfer_value,description;
    private ImageView qrcode;

    private String NETWORK_URL;
    private String RINKEBY = "https://rinkeby.infura.io/MIY5d592BKY8caiAK2TJ";
    private String ROPSTEN = "https://ropsten.infura.io/MIY5d592BKY8caiAK2TJ";
    private String KOVAN = "https://kovan.infura.io/MIY5d592BKY8caiAK2TJ";
    private String IPFS = "https://ipfs.infura.io:5001";

    private String COIN_CONTRACT_ADDR;
    private String VASACOIN = "0x2005e078693f182ce912550eb095e9f4e47900e5";

    String filepath1 = "/data/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore/";
    String filepath2 = "/data/data/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore/";
    String filepath;
    File f1 = new File(filepath1);
    File f2 = new File(filepath2);
    
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private boolean trasactionIntent=false;
    private boolean authIntent = false;

    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        network = (TextView) findViewById(R.id.network);
        coin_name = (TextView) findViewById(R.id.coin_name);
        balance = (TextView) findViewById(R.id.balance);


        send = (Button) findViewById(R.id.send);
        recieve = (Button) findViewById(R.id.recieve);
        transfer = (Button) findViewById(R.id.transfer);
        add_to_ipfs = (Button) findViewById(R.id.add_to_ipfs);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);

        transfer_addr = (EditText) findViewById(R.id.transfer_addr);
        transfer_value = (EditText) findViewById(R.id.transfer_value);
        description = (EditText) findViewById(R.id.description);

        qrcode = (ImageView) findViewById(R.id.qrcode);

        wallet_layout = (LinearLayout) findViewById(R.id.wallet_layout);
        send_layout = (RelativeLayout) findViewById(R.id.send_layout);
        recieve_layout = (RelativeLayout) findViewById(R.id.recieve_layout);



        int transfer_val = 0;
        try{
            Intent intent = getIntent();

            transfer_val = intent.getIntExtra("transfer_value",1);

            Log.d("wallet_intent: ", String.valueOf(transfer_val));

            transfer_value.setText(String.valueOf(transfer_val));
            trasactionIntent = true;

        }catch (Exception e){
            trasactionIntent = false;
            Log.d("wallet_intent_err: ", String.valueOf(e));
        }



        wallet_layout.setVisibility(View.VISIBLE);
        send_layout.setVisibility(View.GONE);
        recieve_layout.setVisibility(View.GONE);

        
        try {
            Intent intent = getIntent();
            message = intent.getStringExtra("message");
            Log.d("message: ",message);
            authIntent = true;
            CreateWallet();
        } catch (Exception e) {
            authIntent = false;
            loadWallet();
            e.printStackTrace();
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_layout.setVisibility(View.GONE);
                send_layout.setVisibility(View.VISIBLE);
                recieve_layout.setVisibility(View.GONE);
                startScanner();
            }
        });

        recieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_layout.setVisibility(View.GONE);
                send_layout.setVisibility(View.GONE);
                recieve_layout.setVisibility(View.VISIBLE);

            }
        });

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Transfer().execute();
            }
        });

        add_to_ipfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void loadWallet() {
        NETWORK_URL = RINKEBY;

        COIN_CONTRACT_ADDR = VASACOIN;
        network.setText(NETWORK_URL);


        new ConnectTestnet().execute();
        try {
            CreateWallet();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private String CreateWallet() throws Exception{

        File f = null;

        if(f1.isDirectory()){
            f = f1;
            filepath = filepath1;
            Log.d("wallet_filepath: ", filepath);
        }
        else if(f2.isDirectory()){
            f = f2;
            filepath = filepath2;
            Log.d("wallet_filepath: ", filepath);
        }

        String[] d = f.list();

            if (d == null) {

                //Toast.makeText(getApplicationContext(), "Please wait...Creating a wallet for you.", Toast.LENGTH_LONG);
                WalletUtils.generateLightNewWalletFile("borntochange", new File(filepath));
                //Toast.makeText(getApplicationContext(), "Wallet created successfully!!", Toast.LENGTH_LONG);
                WalletFile = d[0];
                AuthProtocol();
                Log.d("wallet_name: ", String.valueOf(d[0]));
                new InitiateWallet().execute();
                new InitiateCoinContract().execute();
                return WalletFile;
            }
        else {
                WalletFile = d[0];
                AuthProtocol();
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
                credentials = WalletUtils.loadCredentials("borntochange",filepath+WalletFile);

            } catch (Exception e) {
                Log.d("wallet_init_err: ", String.valueOf(e));
            }
            Log.d("wallet_initiation: ","SUCCESS");
            Log.d("wallet_Pubkey: ", String.valueOf(credentials.getEcKeyPair().getPublicKey()));
            Log.d("wallet_Prikey: ", String.valueOf(credentials.getEcKeyPair().getPrivateKey()));
            Log.d("wallet_addr: ",credentials.getAddress());



            return credentials;
        }
    }

    public void AuthProtocol() throws SignatureException {

        if(authIntent) {
            try {
                credentials = WalletUtils.loadCredentials("borntochange", filepath + WalletFile);


                byte[] hexMessage = Hash.sha3(message.getBytes());
                Sign.SignatureData signMessage = Sign.signMessage(hexMessage, credentials.getEcKeyPair());

                Log.d("R: ", String.valueOf((signMessage.getR().clone())));
                Log.d("S: ", String.valueOf((signMessage.getS().clone())));//String pubkey = "7138663510653550718662385675652596987052393110042324543283744119624
                Log.d("V: ", String.valueOf((signMessage.getV())));

                Intent myIntent = new Intent();
                myIntent.setClassName("com.vasa.vaibhav.vasacart", "com.vasa.vaibhav.vasacart.AuthProtocol");
                myIntent.putExtra("R", (signMessage.getR().clone()));
                myIntent.putExtra("S", (signMessage.getS().clone()));
                myIntent.putExtra("V", signMessage.getV());
                myIntent.putExtra("pubKey", String.valueOf(credentials.getAddress()));
                myIntent.putExtra("message",message);

                startActivity(myIntent);

            } catch (Exception e) {
                Log.d("Authentication_err: ", String.valueOf(e));
            }
        }
        // Message to sign
        //String plainMessage = "hello world";


        // Use java to sign and verify the signature

        /*System.out.println("Signed message    : "+ signMessage.hashCode());
        String pubKey = Sign.signedMessageToKey(hexMessage, signMessage).toString(16);
        String signerAddress = Keys.getAddress(pubKey);
        System.out.println("Signer address    : 0x"+ signerAddress);*/

        // Now use java signature to verify from the btrasactionIntentchain
        /*Bytes32 message = new Bytes32(hexMessage);
        Uint8 v = new Uint8(signMessage.getV());
        Bytes32 r = new Bytes32(signMessage.getR());
        Bytes32 s = new Bytes32(signMessage.getS());*/

       /* String address = contract.verify(message, v, r, s).get().getValue().toString(16);
        String address2 = contract.verifyWithPrefix(message, v, r, s).get().getValue().toString(16);
        System.out.println("Recovered address1 : 0x"+address);
        System.out.println("Recovered address2 : 0x"+address2);*/
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
            coin_name.setText(name);
            super.onPostExecute(s);
        }
    }


    class ConnectToIPFS extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {



            IPFS ipfs = new IPFS(new MultiAddress("/ip4/104.131.131.82/tcp/4001/ipfs/QmaCpDMGvV2BGHeYERUEnRQAwe3N8SzbUtfsmvsqQLuvuJ"));
            try {
                ipfs.refs.local();
            } catch (IOException e) {
                e.printStackTrace();
            }

               /* NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File("hello.txt"));
                try {
                    MerkleNode addResult = ipfs.add(file).get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            return true;
        }
    }


    String hash = null;
    class Transfer extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            /*try {

            }catch (NullPointerException e){
                //reciever_address = "0xa840135b540365af23a6a7189097d2e779bf9783";
                Log.d("wallet_transfer_err:", String.valueOf(e));
            }*/

            Log.d("wallet_transaction: ","STARTED");

            try {
                BigInteger val = BigInteger.valueOf(Long.parseLong(transfer_value.getText().toString()));
                hash = test_solc_vasacoin.transfer(transfer_addr.getText().toString(),val).send().getTransactionHash();
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
            if(trasactionIntent){
                Intent myIntent = new Intent();
                myIntent.setClassName("com.vasa.vaibhav.vasacart", "com.vasa.vaibhav.vasacart.MainActivity");
                myIntent.putExtra("hash",hash); // key/value pair, where key needs current package prefix.
                startActivity(myIntent);
            }


        }
    }

    private void startScanner() {
        try {
            
            cameraView = (SurfaceView) findViewById(R.id.surface_view);
            
            barcodeDetector = new BarcodeDetector.Builder(Wallet.this)
                    .setBarcodeFormats(Barcode.ALL_FORMATS)
                    .build();

            cameraSource = new CameraSource.Builder(Wallet.this, barcodeDetector)
                    .setRequestedPreviewSize(1600, 1024)
                    .setAutoFocusEnabled(true) //you should add this feature
                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(Wallet.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                        transfer_addr.post(new Runnable() {
                            @Override
                            public void run() {
                                //Update barcode value to TextView
                                //barcodeValue.setText(barcodes.valueAt(0).displayValue);
                                transfer_addr.setText(barcodes.valueAt(0).displayValue);
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            Log.d("wallet_recieve_err: ", String.valueOf(e));
        }
    }

    @Override
    public void onBackPressed() {
        wallet_layout.setVisibility(View.VISIBLE);
        send_layout.setVisibility(View.GONE);
        recieve_layout.setVisibility(View.GONE);
    }
}

