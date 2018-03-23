package com.redgalaxy.mbdf;

public interface IMBDFType<T> {
    MBDFBinary encode();
    Binary encodeBinary();
    T getValue();
}
