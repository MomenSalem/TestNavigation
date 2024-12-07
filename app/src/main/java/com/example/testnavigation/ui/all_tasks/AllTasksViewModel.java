package com.example.testnavigation.ui.all_tasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllTasksViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AllTasksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is All Tasks fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}