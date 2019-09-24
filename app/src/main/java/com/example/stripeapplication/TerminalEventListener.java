package com.example.stripeapplication;

import android.util.Log;

import com.stripe.stripeterminal.callable.TerminalListener;
import com.stripe.stripeterminal.model.external.ConnectionStatus;
import com.stripe.stripeterminal.model.external.PaymentStatus;
import com.stripe.stripeterminal.model.external.Reader;
import com.stripe.stripeterminal.model.external.ReaderEvent;

import org.jetbrains.annotations.NotNull;

class TerminalEventListener implements TerminalListener {
    @Override
    public void onConnectionStatusChange(@NotNull ConnectionStatus connectionStatus) {
        Log.d("onConnectionStatusChange", connectionStatus.toString());
    }

    @Override
    public void onPaymentStatusChange(@NotNull PaymentStatus paymentStatus) {
        Log.d("onPaymentStatusChange", paymentStatus.toString());
    }

    @Override
    public void onReportLowBatteryWarning() {
        Log.d("onReportLowBatteryWarning", "Battery low");
    }

    @Override
    public void onReportReaderEvent(@NotNull ReaderEvent readerEvent) {
        Log.d("onReportReaderEvent", readerEvent.toString());
    }

    @Override
    public void onUnexpectedReaderDisconnect(@NotNull Reader reader) {
        Log.d("onUnexpectedReaderDisconnect", reader.toString());
    }
}
