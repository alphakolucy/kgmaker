package com.warmer.kgmaker.py;

import org.python.util.PythonInterpreter;

import java.util.Properties;

public class sentences {

    public static void main(String[] args) {


         PythonInterpreter interpreter = new PythonInterpreter();


        System.out.println("---------------执行py--------------");
        String fileName = "E:/path.py";
        interpreter.execfile(fileName);




    }

}
