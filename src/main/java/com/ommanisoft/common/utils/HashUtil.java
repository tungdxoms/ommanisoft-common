package com.ommanisoft.common.utils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Formatter;

public class HashUtil {

  private static final String HMAC_SHA256 = "HmacSHA256";
  private static final String HMAC_SHA512 = "HmacSHA512";

  private static final int KEY_LENGTH = 256;
  private static final int ITERATION_COUNT = 65536;
  private static final String salt = "ommani";

  public static String getMD5(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] messageDigest = md.digest(input.getBytes());

      BigInteger number = new BigInteger(1, messageDigest);
      String hashtext = number.toString(16);
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }
      return hashtext;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private static String toHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 2);
    Formatter formatter = new Formatter(sb);
    for (byte b : bytes) {
      formatter.format("%02x", b);
    }
    return sb.toString();
  }

  public static String signHmacSHA256(String data, String secretKey)
    throws NoSuchAlgorithmException, InvalidKeyException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256);
    Mac mac = Mac.getInstance(HMAC_SHA256);
    mac.init(secretKeySpec);
    byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    return toHexString(rawHmac);
  }

  public static String signHmacSHA512(String value, String secret) {
    String result;
    try {
      Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
      SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
      hmacSHA512.init(secretKeySpec);

      byte[] digest = hmacSHA512.doFinal(value.getBytes());
      BigInteger hash = new BigInteger(1, digest);
      result = hash.toString(16);
      if ((result.length() % 2) != 0) {
        result = "0" + result;
      }
    } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException ex) {
      throw new RuntimeException("Problemas calculando HMAC", ex);
    }
    return result;
  }

  public static String aesEncode(String value, String key){
    try {

      SecureRandom secureRandom = new SecureRandom();
      byte[] iv = new byte[16];
      secureRandom.nextBytes(iv);
      IvParameterSpec ivspec = new IvParameterSpec(iv);

      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
      SecretKey tmp = factory.generateSecret(spec);
      SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

      byte[] cipherText = cipher.doFinal(value.getBytes("UTF-8"));
      byte[] encryptedData = new byte[iv.length + cipherText.length];
      System.arraycopy(iv, 0, encryptedData, 0, iv.length);
      System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

      return Base64.getEncoder().encodeToString(encryptedData);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String aesDecode(String hashValue, String key){
    try {

      byte[] encryptedData = Base64.getDecoder().decode(hashValue);
      byte[] iv = new byte[16];
      System.arraycopy(encryptedData, 0, iv, 0, iv.length);
      IvParameterSpec ivspec = new IvParameterSpec(iv);

      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
      SecretKey tmp = factory.generateSecret(spec);
      SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);

      byte[] cipherText = new byte[encryptedData.length - 16];
      System.arraycopy(encryptedData, 16, cipherText, 0, cipherText.length);

      byte[] decryptedText = cipher.doFinal(cipherText);
      return new String(decryptedText, "UTF-8");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
