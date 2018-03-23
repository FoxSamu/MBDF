package com.redgalaxy.mbdf;

import com.sun.tools.javac.util.List;

import java.util.ArrayList;
import java.util.Arrays;

public class TypeArray {

    private ArrayList<IMBDFType> values;

    public TypeArray(){
        values = new ArrayList<>();
    }

    public TypeArray(IMBDFType[] values){
        this.values = new ArrayList<>();
        this.values.addAll(Arrays.asList(values));
    }

    public void add( IMBDFType value ) {
        values.add( value );
    }

    public void remove( int index ) {
        values.remove( index );
    }

    public void add( IMBDFType value, int index ) {
        values.add( index, value );
    }

    public int length() {
        return values.size();
    }

    public IMBDFType get( int index ) {
        return values.get( index );
    }

    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append("[\n");
        for( IMBDFType t : values ) {
            b.append(t.toString());
            b.append("\n");
        }
        b.append("]");
        return b.toString();
    }

}
