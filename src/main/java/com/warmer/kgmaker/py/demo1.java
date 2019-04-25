package com.warmer.kgmaker.py;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 * @author Administrator
 */
public class demo1 {

    public static void main(String[] args) {
        try {
            System.out.println("start");
            String[] args1 = new String[]{"python", "E:\\sentences.py"};
            Process pr = Runtime.getRuntime().exec(args1);

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            pr.waitFor();
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test() {
        System.out.println("我的第一个方法C");
    }




//    package com.warmer.kgmaker;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.ByteBuffer;
//import java.nio.charset.Charset;
//
//public class Demo1 {
//
//    public static void main(String[] args) {
//        try {
//            System.out.println("start");
//            String[] args1 = new String[]{"python", "E:\\PyNeo4j\\sentences.py"};
//            Process pr = Runtime.getRuntime().exec(args1);
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    pr.getInputStream(),"GBK"));
//            String line;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//            }
//            in.close();
//            pr.waitFor();
//            System.out.println("结束");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void test() {
//        System.out.println("我的第一个程序C");
//    }
//}
}