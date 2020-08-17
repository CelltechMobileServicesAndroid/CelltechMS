package com.android.celltechmobileservicesapp;

public class Constants {

    public static final long keepMBytesFree = 10;
    public static final long toBytes = 1000000;
    public static int ddBS = 1000000; //4096;

    public static final String StringTerminatorForMessages = "####";
    public final static int socketPort = 59900;
    public static int processStatus = 1;

    //1 : send Json to Win
    //2 : received action Factory Reset or Wipe Data
    //3 : send results of wipe to App Win

    //for tests
    //public static final long keepMBytesFree = 20000;

    public static String SERIAL = "null";
    public static String IMEI = "null";

}
