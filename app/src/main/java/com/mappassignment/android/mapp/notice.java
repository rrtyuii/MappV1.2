package com.mappassignment.android.mapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class notice extends AppCompatActivity  {
    private static SecretKeySpec secret;
    private Button signOut;
    private EditText test;
    private Button encrypt;
    private Button decrypt;
    private TextView secretKey;
    private String privateString;
    private byte[] key;
    private EditText testDecrypt;
    private static final String TAG = "encryption";
    SecretKeySpec sks = null;
    private static final String ALGORITHM = "AES";
    private String salt = "slkdjaldja";
    private String HashKey;
    private TextView HasedPrivateKey;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticemessage);
        HasedPrivateKey=(TextView) findViewById(R.id.HashKey);
        signOut = (Button) findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(notice.this, LogIn.class);
                startActivity(i);

            }}
        );
        //this is to get the secret key the secret key is sks
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any ddaadtdasadad udafasdased dasdasas randomsada seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
            //SecretKey Key = KeyGenerator.getInstance("AES").generateKey();
            String encodeKey = Base64.encodeToString(sks.getEncoded(),Base64.DEFAULT);
            secretKey = (TextView) findViewById((R.id.SecretKey));
            secretKey.setText(encodeKey);
           HashKey = Hash(encodeKey, salt);
            HasedPrivateKey.setText(HashKey);
        } catch (Exception e) {
            Log.e(TAG, "AES secret key spec error");
        }


//button declaration
    encrypt=(Button) findViewById(R.id.Encrypt);
        decrypt=(Button) findViewById(R.id.Decrypt);
        test=(EditText) findViewById(R.id.TestEncrypt);
        testDecrypt=(EditText) findViewById(R.id.TestDecrypt);

        //testing encrypting
        encrypt.setOnClickListener((new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Encrypt(test.getText().toString(),sks);
         }
     }));


        //testing decrypting
        decrypt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mDecrypt(Encrypt(test.getText().toString(),sks), sks);
            }

        });




    }

    //encryption
    public byte[] Encrypt(String plainText,SecretKeySpec sks){
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(plainText.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }

        test.setText("[ENCODED]:\n" +
                Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");

            return encodedBytes;
    }


    //decryption
    public String mDecrypt(byte[]encoded, SecretKeySpec sks )
    {
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(encoded);
        } catch (Exception e) {
            Log.e(TAG, "AES decryption error");
        }

        testDecrypt.setText("[DECODED]:\n" + new String(decodedBytes) + "\n");
        Log.d(TAG,decodedBytes.toString());
        return decodedBytes.toString();


    }

    public String Hash(String PrivateKey, String   salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(PrivateKey.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }






    }










