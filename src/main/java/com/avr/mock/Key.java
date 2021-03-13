package com.avr.mock;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Key {
    private ECKeyPair ecPair;

    public Key() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ecPair = Keys.createEcKeyPair();
    }

    public Key(BigInteger privateKey, BigInteger publicKey){
        ecPair = new ECKeyPair(privateKey, publicKey);
    }

    public String getPrivateKey() {
        return ecPair.getPrivateKey().toString(16);
    }

    public String getPublicKey() {
        return ecPair.getPublicKey().toString(16);
    }
}
