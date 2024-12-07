package com.example.testnavigation.ui.new_task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewTaskViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NewTaskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is new Task fragment");

        // Hi Momen Salem
    }

    public LiveData<String> getText() {
        return mText;
    }
}