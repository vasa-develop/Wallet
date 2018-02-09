package com.vasa.vaibhav.example_wallet;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.Tuple;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class KYCActivity extends AppCompatActivity {

    private TextView name,email,mobile_no;
    private String name_,email_;
    private BigInteger mobile_no_=BigInteger.valueOf(813076);

    private Web3j web3j;
    private Credentials credentials;
    private KYC_solc_HackKYC kyc_solc_hackKYC;

    private String NETWORK_URL = "https://rinkeby.infura.io/MIY5d592BKY8caiAK2TJ";
    private String CONTRACT_URL = "0x752Dd0d9dF96c35314F1542B18E8E5136f0ac105";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        mobile_no = (TextView) findViewById(R.id.mobile_no);


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

                    start_KYC();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return web3j;
            }
        }







        try{
            Intent intent = getIntent();
            if(intent.getBooleanExtra("request_kyc",false)){
                AlertDialog.Builder builder = new AlertDialog.Builder(KYCActivity.this);
                builder.setMessage("VasaCart wants to access your KYC info.")
                        .setPositiveButton("give access", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getApplicationContext(),"Preparing KYC to be sent...",Toast.LENGTH_LONG).show();
                                new ConnectTestnet().execute();
                            }
                        })
                        .setNegativeButton("reject", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();


            }


        }catch (Exception e){
            Log.d("wallet_kyc_err: ", String.valueOf(e));
        }


    }

    private void start_KYC() throws IOException, CipherException {
        File f = new File("/data/data/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore/");
        String[] d = f.list();
        String WalletFile = d[0];
        Log.d("wallet_name: ", String.valueOf(d[0]));



        credentials = WalletUtils.loadCredentials("borntochange","/data/data/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore/"+WalletFile);

        kyc_solc_hackKYC = KYC_solc_HackKYC.load(CONTRACT_URL,web3j,credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        /*try {
            String hash = kyc_solc_hackKYC.AddUser(credentials.getAddress(),name.getText().toString(),email.getText().toString(),mobile_no_).send().getTransactionHash();
            Log.d("KYC_transaction: ","KYC Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
                    + hash);
        } catch (Exception e) {
            Log.d("KYC_transaction_err: ", String.valueOf(e));
        }*/
        Tuple tuple = null;
        try {
            tuple = kyc_solc_hackKYC.viewUser(credentials.getAddress(),BigInteger.valueOf(0)).send();
            Log.d("KYC_tuple: ", String.valueOf(tuple));
        } catch (Exception e) {
            Log.d("KYC_tuple_err: ", String.valueOf(e));
        }

        Intent myIntent = new Intent();
        myIntent.setClassName("com.vasa.vaibhav.vasacart", "com.vasa.vaibhav.vasacart.KYC");

        myIntent.putExtra("details",String.valueOf(tuple));


        startActivity(myIntent);
    }


}