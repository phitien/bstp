package com.bosch.si.emobility.bstp.core;

import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sgp0458 on 8/12/15.
 */
public class UserSessionManager {

    private static UserSessionManager ourInstance = new UserSessionManager();

    public static UserSessionManager getInstance() {
        return ourInstance;
    }

    private UserSessionManager() {
        restoreSession();
    }

    public void clearUserSession() {
        setUserSession(null);
    }

    private User user;
    private boolean sessionExpired;
    private static final String CREDENTIALS_FILE_NAME = "config.dat";
    private static final String CREDENTIALS_FOLDER_NAME = "Credentials";
    private File credentialsFile = Utils.getFile(CREDENTIALS_FOLDER_NAME, CREDENTIALS_FILE_NAME);

    public void setUserSession(User user) {
        try {
            sessionExpired = false;
            this.user = user;
            deleteCredentialsFile();//delete old file
            if (this.user != null && this.user.isValid() && this.user.isSaveCredentials()) {
                String base64EncryptedCredentialsData = new AESCryptor().encryptData(this.user.toJsonString());
                FileOutputStream outputStream = new FileOutputStream(this.credentialsFile);
                outputStream.write(base64EncryptedCredentialsData.getBytes());
                outputStream.close();
            } else {
                Event.broadcast("", Constants.EventType.LOGOUT_OK.toString());
            }
        } catch (Exception e) {
            Utils.Log.e("BSTP_UserSessionManager_setUser", e.getMessage());
        }
    }

    public User getUser() {
        return user;
    }

    private void restoreSession() {
        try {
            if (user == null) {
                String base64EncryptedCredentialsData = Utils.getFileContent(CREDENTIALS_FOLDER_NAME, CREDENTIALS_FILE_NAME);
                if (base64EncryptedCredentialsData != null) {
                    String decryptedCredentialsData = new AESCryptor().decryptData(base64EncryptedCredentialsData);
                    setUserSession(User.parseUser(decryptedCredentialsData));
                }
            }
        } catch (Exception e) {
            Utils.Log.e("BSTP_UserSessionManager_restoreSession", e.getMessage());
        }
    }

    private void deleteCredentialsFile() {
        if (this.credentialsFile.exists()) {
            this.credentialsFile.delete();
        }
    }

    public boolean isLogged() {
        if (user != null) {
            return !sessionExpired && this.user.isValid();
        }
        return false;
    }

    public boolean isSessionExpired() {
        return sessionExpired;
    }

    public void setSessionExpired(boolean sessionExpired) {
        this.sessionExpired = sessionExpired;
    }

    public static class AESCryptor {

        private static final String AES_ALGORITHM_SPEC = "AES/CBC/PKCS5Padding";
        private static final String ANDROID_OPEN_SSL = "AndroidOpenSSL";
        private static final byte[] ivBytes = {0x77, 0x05, 0x40, 0x70, 0x65, 0x32, 0x54, 0x68, 0x39, 0x28, 0x18, 0x47, 0x35, 0x09, 0x23, 0x20};

        public static String encryptData(String input) {
            try {
                SecretKey secretKey = KeyStoreManager.getSecretKey();
                byte[] inputDataBytes = input.getBytes();
                Cipher cipher = Cipher.getInstance(AES_ALGORITHM_SPEC, "BC");
                IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                byte[] encryptedDataBytes = cipher.doFinal(inputDataBytes, 0, inputDataBytes.length);
                String base64EncodedDataBytes = Base64.encodeToString(encryptedDataBytes, Base64.DEFAULT);
                return base64EncodedDataBytes;
            } catch (Exception e) {
                Utils.Log.e("BSTP_AESCryptor_encryptData", e.getMessage());
            }
            return null;
        }

        public String decryptData(String input) {
            try {
                SecretKey secretKey = KeyStoreManager.getSecretKey();
                byte[] decodedInputDataBytes = Base64.decode(input.getBytes(), Base64.DEFAULT);
                Cipher cipher = Cipher.getInstance(AES_ALGORITHM_SPEC, "BC");
                IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
                byte[] decryptedDataBytes = cipher.doFinal(decodedInputDataBytes, 0, decodedInputDataBytes.length);
                String decryptedString = new String(decryptedDataBytes, "UTF-8");
                return decryptedString;
            } catch (Exception e) {
                Utils.Log.e("BSTP_AESCryptor_decryptData", e.getMessage());
            }
            return null;
        }
    }

    public static class KeyStoreManager {

        private static final String RSA_KEY_ALIAS = "APLM_RSA_KEY";
        private static final String AES_KEY_ALIAS = "APLM_AES_KEY";
        private static final String KEY_STORE_ALIAS = "AndroidKeyStore";
        private static SecretKeyManager secretKeyManager = null;

        //get secret key
        public static SecretKey getSecretKey() {

            if (secretKeyManager == null) {
                secretKeyManager = new SecretKeyManager(AES_KEY_ALIAS);
                secretKeyManager.generateOrRestoreSecretKey();
            }
            return secretKeyManager.getSecretKey();
        }
    }

    public static class SecretKeyManager {
        private SecretKey secretKey = null;
        private String aesKey = null;
        private static final byte[] aesSeedData = {0x75, 0x55, 0x34, 0x23, 0x62, 0x02, 0x26, 0x37, 0x49, 0x22, 0x58, 0x67, 0x45, 0x29, 0x43, 0x50};

        public SecretKeyManager(String aesKey) {
            this.aesKey = aesKey;
        }

        public SecretKey getSecretKey() {
            if (secretKey == null) {
                generateOrRestoreSecretKey();
            }
            return secretKey;
        }

        public void generateOrRestoreSecretKey() {
            try {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null);
                this.secretKey = (SecretKey) keyStore.getKey(this.aesKey, null);
                if (this.secretKey == null) {
                    SecretKey key = new SecretKeySpec(aesSeedData, "AES");
                    KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(key);
                    keyStore.setEntry(this.aesKey, skEntry, null);
                    this.secretKey = key;
                }
            } catch (Exception e) {
                Utils.Log.e("BSTP_SecretKeyManager_generateOrRestoreSecretKey", e.getMessage());
            }
        }
    }

}
