package com.example.testnavigation.ui.today_task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TodayTaskViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TodayTaskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Basheer's today task fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}