package com.redgalaxy.mbdf;

public class KeyValuePair {
    public final String key;
    public final IMBDFType value;

    public KeyValuePair(String key, IMBDFType value) {
        this.key = key;
        this.value = value;
    }

    public String toString(){
        return key+": "+value.getValue();
    }

    public Binary getBinaryKeyValueEntry() {
        Binary keyBinary = new MBDFString(key).encodeBinaryRaw();
        Binary valueBinary = value.encodeBinary();
        return keyBinary.concat(valueBinary);
    }
}
