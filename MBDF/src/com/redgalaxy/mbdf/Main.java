package com.redgalaxy.mbdf;

public class Main {

    public static void main(String[] args) throws Exception {
        TypeCompound compound = new TypeCompound();
        compound.add("string1", new MBDFString("Hello world!"));
        compound.add("string2", new MBDFString("3"));
        compound.add("int1", new MBDFInteger(3));
        compound.add("float1", new MBDFFloat(3.3f));
        compound.add("float2", new MBDFDouble(3.3));
        compound.add("bool1", new MBDFBoolean(false));
        compound.add("bool2", new MBDFBoolean(true));
        compound.add("null1", new MBDFNull());
        TypeCompound c2 = new TypeCompound();
        c2.add("str", new MBDFString("one length compound"));
        TypeCompound c3 = new TypeCompound();
        c3.add("int", new MBDFInteger(23));
        c3.add("xml", new MBDFString("two length compound"));
        compound.add("arr1", new MBDFArray(new IMBDFType[]{
                new MBDFString("Hello world!"),
                new MBDFInteger(3),
                new MBDFString("3"),
                new MBDFFloat(3.29f),
                new MBDFDouble(249.2992),
                new MBDFBoolean(true),
                new MBDFCompound(c2),
                new MBDFBoolean(false),
                new MBDFNull()
        }));
        compound.add("compound1", new MBDFCompound(c3));
        MBDFBinary b = new MBDFCompound( compound ).encode();

        MBDFFile.writeMBDFToFile("/Users/Mac1/Documents/Java/MBDF/resources/file.mbdf", b.makeMBDF(false));

        MBDF mbdf = MBDFFile.readMBDFFromFile("/Users/Mac1/Documents/Java/MBDF/resources/file.mbdf");
        System.out.println(mbdf.getBinaryObject().parse());
    }

}
