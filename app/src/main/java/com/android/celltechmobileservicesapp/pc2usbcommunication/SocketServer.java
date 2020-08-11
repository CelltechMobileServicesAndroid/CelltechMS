package com.android.celltechmobileservicesapp.pc2usbcommunication;

import com.android.celltechmobileservicesapp.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private ServerSocket mSocketServer = null;
    BufferedReader input = null;
    PrintWriter output;
    Thread thread1;
    InputStream is;
    OutputStream os;
    Socket socket;

    public SocketServer() {
        thread1 = new Thread(new Thread1());
        thread1.start();
    }

    class Thread1 implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("step 1 connecting...");
                mSocketServer = new ServerSocket(Constants.socketPort);
                while (true) {
                    socket = mSocketServer.accept();

                    is = socket.getInputStream();
                    os = socket.getOutputStream();

                    input = new BufferedReader(new InputStreamReader(is));
                    output = new PrintWriter(os);

                    System.out.println("Connected");

                    new Thread(new Thread2()).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Thread2 implements Runnable {
        @Override
        public void run() {
            while (socket.isConnected()) {
                try {
                    String msg = "";
                    msg = input.readLine();
                    if (msg != null) {
                        System.out.println("client message received:" + msg + "\n");
                        try {
                            //JSONObject jsonObj = new JSONObject(msg);
                            //if ( jsonObj.getInt("status") == 1) {
                            if (msg.contains("serial")){
                                //send factory reset
                                //new Thread(new Thread3("factory_reset")).start();

                                //send wipe
                                new Thread(new Thread3("data_wipe")).start();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        thread1 = new Thread(new Thread1());
                        thread1.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e.getCause());
                }
            }
        }
    }

    class Thread3 implements Runnable {
        private String message;

        Thread3(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            System.out.println("sending to client: " + message + "\n");
            output.write(message + "\n");
            output.flush();
        }
    }


    public static void main(String[] args) {
        SocketServer ob = new SocketServer();
    }

    public String createFactoryResetJson() {
        String str= "";
        try {
            JSONObject dataToSend = new JSONObject();
            dataToSend.put("marca", "");
            dataToSend.put("model", "");
            dataToSend.put("culoare", "");
            dataToSend.put("imei", "");
            dataToSend.put("action", "factory_reset");
            dataToSend.put("params", "");
            dataToSend.put("error", "");
            dataToSend.put("success", "");
            dataToSend.put("info", "");
            str = dataToSend.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String createWipeJson() {
        String str= "";
        try {
            JSONObject dataToSend = new JSONObject();
            dataToSend.put("marca", "");
            dataToSend.put("model", "");
            dataToSend.put("culoare", "");
            dataToSend.put("imei", "");
            dataToSend.put("action", "data_wipe");
            dataToSend.put("params", "");
            dataToSend.put("error", "");
            dataToSend.put("success", "");
            dataToSend.put("info", "");
            str = dataToSend.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}
