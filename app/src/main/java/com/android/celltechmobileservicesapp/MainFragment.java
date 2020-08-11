package com.android.celltechmobileservicesapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.celltechmobileservicesapp.actions.FactoryResetAction;
import com.android.celltechmobileservicesapp.json.Algorithm;
import com.android.celltechmobileservicesapp.json.InJsonModel;
import com.android.celltechmobileservicesapp.json.OutJsonModel;
import com.android.celltechmobileservicesapp.json.ParseInJson;
import com.android.celltechmobileservicesapp.json.StartJsonModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

//https://github.com/ivorycirrus/Android2PC

public class MainFragment extends Fragment {
    static String TAG = MainFragment.class.getSimpleName();
    View mFragmentContainer;
    TextView mLogList;
    Thread mainTask;
    List<Algorithm> listAlgsResults = new ArrayList<>();

    Socket socket;
    USBStateReceiver mUsbAttachReceiver = new USBStateReceiver();


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        mFragmentContainer = inflater.inflate(R.layout.wipe_main_fragment, null);
        mLogList = mFragmentContainer.findViewById(R.id.text_field);
        mLogList.setMovementMethod(new ScrollingMovementMethod());
        mLogList.setSelected(true);

        return mFragmentContainer;

    }

    public boolean isUSBConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        return intent.getExtras().getBoolean("connected");
    }

    @Override
    public void onResume() {
        super.onResume();
        showMessageInLogsList("initializare communicare - onresume", true);
        initializeCommunication();
    }

    public void initializeCommunication() {
        clearMessageInLogsList();
        showMessageInLogsList(Utils.getLogFromFile(), false);
        //can't send message if USB cable not connected
        if (isUSBConnected(getActivity()) && (Constants.processStatus == 1 || Constants.processStatus == 3)) {
            mainTask = new Thread(new OpenSocketRunnable());
            mainTask.start();
        } else {
            if (!isUSBConnected(getActivity())) {
                showMessageInLogsList("Va rugam sa conectati telefonul prin USB la app windows", true);
                IntentFilter filter = new IntentFilter("android.hardware.usb.action.USB_STATE");
                getActivity().registerReceiver(mUsbAttachReceiver, filter);
            }
        }
    }

    private PrintWriter output;
    private BufferedReader input;
    long backoff = 2000;
    int MAX_ATTEMPTS = 5;

    class OpenSocketRunnable implements Runnable {
        public void run() {
            try {
                boolean connSucces = false;
                backoff = 2000;
                for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress("localHost", Constants.socketPort), 10000);
                        connSucces = true;
                    } catch (IOException e) {
                        if (i == MAX_ATTEMPTS) {
                            showMessageInLogsList("Conexiunea la server a esuat, dupa " + MAX_ATTEMPTS + " incercari", true);
                            break;
                        }
                        try {
                            showMessageInLogsList("Conexiunea a esuat. Se asteapta " + backoff + " ms inainte de reincercare", true);
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            showMessageInLogsList("Conexiunea a esuat. Nu se mai incearca conexiunea al server.", true);
                            Thread.currentThread().interrupt();
                            break;
                        }
                        backoff *= 2;
                    }
                }

                if (connSucces) {
                    output = new PrintWriter(socket.getOutputStream(), false);
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    //init comunication with json start
                    if (Constants.processStatus == 1) {
                        showMessageInLogsList("trimit start json (status 1)", true);
                        Constants.processStatus = 2; //waiting action from server
                        new Thread(new SendingMessageThread(new StartJsonModel(mFragmentContainer.getContext()).createJson())).start();
                    }

                    //send results as json
                    if (Constants.processStatus == 3) {
                        showMessageInLogsList("trimit json cu rezultate (status 3)", true);
                        new Thread(new SendingMessageThread(new OutJsonModel(mFragmentContainer.getContext(), "wipe_data", listAlgsResults).createJson())).start();
                        Constants.processStatus = 4; //all done
                    }

                    new Thread(new ReceivingThread()).start();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                showMessageInLogsList("Socket UnknownHostException", true);
            } catch (IOException e) {
                e.printStackTrace();
                showMessageInLogsList("Socket IOException", true);
            }
        }
    }

    class ReceivingThread implements Runnable {
        @Override
        public void run() {
            if (socket != null) {
                while (socket.isConnected()) {
                    try {
                        String message = "";

                        StringBuilder sb = new StringBuilder();
                        try {
                            BufferedReader buffer = new BufferedReader(input);
                            int r;
                            while ((r = buffer.read()) != -1) {
                                char c = (char) r;
                                sb.append(c);
                                if (sb.toString().endsWith(Constants.StringTerminatorForMessages)) { // delimitator pentru mesaje
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            //e.printStackTrace();
                        }
                        message = sb.toString();

                        if (!"".equals(message)) {
                            handleMessageAndDecideAction(message);
                        } else {
                            //showMessageInLogsList("initializare communicare - receivingthread no message");
                            //initializeCommunication();
                            return;
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            } else {
                showMessageInLogsList("Eroare socket, reinitializare comunicatie", true);
                initializeCommunication();
            }
        }
    }

    private void handleMessageAndDecideAction(String message) {
        showMessageInLogsList("mesaj primit: " + message + "\n", true);

        //avoid json invalidation because of terminator
        if (message.endsWith(Constants.StringTerminatorForMessages)) {
            message = message.replace(Constants.StringTerminatorForMessages, "");
        }

        try {
            JSONObject jsonObj = new JSONObject(message);

            if ("factory_reset".equals(jsonObj.get("action"))) {
                showMessageInLogsList("start factory reset", true);

                //send message with FR started
                new Thread(new SendingMessageThread(new OutJsonModel(mFragmentContainer.getContext(), "factory_reset").createJson())).start();

                //start factory reset
                FactoryResetAction.finalizeFactoryResetWithoutAlert((MainActivity) getActivity());
            }

            if ("data_wipe".equals(jsonObj.get("action"))) {
                showMessageInLogsList("start data wipe", true);
                //start wipe data
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = null;

                String title = "WIPE DETAIL";
                InJsonModel modelIn = ParseInJson.parseJson(jsonObj.toString());
                fragment = TestWipeFragment.newInstance(modelIn, "MainFragment");
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_right);
                transaction.addToBackStack(title).replace(R.id.content_frame, fragment, title).commit();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    class SendingMessageThread implements Runnable {
        private String message;

        SendingMessageThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            output.write(message + "\n");
            output.flush();
            showMessageInLogsList("mesaj trimis: " + message + "\n", true);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
            getActivity().unregisterReceiver(mUsbAttachReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(mUsbAttachReceiver);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    public void showMessageInLogsList(final String message, boolean saveinfile) {
        try {
            (getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLogList.append("\n" + message);
                }
            });
            if (saveinfile){ // && Constants.TestsSpaceDivider != 1) { //save just when testing
                Utils.appendLogFile(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearMessageInLogsList() {
        try {
            (getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLogList.setText("");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class USBStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.hardware.usb.action.USB_STATE".equals(action)) {
                if (intent.getExtras().getBoolean("connected")) {
                    // USB was connected
                    showMessageInLogsList("cablu USB conectat", true);
                    initializeCommunication();
                } else {
                    showMessageInLogsList("cablu USB deconectat", true);
                    // USB was disconnected
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.reload:
                Constants.processStatus = 1;
                initializeCommunication();
                Utils.clearFileContent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


//    String total = "{\n" +
//            " \"action\": \"data_wipe\", \n" +
//            "   \"params\": {\n" +
//            "   \"algorithms\": [\n" +
//            "     {\"name\": \"HMG5\", \n" +
//            "      \"run\" : \"off\"  },\n" +
//            "     {\"name\": \"NIST\", \n" +
//            "      \"run\" : \"off\"  },\n" +
//            "     {\"name\": \"DOD\", \n" +
//            "      \"run\" : \"off\"  },\n" +
//            "     {\"name\": \"NCSC\", \n" +
//            "      \"run\" : \"off\"  },\n" +
//            "     {\"name\": \"BSI\", \n" +
//            "      \"run\" : \"off\"  },\n" +
//            "     {\"name\": \"Rand\", \n" +
//            "      \"run\" : \"on\"  }\n" +
//            "    ]\n" +
//            " } ,       \n" +
//            " \"error\": \"\",\n" +
//            " \"success\": \"\",\n" +
//            " \"info\": \"\"\n" +
//            "}\n";
//    JSONObject jsonObj = null;
//                try {
//                        jsonObj = new JSONObject(total);
//                        } catch (JSONException e) {
//                        e.printStackTrace();
//                        }

