package com.elfso.data;

import com.elfso.stream.ElfStreamer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import sun.nio.cs.UTF_8;

/**
 *
 * Created by xueqiulxq on 07/07/2017.
 */

public class StringTable {

    public String[] strs;
    public byte[] strBytes;

    public static StringTable parseFrom(ElfStreamer s) {
        int len = s.length();
        StringTable strTab = new StringTable();
        strTab.strBytes = new byte[len];
        List<String> tmp = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<len; ++i) {
            byte[] b = s.read(1);
            strTab.strBytes[i] = b[0];
            if (b[0] == 0) {
                tmp.add(builder.toString());
                builder.setLength(0);
            } else {
                builder.append((char) b[0]);
            }
        }
        strTab.strs = new String[tmp.size()];
        tmp.toArray(strTab.strs);
        return strTab;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(strs!=null && strs.length>0) {
            builder.append("String table:\n");
            for (int i=0; i<strs.length; ++i) {
                builder.append(i).append(":").append(strs[i]).append('\n');
            }
            builder.deleteCharAt(builder.length()-1);
        } else {
            builder.append("String table: is empty");
        }
        return builder.toString();
    }

    public String get(int idx) {
        StringBuilder builder = new StringBuilder();
        if (strBytes != null && idx < strBytes.length) {
            for (int i=idx; i<strBytes.length; ++i) {
                if (strBytes[i] != 0) {
                    builder.append((char)strBytes[i]);
                } else {
                    break;
                }
            }
        }
        return builder.toString();
    }
}
