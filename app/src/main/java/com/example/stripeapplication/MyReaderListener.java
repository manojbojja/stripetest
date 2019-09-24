package com.example.stripeapplication;

import android.util.Log;

import com.stripe.stripeterminal.callable.ReaderDisplayListener;
import com.stripe.stripeterminal.model.external.ReaderDisplayMessage;
import com.stripe.stripeterminal.model.external.ReaderInputOptions;

import org.jetbrains.annotations.NotNull;

class MyReaderListener implements ReaderDisplayListener {
    @Override
    public void onRequestReaderDisplayMessage(@NotNull ReaderDisplayMessage readerDisplayMessage) {

    }

    @Override
    public void onRequestReaderInput(@NotNull ReaderInputOptions readerInputOptions) {
        Log.d("onRequestReaderInput", "Insert card");
    }
}
