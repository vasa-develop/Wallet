package com.vasa.vaibhav.example_wallet;

/**
 * Created by vaibhav on 25/1/18.
 */

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

public class AESCrypt {

    //todo: work in progress for authprotocol with server

    private final Cipher cipher;
    private final SecretKeySpec key;
    private AlgorithmParameterSpec spec;
    public static final String SEED_16_CHARACTER = "U1MjU1M0FDOUZ.Qz";

    public AESCrypt() throws Exception {
        // hash password with SHA-256 and crop the output to 128-bit for key
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(SEED_16_CHARACTER.getBytes("UTF-8"));
        byte[] keyBytes = new byte[16];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);

        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        key = new SecretKeySpec(keyBytes, "AES");
        spec = getIV();
    }

    public AlgorithmParameterSpec getIV() {
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);

        return ivParameterSpec;
    }

    public String encrypt(String plainText) throws Exception {

            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));

            return Hex.encodeHexString( encrypted );

            //String encryptedText = new String(Base64.encode(encrypted,Base64.DEFAULT), "UTF-8");
            //return encryptedText;




    }

    public String decrypt(String cryptedText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(cryptedText);
        byte[] decrypted = cipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");

        return decryptedText;
    }

    public static void main(String [] args) throws Exception {
        AESCrypt aesCrypt = new AESCrypt();
        String cryptedText = aesCrypt.encrypt("1234567812345678");
        System.out.println("cryptedText: "+cryptedText);
        /*String plaintext = aesCrypt.decrypt(cryptedText);
        System.out.println("plaintext: "+plaintext);*/
    }



}