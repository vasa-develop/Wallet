package com.vasa.vaibhav.example_wallet;

import android.content.Intent;
import android.net.Credentials;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Auth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        class EncryptionProvider2 {
            private final String characterEncoding = "UTF-8";
            private final String cipherTransformation = "AES/CBC/PKCS5Padding";
            private final String aesEncryptionAlgorithm = "AES";

            public byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
                Cipher cipher = Cipher.getInstance(cipherTransformation);
                SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
                try {
                    cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }
                cipherText = cipher.doFinal(cipherText);

                return cipherText;
            }

            public byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
                Cipher cipher = Cipher.getInstance(cipherTransformation);
                SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
                plainText = cipher.doFinal(plainText);
                return plainText;
            }

            private byte[] getKeyBytes(String key) throws UnsupportedEncodingException, UnsupportedEncodingException {
                byte[] keyBytes = new byte[16];
                byte[] parameterKeyBytes = key.getBytes(characterEncoding);
                System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
                return keyBytes;
            }


            public String encrypt(String plainText, String key) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
                byte[] plainTextbytes = plainText.getBytes(characterEncoding);
                byte[] keyBytes = getKeyBytes(key);
                return Base64.encodeToString(encrypt(plainTextbytes, keyBytes, keyBytes), Base64.NO_WRAP);
            }


            public String decrypt(String encryptedText, String key) throws KeyException, GeneralSecurityException, GeneralSecurityException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
                byte[] cipheredBytes = Base64.decode(encryptedText, Base64.NO_WRAP);
                byte[] keyBytes = getKeyBytes(key);
                return new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);
            }
        }

        org.web3j.crypto.Credentials credentials = null;
        File f = new File("/data/data/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore/");
        String[] d = f.list();
        String WalletFile = d[0];
        try {
            credentials = WalletUtils.loadCredentials("borntochange", "/data/data/com.vasa.vaibhav.example_wallet/files/.ethereum/keystore/" + WalletFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }

        System.out.println("privkey: "+credentials.getEcKeyPair().getPrivateKey().toString());
        System.out.println("pubkey: "+credentials.getEcKeyPair().getPublicKey().toString());


        EncryptionProvider2 encryptionProvider2 = new EncryptionProvider2();
        try {
            String encrypt = encryptionProvider2.encrypt("123456", credentials.getEcKeyPair().getPrivateKey().toString());
            System.out.println("ecrypt: "+encrypt);

            /*Decoder decoder = new Decoder();
            decoder.crypted = encrypt;
            decoder.pubkey = credentials.getEcKeyPair().getPublicKey().toString();
            decoder.execute();*/

            /*String decrypt = encryptionProvider2.decrypt(encrypt, credentials.getEcKeyPair().getPublicKey().toString());
            System.out.println(decrypt);*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (KeyException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }






    class Decoder extends AsyncTask<String, Void, Boolean> {
        String crypted;
        String pubkey;
        String response = "";

        @Override
        protected Boolean doInBackground(String... strings) {
            //admin_panel/authprotocol/decoder.php
            URL url = null;
            try {
                //url = new URL("http://162.144.124.122/~lokasotech/stage_v2/app_panel/admin/admin_panel/authprotocol/test.php?hex=" + hex);
                url = new URL("http://162.144.124.122/~lokasotech/stage_v2/app_panel/admin/admin_panel/authprotocol/test.php?crypted="+crypted+"&pubkey="+pubkey);
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
    }
}
