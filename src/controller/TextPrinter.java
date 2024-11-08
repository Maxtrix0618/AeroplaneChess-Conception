package controller;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.io.IOException;
import java.io.PrintWriter;

public class TextPrinter {
    private static PrintWriter Log;

    public static void initialize() {
        try {
            Log = new PrintWriter("./resource/data/Log.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void reinitialize() {
        In log = new In("./resource/data/Log.txt");
        String text = log.readAll();
        try {
            Log = new PrintWriter("./resource/data/Log.txt");
            Log.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void terminate() {
        Log.close();
    }

    public static String read() {
        terminate();
        In log = new In("./resource/data/Log.txt");
        String text = log.readAll();
        reinitialize();
        return text;
    }

    public static void print(String text) {
        StdOut.print(text);
        Log.write(text);
    }


    public static void println() {
        print("\n");
    }
    public static void println(String text) {
        print(text);
        println();
    }







}
