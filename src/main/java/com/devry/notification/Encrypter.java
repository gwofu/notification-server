package com.devry.notification;

import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


public class Encrypter {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    
    public String encrypt(String secret, String data) {
        return encode(secret, data, HMAC_SHA256_ALGORITHM);
    }
    
    /**
     * Computes RFC 2104-compliant HMAC-SHA1 signature.
     * 
     * @param key The signing key.
     * @param data The data to be signed.
     * @return String The Base64-encoded RFC 2104-compliant HMAC signature.
     * @throws RuntimeException
     */
    public String encryptSha1(String secret, String data) {
        return encode(secret, data, HMAC_SHA1_ALGORITHM);
    }

    public String encode(final String secret, final String data, final String algorithm) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), algorithm);

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(algorithm);
            mac.init(signingKey);

            // base64-encode the hmac
            return Base64.encodeBase64String(mac.doFinal(data.getBytes()));

        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Use SHA-256 algorithm to generate almost-unique, fixed size 256-bit (32-byte) hash.
     * Hash is a one way function – it cannot be decrypted back.
     * @param message
     * @return Hash string
     * @throws Exception
     */
    public String generateSHA256(String message)
        throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
 
        return convertByteArrayToHexString(hashedBytes);
    }

    private String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}
