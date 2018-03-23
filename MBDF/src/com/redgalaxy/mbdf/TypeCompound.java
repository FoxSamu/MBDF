package com.redgalaxy.mbdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TypeCompound {

    private HashMap<String, IMBDFType> compound;

    public TypeCompound() {
        compound = new HashMap<>();
    }

    public void add(String key, IMBDFType value) {
        compound.putIfAbsent(key, value);
    }

    public void set(String key, IMBDFType value) {
        compound.put(key, value);
    }

    public void remove(String key) {
        compound.remove(key);
    }

    public boolean has(String key) {
        return compound.containsKey(key);
    }

    public IMBDFType get(String key) {
        return compound.get(key);
    }

    public int length() {
        return compound.size();
    }

    public KeyValuePair[] getKeyValuePairs() {
        ArrayList<KeyValuePair> pairs = new ArrayList<>();
        Set<String> keys = compound.keySet();
        for( String key : keys ) {
            pairs.add(new KeyValuePair( key, get(key)));
        }
        return pairs.toArray(new KeyValuePair[keys.size()]);
    }

    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append("{\n");
        for( KeyValuePair t : getKeyValuePairs() ) {
            b.append(t.key);
            b.append(": ");
            b.append(t.value.toString());
            b.append("\n");
        }
        b.append("}");
        return b.toString();
    }

}
