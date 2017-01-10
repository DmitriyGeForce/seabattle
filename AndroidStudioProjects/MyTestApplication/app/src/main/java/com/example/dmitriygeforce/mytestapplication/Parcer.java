package com.example.dmitriygeforce.mytestapplication;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by dmitriygeforce on 10.01.17.
 */

public class Parcer {

    public static String get(String path) {
        StringBuilder builder = new StringBuilder();
        try {
            Process exec = Runtime.getRuntime().exec("whois stackoverfl.com");

            InputStream stdin = exec.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);

            String line = null;

            while ((line = br.readLine()) != null) {
                builder.append(line);
            }

            Log.e("Parce", builder.toString());
        } catch (IOException ex) {
            Log.e("Parce", ex.getMessage());
            Log.e("Parce", String.valueOf(ex.getCause()));
        }
        return builder.toString();
    }
}
