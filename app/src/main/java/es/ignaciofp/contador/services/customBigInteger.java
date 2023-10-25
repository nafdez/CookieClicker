package es.ignaciofp.contador.services;

import androidx.annotation.NonNull;

import java.math.BigInteger;

public class customBigInteger extends BigInteger {

    public customBigInteger(@NonNull String val) {
        super(val);
    }
}
