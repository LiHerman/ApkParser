package com.elfso;

import com.common.FileUtil;
import com.common.LogUtil;
import com.elf.excep.FormatException;
import com.elfso.data.ElfFile;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ElfParser {

    private static final String TAG = ElfParser.class.getSimpleName();

    public ElfFile parse(String soFileName) {
        RandomAccessFile racFile = null;
        try {
            racFile = FileUtil.loadAsRAF(soFileName);
            ElfFile elf = new ElfFile();
            elf.parse(racFile);
            return elf;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeQuietly(racFile);
        }
        return null;
    }

    public static void main(String[] args) {

//        String fileName = "parser_elfso/res/libUE4.so";
////        String fileName = "parser_elfso/res/libjiagu.so";
////        String fileName = "parser_elfso/res/libA3AEECD8.so";
//        ElfParser parser = new ElfParser();
//        ElfFile elfFile = parser.parse(fileName);
//        if (elfFile != null) {
//            LogUtil.i(TAG, elfFile.toString());
//        } else {
//            LogUtil.e("Parse failed: " + fileName);
//        }
        ElfParser parser = new ElfParser();
        parser.parserDir("parser_elfso/res/");
    }

    public void parserFile(String fileName) {
        LogUtil.i(TAG, "============="+fileName+"start parse");
        ElfFile elfFile = parse(fileName);
        if (elfFile != null) {
//            LogUtil.i(TAG, elfFile.toString());
            LogUtil.i(TAG, "=============Parse end suc!======================\n");
        } else {
            LogUtil.e("Parse failed: " + fileName);
        }
    }

    public void parserDir(String dirPath) {
        File input = new File(dirPath);
        if(input.isDirectory()) {
            for(File file: input.listFiles()) {
                parserFile(file.getAbsolutePath());
            }
        } else if(input.isFile()) {
            parserFile(dirPath);
        }
    }

//    private static String maskString(String source, int startIndex, int endIndex) {
//        if (source.length() - 1 < startIndex) {
//            return source;
//        }
//        StringBuilder sb = new StringBuilder();
//        char replaceSymbol = '*';
//        for (int i = 0; i < source.length(); i++) {
//            char number = source.charAt(i);
//            if (i >= startIndex - 1 && i < endIndex) {
//                sb.append(replaceSymbol);
//            } else {
//                sb.append(number);
//            }
//        }
//        return sb.toString();
//    }
}
