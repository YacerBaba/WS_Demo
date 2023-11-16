package com.example.localsocketsdemo;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okio.Buffer;

public class WebSocketListener extends okhttp3.WebSocketListener {

    private final CallBack callBack;

    public WebSocketListener(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);
        log("Connection opened : " + webSocket.request().url());
        MainActivity.connected = true;
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        log("onFailure : " + t.getMessage());
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        super.onMessage(webSocket, text);
        log("Message received : " + text);
        callBack.update(text);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosed(webSocket, code, reason);
        log("On Close : code = " + code + " , reason = " + reason);
    }

    public void log(String str) {
        Log.e("msg", str);
    }
}
