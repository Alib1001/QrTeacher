package com.app.qrteachernavigation.ui.schedule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScheduleListViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ScheduleListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ScheduleList fragment");

    }

    public LiveData<String> getText() {
        return mText;
    }
}