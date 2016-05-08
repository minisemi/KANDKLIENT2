package com.example.adrian.klient.vote;

import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Alexander on 2016-04-18.
 * Generally speaking, there are 2 kinds of encryption algorithms——symmetric-key algorithm and asymmetric-key algorithm.

 For symmetric-key algorithm, the same cryptographic key is used for both encryption and decryption, in comparison to asymmetric-key algorithm symmetric-key algorithm like AES is usually high speed and low RAM requirements, but because it’s the same key for both encryption and decryption, it’s a big problem of key transport from encryption side (sender) to decryption side (receiver).

 For asymmetric-key algorithm, it requires two separate keys, one of which is secret (or private) and one of which is public. Although different, the two parts of this key pair are mathematically linked. The public key is used to encrypt plaintext or to verify a digital signature; whereas the private key is used to decrypt ciphertext or to create a digital signature, comparing to symmetric-key algorithm, asymmetric-key algorithm does not have the problem of key transport, but it is computationally costly compared with symmetric key algorithm.

 The way to make both ends meet is using the 2 algorithms in combination:

 1. Data receiver creates the key pairs of asymmetric-key algorithm, and publishes the public key to sender.
 2. Sender uses symmetric-key algorithm to encrypt data, and uses asymmetric-key algorithm to encrypt that symmetric key with receiver’s public key.
 Receiver uses its private key to decrypt the symmetric key, and then decrypt data with the symmetric key.
 3. Here symmetric-key algorithm is only used to encrypt the symmetric key, computationally cost is negligible.

 Yes, that’s the way SSL works!

 For our file encryption tool, AES (A symmetric-key algorithm) is used to encrypt file data, and RSA (an asymmetric cryptography standard) is used to encrypt AES key.
 */

public class FileEncryption {

    Cipher pkCipher;
    Cipher aesCipher;
    int AES_Key_Size = 256;
    byte[] aesKey;
    SecretKeySpec aeskeySpec;
    FileUtils fileUtils;
    private static final String TAG = VoteActivity.class.getSimpleName();

    /**
     * Constructor: creates ciphers
     */
    public FileEncryption() throws GeneralSecurityException {
        // create RSA public key cipher
        pkCipher = Cipher.getInstance("RSA");
        // create AES shared key cipher
        aesCipher = Cipher.getInstance("AES");
        fileUtils = new FileUtils();
    }

    /**
     * Creates a new AES key
     */
    public void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        aesKey = key.getEncoded();
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
    }

    /**
     * Decrypts an AES key from a file using an RSA private key
     */
    public void loadKey(File in, File privateKeyFile) throws GeneralSecurityException, IOException {
        // read private key to be used to decrypt the AES key
        byte[] encodedKey = new byte[(int)privateKeyFile.length()];
        new FileInputStream(privateKeyFile).read(encodedKey);

        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

        // read AES key
        pkCipher.init(Cipher.DECRYPT_MODE, pk);
        aesKey = new byte[AES_Key_Size/8];
        Log.d(TAG, "Encrypted AES key");
        String encryptedText = fileUtils.readFileToString(in);
        Log.d(TAG, encryptedText);
        CipherInputStream is = new CipherInputStream(new FileInputStream(in), pkCipher);
        is.read(aesKey);
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
    }


    public String getEncryptedAesKey (File encryptedAesKey) {
        try{
            String encryptedText = fileUtils.readFileToString(encryptedAesKey);;
            return encryptedText;
    }catch (IOException i){
        i.printStackTrace();
    }
        return null;
    }

    /**
     * Encrypts the AES key to a file using an RSA public key
     */
    public void saveKey(File out, File publicKeyFile) throws IOException, GeneralSecurityException {
        // read public key to be used to encrypt the AES key
        byte[] encodedKey = new byte[(int)publicKeyFile.length()];
        new FileInputStream(publicKeyFile).read(encodedKey);

        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(publicKeySpec);

        // write AES key
        pkCipher.init(Cipher.ENCRYPT_MODE, pk);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), pkCipher);
        os.write(aesKey);
        os.close();
    }

    /**
     * Encrypts and then copies the contents of a given file.
     */
    public void encrypt(File in, File out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

        copy(is, os);

        os.close();
    }

    /**
     * Decrypts and then copies the contents of a given file.
     */
    public void decrypt(File in, File out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
        FileOutputStream os = new FileOutputStream(out);

        copy(is, os);

        is.close();
        os.close();
    }

    /**
     * Copies a stream.
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while((i=is.read(b))!=-1) {
            os.write(b, 0, i);
        }
    }
}
