package com.example.localsocketsdemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;


public class MainActivity extends AppCompatActivity {
    private Socket socket;
    TextView textView;
    Button btn;
    OkHttpClient client;
    CallBack callBack;
    static Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        Button btn_runServer = findViewById(R.id.btn_runserver);
        Button btn_connect = findViewById(R.id.btn_connect);
        client = new OkHttpClient();
        callBack = text -> {
            textView.setText(text);
        };

        btn_runServer.setOnClickListener(view -> {
            CompletableFuture.runAsync(() -> {
                try {
                    runServer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        });

        btn_connect.setOnClickListener(view -> {
            try {
                connectToNetwork();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public boolean connectToNetwork() throws IOException {
        InetAddress ipAddress = getDeviceIpAddress();
        assert ipAddress != null;
        String[] arr = ipAddress.getHostAddress().split("\\.");
        String network = arr[0] + "." + arr[1] + "." + arr[2] + ".";
        for (int i = 1; i <= 255; i++) {
            String ip = network + i;
            try {

                Request request = new Request.Builder().url("http://" + ip + ":9000").build();
                WebSocketListener listener = new WebSocketListener(callBack);
                WebSocket webSocket = client.newWebSocket(request, listener);
                Thread.sleep(300);
                if (connected) {
                    Log.e("msg", "connected successfully to :" + ip);
                    break;
                }
            } catch (Exception e) {
                Log.e("msg", "error :" + e.getMessage());
            }
        }
        Log.e("msg", "failed to connect to each network , check again");
        return false;
    }

    public void runServer() throws IOException {
        InetAddress hotspotIp = getDeviceIpAddress();
        if (hotspotIp != null) {
            ServerSocket serverSocket = new ServerSocket(9000, 0, hotspotIp);
            textView.setText("Waiting for client...");
            socket = serverSocket.accept();
            textView.setText("new client Connected");
        } else {
            textView.setText("enable hotspot first");
        }
    }

    public static InetAddress getDeviceIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                for (InetAddress addr : Collections.list(networkInterface.getInetAddresses())) {
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        Log.e("msg", "address :" + addr.getHostAddress() + " , interface :" + networkInterface.getDisplayName());
                        return addr;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}