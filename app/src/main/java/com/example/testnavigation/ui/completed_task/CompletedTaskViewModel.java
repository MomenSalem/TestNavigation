package com.example.testnavigation.ui.completed_task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CompletedTaskViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CompletedTaskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Completed fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}