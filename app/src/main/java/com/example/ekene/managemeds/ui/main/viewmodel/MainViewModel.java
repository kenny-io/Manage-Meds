package com.example.ekene.managemeds.ui.main.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import com.example.ekene.managemeds.DB.AppDatabase;
import com.example.ekene.managemeds.data.model.User;

public class MainViewModel extends AndroidViewModel {

    private User userLiveData;
    private AppDatabase appDatabase;

    public MainViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        userLiveData = appDatabase.userDao().getUserById("1");
    }

    //liveData
    public User getUserLiveData() {
        return userLiveData;
    }
}
