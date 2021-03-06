package fakecompany.udpsender.camerademo.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by cypressf on 4/6/14.
 * TextReceiver initiates a TCP socket connection to our server and receives
 * text from the server. Upon receiving the text, we will simply log it for now.
 */
public class TextReceiver implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private MainActivity activity;
    public boolean connected;
    private String addressMessage = "this is your unique address:";

    public TextReceiver(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.d(MainActivity.TAG, "Started text receiver");
        try {
            String message;
            socket = connect();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            while(connected) {
                message = in.readLine();
                if (message != null && message.startsWith(addressMessage)) {
                    String address = message.replace(addressMessage, "");
                    activity.ourUniqueAddress = address;
                }
                else if (message != null) {
                    Log.d(MainActivity.TAG, message);
                    activity.speak(message);
                }
                else {
                    disconnect();
                }
            }
        } catch (SocketException e) {
            disconnect();
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();
        }
    }

    private synchronized Socket connect() throws IOException {
            return new Socket(activity.serverAddress, 9999);
    }

    public synchronized void disconnect() {
        connected = false;
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
