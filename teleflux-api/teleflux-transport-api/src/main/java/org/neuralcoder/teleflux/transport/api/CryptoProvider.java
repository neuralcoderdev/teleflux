package org.neuralcoder.teleflux.transport.api;

public interface CryptoProvider {
    byte[] rng(int length);

    /** AES cipher as required by the protocol (e.g., IGE/CTR). */
    byte[] aesEncrypt(byte[] data, byte[] key, byte[] iv);
    byte[] aesDecrypt(byte[] data, byte[] key, byte[] iv);

    /** RSA public-key encryption (PKCS#1 v1.5 or OAEP depending on impl). */
    byte[] rsaEncrypt(byte[] data, byte[] publicKeyDer);

    /** Diffie–Hellman primitive (domain params handled by impl). */
    byte[] dh(byte[] g, byte[] p, byte[] a);
}
