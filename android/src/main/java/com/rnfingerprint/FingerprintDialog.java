package com.rnfingerprint;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

public class FingerprintDialog extends DialogFragment implements FingerprintHandler.Callback {

    private FingerprintManager.CryptoObject mCryptoObject;
    private DialogResultListener dialogCallback;
    private FingerprintHandler mFingerprintHandler;
    private boolean isAuthInProgress;

    private TextView mFingerprintSensorDescription;

    private String authReason;
    private int authReasonColor = 0;
    private int sensorColor = 0;
    private int sensorErrorColor = 0;
    private int cancelTextColor = 0;
    private String cancelText = "";
    private String sensorDescription = "";
    private String sensorErrorDescription = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mFingerprintHandler = new FingerprintHandler(context, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fingerprint_dialog, container, false);

        final TextView mFingerprintDescription = (TextView) v.findViewById(R.id.fingerprint_description);
        mFingerprintDescription.setText(this.authReason);
        mFingerprintDescription.setTextColor(this.authReasonColor);


        this.mFingerprintSensorDescription = (TextView) v.findViewById(R.id.fingerprint_sensor_description);
        this.mFingerprintSensorDescription.setText(this.sensorDescription);
        this.mFingerprintSensorDescription.setTextColor(this.sensorColor);


        final Button mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setText(this.cancelText);
        mCancelButton.setTextColor(this.cancelTextColor);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelled();
            }
        });

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode != KeyEvent.KEYCODE_BACK || mFingerprintHandler == null) {
                    return false; // pass on to be processed as normal
                }

                onCancelled();
                return true; // pretend we've processed it
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.isAuthInProgress) {
            return;
        }

        this.isAuthInProgress = true;
        this.mFingerprintHandler.startAuth(mCryptoObject);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.isAuthInProgress) {
            this.mFingerprintHandler.endAuth();
            this.isAuthInProgress = false;
        }
    }


    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        this.mCryptoObject = cryptoObject;
    }

    public void setDialogCallback(DialogResultListener newDialogCallback) {
        this.dialogCallback = newDialogCallback;
    }

    public void setReasonForAuthentication(String reason) {
        this.authReason = reason;
    }

    public void setAuthConfig(final ReadableMap config) {
        if (config == null) {
            return;
        }

        if (config.hasKey("cancelText")) {
            this.cancelText = config.getString("cancelText");
        }

        if (config.hasKey("sensorDescription")) {
            this.sensorDescription = config.getString("sensorDescription");
        }

        if (config.hasKey("sensorErrorDescription")) {
            this.sensorErrorDescription = config.getString("sensorErrorDescription");
        }


        if (config.hasKey("authReasonColor")) {
            if(config.getType("authReasonColor") == ReadableType.Number){
                this.authReasonColor = config.getInt("authReasonColor");
            }else if(config.getType("authReasonColor") == ReadableType.String){
                this.authReasonColor = Color.parseColor(config.getString("authReasonColor"));
            }
        }
        if (config.hasKey("sensorColor")) {
            if(config.getType("sensorColor") == ReadableType.Number){
                this.sensorColor = config.getInt("sensorColor");
            }else if(config.getType("sensorColor") == ReadableType.String){
                this.sensorColor = Color.parseColor(config.getString("sensorColor"));
            }
        }

        if (config.hasKey("sensorErrorColor")) {
            if(config.getType("sensorErrorColor") == ReadableType.Number){
                this.sensorErrorColor = config.getInt("sensorErrorColor");
            }else if(config.getType("sensorErrorColor") == ReadableType.String){
                this.sensorErrorColor = Color.parseColor(config.getString("sensorErrorColor"));
            }

        }
        if (config.hasKey("cancelTextColor")) {
            if(config.getType("cancelTextColor") == ReadableType.Number){
                this.cancelTextColor = config.getInt("cancelTextColor");
            }else if(config.getType("cancelTextColor") == ReadableType.String){
                this.cancelTextColor = Color.parseColor(config.getString("cancelTextColor"));
            }
        }
    }

    public interface DialogResultListener {
        void onAuthenticated();

        void onError(String errorString, int errorCode);

        void onCancelled();
    }

    @Override
    public void onAuthenticated() {
        this.isAuthInProgress = false;
        this.dialogCallback.onAuthenticated();
        dismiss();
    }

    @Override
    public void onError(String errorString, int errorCode) {
        if(errorCode == FingerprintAuthConstants.AUTHENTICATION_FAILED){
            this.mFingerprintSensorDescription.setText(this.sensorErrorDescription);
        }else{
            this.mFingerprintSensorDescription.setText(errorString);
        }
        this.mFingerprintSensorDescription.setTextColor(this.sensorErrorColor);
    }

    @Override
    public void onCancelled() {
        this.isAuthInProgress = false;
        this.mFingerprintHandler.endAuth();
        this.dialogCallback.onCancelled();
        dismiss();
    }
}
