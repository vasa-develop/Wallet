package com.vasa.vaibhav.example_wallet;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;


import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.content.ContentValues.TAG;


/**
 * Created by vaibhav on 8/1/18.
 */

public class QrCodeGenerator {

    public Bitmap generateQrCode(String inputValue , int smallerDimension) throws WriterException {
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDimension);

        // Getting QR-Code as Bitmap
        return qrgEncoder.encodeAsBitmap();
    }


}

