package com.app.qrteachernavigation.ui.qrScanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QrScannerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public QrScannerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is qr fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}