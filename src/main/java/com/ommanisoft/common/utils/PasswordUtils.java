package com.ommanisoft.common.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class PasswordUtils {
  private static final Random RANDOM = new SecureRandom();
  private static final String ALPHABET =
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final int ITERATIONS = 10000;
  private static final int KEY_LENGTH = 256;

  private static byte[] hash(char[] password) {
    String salt = "EqdmPh53c9x33EygXpTpcoJvc4VXLK";
    PBEKeySpec spec = new PBEKeySpec(password, salt.getBytes(), ITERATIONS, KEY_LENGTH);
    Arrays.fill(password, Character.MIN_VALUE);
    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      return skf.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
    } finally {
      spec.clearPassword();
    }
  }

  public static String encode(String password) {
    String returnValue = null;

    byte[] securePassword = hash(password.toCharArray());

    returnValue = Base64.getEncoder().encodeToString(securePassword);

    return returnValue;
  }

  public static boolean verifyPassword(String providedPassword, String securedPassword) {
    boolean returnValue = false;

    String newSecurePassword = encode(providedPassword);

    returnValue = newSecurePassword.equalsIgnoreCase(securedPassword);

    return returnValue;
  }
}
