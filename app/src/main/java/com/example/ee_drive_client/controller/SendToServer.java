package com.example.ee_drive_client.controller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SendToServer {
    public URL url = new URL("the url");
    public HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
    public SendToServer() throws IOException {

//        conn.setSSLSocketFactory(PinnedPublicKeySocketFactory.createSocketFactory());
//        conn.setDoOutput(true);
//        conn.setDoInput(true);
//        conn.setRequestProperty("Content-Type", "application/json");
//        conn.setRequestProperty("ApiUserName", SoapHeader.VALUE_USERNAME);
//        conn.setRequestProperty("ApiPassword", SoapHeader.VALUE_PASSWORD);
//        conn.setRequestProperty("ApiKey", SoapHeader.VALUE_AUTH);
//        conn.setRequestMethod("POST");
//        conn.setConnectTimeout(1 * 60 * 1000);

    }
    public String send(JSONObject drive) throws IOException {
        StringBuilder respond= new StringBuilder();
        DataOutputStream out = new DataOutputStream(this.conn.getOutputStream());
        out.writeBytes(drive.toString());
        this.conn.connect();

        if (conn.getResponseCode() == 200) {
            InputStream input = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                respond.append(line);
            }
        }

        out.flush();
        out.close();
        return respond.toString();

    }
}
