package com.warmer.kgmaker.py;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InvokerByRuntime {
    /**
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        String exe = "python";
        String command = "E:/sentences.py";
        String num1 = "1";
        String num2 = "2";
        String[] cmdArr = new String[] {exe, command, num1, num2};
        Process process = Runtime.getRuntime().exec(cmdArr);
        InputStream is = process.getInputStream();
        DataInputStream dis = new DataInputStream(is);

        String str = dis.readLine();
        byte[] bytes = new byte[1024];
        int read = dis.read(bytes);

        process.waitFor();
        System.out.println("Str:"+str);
        System.out.println("read:"+read);
        System.out.println("is:"+is);
        System.out.println("dis:"+dis.toString());

    }
}
