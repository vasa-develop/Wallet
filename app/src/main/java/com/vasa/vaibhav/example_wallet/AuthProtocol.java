package com.vasa.vaibhav.example_wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.spongycastle.util.encoders.Base64;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPairGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AuthProtocol extends AppCompatActivity {

    /**
     * String to hold name of the encryption algorithm.
     */
    public static final String ALGORITHM = "RSA";

    /**
     * String to hold the name of the private key file.
     */
    public static final String PRIVATE_KEY_FILE = Environment.getExternalStorageDirectory()+"/privatekey.key";

    /**
     * String to hold name of the public key file.
     */
    public static final String PUBLIC_KEY_FILE = Environment.getExternalStorageDirectory()+"/publickey.key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_protocol);

        try {

            // Check if the pair of keys are present else generate those.
            if (!areKeysPresent()) {
                // Method generates a pair of keys using the RSA algorithm and stores it
                // in their respective files
                generateKey();
            }



            final String originalText = "BORAT";
            ObjectInputStream inputStream = null;

            // Encrypt the string using the public key

            try{
                Intent intent = getIntent();

                    byte[] cipher = intent.getByteArrayExtra("cipherText");
                    String clientPublicKey = intent.getStringExtra("clientPubKey");
                    PublicKey PublicKey = (PublicKey) new SecretKeySpec(Base64.decode(clientPublicKey),"RSA");
                    final String plainText = decrypt(cipher,  PublicKey);
                    Log.d("cipher: ", String.valueOf(plainText));
                    if(plainText.equals("BORAT")){

                        inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
                        final PublicKey publicKey = (PublicKey) inputStream.readObject();

                        inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
                        final PrivateKey privateKey = (PrivateKey) inputStream.readObject();

                        final byte[] cipherText = encrypt(originalText, privateKey);

                        Intent myIntent = new Intent();
                        myIntent.setClassName("com.vasa.vaibhav.vasacart", "com.vasa.vaibhav.vasacart.AuthProtocol");

                        myIntent.putExtra("cipherText",String.valueOf(cipherText));
                        myIntent.putExtra("vendorPublicKey",String.valueOf(publicKey));
                        myIntent.putExtra("VendorAuthConfirmation",true);

                        startActivity(myIntent);
                    }



            }catch (Exception e){

            }



            // Decrypt the cipher text using the private key.



        } catch (Exception e) {
            e.printStackTrace();
        }


}



    /**
     * Generate key which contains a pair of private and public key using 1024
     * bytes. Store the set of keys in Prvate.key and Public.key files.
     *
     *
     */

    public static void generateKey() {
        try {

            org.web3j.crypto.Credentials credentials = null;
            File f = new File("/data/data/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore/");
            String[] d = f.list();
            String WalletFile = d[0];
            try {
                credentials = WalletUtils.loadCredentials("borntochange","/data/data/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore/"+WalletFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CipherException e) {
                e.printStackTrace();
            }

            /*final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);*/
            ECKeyPair key = credentials.getEcKeyPair();
            //final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(PRIVATE_KEY_FILE);
            File publicKeyFile = new File(PUBLIC_KEY_FILE);

            // Create files to store public and private key
            if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }
            privateKeyFile.createNewFile();

            if (publicKeyFile.getParentFile() != null) {
                publicKeyFile.getParentFile().mkdirs();
            }
            publicKeyFile.createNewFile();

            // Saving the Public key in a file
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(
                    new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublicKey());
            publicKeyOS.close();

            // Saving the Private key in a file
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(
                    new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivateKey());
            privateKeyOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * The method checks if the pair of public and private key has been generated.
     *
     * @return flag indicating if the pair of keys were generated.
     */
    public static boolean areKeysPresent() {

        File privateKey = new File(PRIVATE_KEY_FILE);
        File publicKey = new File(PUBLIC_KEY_FILE);

        if (privateKey.exists() && publicKey.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Encrypt the plain text using public key.
     *
     * @param text : original plain text
     * @param key  :The public key
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public static byte[] encrypt(String text, PrivateKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    /**
     * Decrypt text using private key.
     *
     * @param text :encrypted text
     * @param key  :The private key
     * @return plain text
     * @throws java.lang.Exception
     */
    public static String decrypt(byte[] text, PublicKey key) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }

    /**
     * Test the EncryptionUtil
     */
    public static void main(String[] args) {


    }
}
//modulus=bf4945c96e7654ca3e2c938c113e417ac609063ec1e1cb0743e7cff8bc1fd34025c6d63e61b2a9d6857329d8798e99f393801655d0a23368ace415443e167b68ada257d3971c9e468c58656ed2b53ca915d6b7a6099dbb62f64fc2210b91e683521b18d191601653d5d0f3cb23e9bed93e818cf423abbca028e52bc64ef56821,publicExponent=10001,privateExponent=bcd95c3ab801bf484405c24da0a074090006dcd18c9c9cd339695d5bd66f1d22d1a8f2cf77a8ea204e741a360c1c8b457cb41258fab78845a99b2c9bc9286d007ea4e38800545a04030a6c807f034d8f3280e6bdfad082fba7829f4e40de3f0bf1cff7d3d45b39c188c7c2a5c252639490846f5749ade92d0a110214111a47f9,primeP=f39aeecb01d8ca1c6ec717722aecff156d5fd36ebe411fdcd5ef68d7fe95141af6a5d652ddfcf4ed10dfcc233f242a600182a573d52884184744f62ec2b567f7,primeQ=c904dc8e0a2e5de67821e98c86f98faea44f41d97416cc4cdd55a9a8022b132d2bc8067d6b0654f8018f57927a07afefc0fff30c333d2b765b4a864d698c9aa7,primeExponentP=407db5d61ca90de8b513140bef7d4a929ba010d3729ebf16b1b46de730c8bac7e0a04abb2975bc49be7be1093bc0114fd5568702c15db68acee35ce1483eca45,primeExponentQ=4f4e9bb56e4edcbd10a8507c20c454eb91e7b31317b9e6e06c725f7ce95e1532bd132d7c4dbd31c8a6d4e6b7e36d24fc722888bb477bdaa9d75a8fa1fb027e01,crtCoefficient=3d6ae4993dd75b4f425d9477529cb21c1c289cf86115958e76ab669f00a1f166d936f866c28e68251c6a0879f21c83a41e6743a9a7190388b3edc9de021a61ae
//modulus=bf4945c96e7654ca3e2c938c113e417ac609063ec1e1cb0743e7cff8bc1fd34025c6d63e61b2a9d6857329d8798e99f393801655d0a23368ace415443e167b68ada257d3971c9e468c58656ed2b53ca915d6b7a6099dbb62f64fc2210b91e683521b18d191601653d5d0f3cb23e9bed93e818cf423abbca028e52bc64ef56821,publicExponent=10001