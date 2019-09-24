package com.example.stripeapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.stripe.stripeterminal.Terminal;
import com.stripe.stripeterminal.callable.Callback;
import com.stripe.stripeterminal.callable.Cancelable;
import com.stripe.stripeterminal.callable.DiscoveryListener;
import com.stripe.stripeterminal.callable.PaymentIntentCallback;
import com.stripe.stripeterminal.callable.ReaderCallback;
import com.stripe.stripeterminal.log.LogLevel;
import com.stripe.stripeterminal.model.external.ConnectionStatus;
import com.stripe.stripeterminal.model.external.DeviceType;
import com.stripe.stripeterminal.model.external.DiscoveryConfiguration;
import com.stripe.stripeterminal.model.external.PaymentIntent;
import com.stripe.stripeterminal.model.external.PaymentIntentParameters;
import com.stripe.stripeterminal.model.external.Reader;
import com.stripe.stripeterminal.model.external.TerminalException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE_LOCATION = 1;

    private Cancelable discoveryTask;
    private List<? extends Reader> readers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        discoveryTask = null;
        readers = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            // REQUEST_CODE_LOCATION should be defined on your app level
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Location services are required in order to " +
                    "connect to a reader.");
        }
    }

    public void onInitializeClick(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", "Granted");
            try {
                Log.d("Terminal-init", "granting");
                Terminal.initTerminal(getApplicationContext(), LogLevel.VERBOSE, new TokenProvider(),
                        new TerminalEventListener());
            } catch (TerminalException e) {
                Log.d("Terminal-init", "Failed");
                throw new RuntimeException("Location services are required in order to initialize ");
            }

        } else {
            Log.d("Permission", "Not Granted");
            // If we don't have them yet, request them before doing anything else
            final String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_LOCATION);
        }
    }

    public void onDiscoverClick(View view) {
        DiscoveryConfiguration config = new DiscoveryConfiguration(0, DeviceType.CHIPPER_2X, false);

        if(discoveryTask == null && Terminal.getInstance().getConnectedReader() == null) {
            discoveryTask = Terminal.getInstance().discoverReaders(config, new DiscoveryListener() {
                @Override
                public void onUpdateDiscoveredReaders(@NotNull List<? extends Reader> list) {
                    readers = list;
                }
            }, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("discovery", "success");
                }

                @Override
                public void onFailure(@NotNull TerminalException e) {
                    Log.d("discovery", "failure");
                }
            });
        }
    }

    public void onConnectClick(View view) {
        if(Terminal.getInstance().getConnectionStatus() == ConnectionStatus.NOT_CONNECTED) {
            Reader reader = readers.get(0);
            Terminal.getInstance().connectReader(reader, new ReaderCallback() {
                @Override
                public void onSuccess(@NotNull Reader reader) {
                    Log.d("Pair", "Succcess");
                }

                @Override
                public void onFailure(@NotNull TerminalException e) {
                    Log.d("Pair", "Error");
                }
            });
        }
    }

    public void onPaymentClick(View view) {
        PaymentIntentParameters params = new PaymentIntentParameters.Builder()
                .setAmount(100)
                .setCurrency("usd")
                .build();
        Terminal.getInstance().createPaymentIntent(params, new PaymentIntentCallback() {
            @Override
            public void onSuccess(PaymentIntent paymentIntent) {
                Log.d("onSuccess", "Paym intent success");
                // Placeholder for collecting a payment method with paymentIntent
                Cancelable cancelable = Terminal.getInstance().collectPaymentMethod(paymentIntent,
                        new MyReaderListener(),
                        new PaymentIntentCallback() {
                            @Override
                            public void onSuccess(PaymentIntent paymentIntent) {
                                Log.d("onSuccess", "collect paymnet success");
                                Terminal.getInstance().processPayment(paymentIntent,
                                        new PaymentIntentCallback() {
                                            @Override
                                            public void onSuccess(PaymentIntent paymentIntent) {
                                                // Placeholder for notifying your backend to capture paymentIntent.id
                                                Log.d("onSuccess", "process paymnet success");
                                            }

                                            @Override
                                            public void onFailure(TerminalException exception) {
                                                // Placeholder for handling the exception
                                                Log.d("onSuccess", "process paymnet error");
                                            }
                                        });
                            }

                            @Override
                            public void onFailure(TerminalException exception) {
                                // Placeholder for handling exception
                                Log.d("onFailure", "collect paymnet error");
                            }
                        });
            }

            @Override
            public void onFailure(TerminalException exception) {
                Log.d("onFailure", "Paym intent fials");
            }
        });
    }
}
