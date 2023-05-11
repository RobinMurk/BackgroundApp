package com.example.backgroundapp;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
public class TasutapildiMuutja {


    /**
     * Klass muudab faili "user32.dll" et muuta arvuti taustapilti
     */

    public static interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
        boolean SystemParametersInfo (int esimene, int teine, String pildiTee ,int kolmas); //meetod mis muudab "user32.dll" sisu
    }
    public static void muudatTaustakat(String tee){
        User32.INSTANCE.SystemParametersInfo(0x0014, 0, tee , 1);
    }

}
