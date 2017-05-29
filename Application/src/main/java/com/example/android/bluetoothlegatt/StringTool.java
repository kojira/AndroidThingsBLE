package com.example.android.bluetoothlegatt;

import android.net.ParseException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stringツールクラス
 *
 * @author t.tomita
 * @version 1.0
 */
public class StringTool {
    /**
     * 16文字列を取得
     *
     * @param bin)
     * @return
     */
    public static String hexString(byte[] bin) {
        String s = "";
        int size = bin.length;
        for (int i = 0; i < size; i++) {
            int n = bin[i];
            if (n < 0) {
                n += 256;
            }
            String hex = Integer.toHexString(n);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            s += hex;
        }
        return s;
    }
}
