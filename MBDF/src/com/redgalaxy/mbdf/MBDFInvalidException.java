package com.redgalaxy.mbdf;

public class MBDFInvalidException extends Exception {
    public MBDFInvalidException(String type, Binary bin, String msg){
        super("Invalid "+type+" binary: '"+bin+"'. "+msg);
    }
}
