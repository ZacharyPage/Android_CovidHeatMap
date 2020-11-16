package com.example.covidheatmap.ui.support;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SupportViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SupportViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("If any assistance is needed, please contact \n" +
                "___________________________________________" +
                "\n\n email@example.com " +
                "\n or \n (123) 456-7890 \n" +
                "___________________________________________");
    }

    public LiveData<String> getText() {
        return mText;
    }
}