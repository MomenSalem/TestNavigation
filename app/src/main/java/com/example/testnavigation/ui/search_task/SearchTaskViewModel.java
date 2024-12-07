package com.example.testnavigation.ui.search_task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchTaskViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SearchTaskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Search fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}