package br.com.gabrieldani.mylibrary;

import android.annotation.SuppressLint;
import android.os.Build;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class CryptoHelper {

    // Chave para criptografia AES
    private static final String AES_KEY = "mySecretKey12345";

    // Método para criptografar uma string
    public static String encrypt(String value) {
        try {
            // Cria uma instância do algoritmo de criptografia AES
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Cria uma chave secreta usando a chave AES_KEY
            Key secretKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");

            // Inicializa o Cipher para modo de criptografia
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Criptografa a string e a converte para Base64
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(encryptedBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para descriptografar uma string
    public static String decrypt(String encryptedValue) {
        try {
            // Cria uma instância do algoritmo de criptografia AES
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Cria uma chave secreta usando a chave AES_KEY
            Key secretKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");

            // Inicializa o Cipher para modo de descriptografia
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Decodifica a string criptografada de Base64
            byte[] encryptedBytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                encryptedBytes = Base64.getDecoder().decode(encryptedValue);
            }

            // Descriptografa os bytes e converte em uma string
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

