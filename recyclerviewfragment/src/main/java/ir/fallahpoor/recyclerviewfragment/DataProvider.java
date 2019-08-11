package ir.fallahpoor.recyclerviewfragment;

import android.os.AsyncTask;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import ir.fallahpoor.recyclerviewfragment.viewstate.DataErrorState;
import ir.fallahpoor.recyclerviewfragment.viewstate.DataLoadedState;
import ir.fallahpoor.recyclerviewfragment.viewstate.MoreDataErrorState;
import ir.fallahpoor.recyclerviewfragment.viewstate.MoreDataLoadedState;
import ir.fallahpoor.recyclerviewfragment.viewstate.ViewState;

class DataProvider<T> {

    private MutableLiveData<ViewState> liveData = new MutableLiveData<>();
    private RecyclerViewDataProvider<T> dataProvider;

    DataProvider(@NonNull RecyclerViewDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    void getData() {
        AsyncTask.execute(() -> {
            List<T> data = dataProvider.getData();
            if (data == null) {
                liveData.postValue(new DataErrorState());
            } else {
                liveData.postValue(new DataLoadedState<>(data));
            }
        });
    }

    void getMoreData() {
        AsyncTask.execute(() -> {
            List<T> data = dataProvider.getMoreData();
            if (data == null) {
                liveData.postValue(new MoreDataErrorState());
            } else {
                liveData.postValue(new MoreDataLoadedState<>(data));
            }
        });
    }

    LiveData<ViewState> getDataLiveData() {
        return liveData;
    }

}