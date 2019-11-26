package ir.fallahpoor.recyclerviewfragment;

import android.os.AsyncTask;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import ir.fallahpoor.recyclerviewfragment.state.DataErrorState;
import ir.fallahpoor.recyclerviewfragment.state.DataLoadedState;
import ir.fallahpoor.recyclerviewfragment.state.MoreDataErrorState;
import ir.fallahpoor.recyclerviewfragment.state.MoreDataLoadedState;
import ir.fallahpoor.recyclerviewfragment.state.State;

class DataProvider<T> {

    private MutableLiveData<State> stateLiveData = new MutableLiveData<>();
    private RecyclerViewDataProvider<T> dataProvider;

    DataProvider(@NonNull RecyclerViewDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    void getData() {
        AsyncTask.execute(() -> {
            List<T> data = dataProvider.getData();
            if (data == null) {
                stateLiveData.postValue(new DataErrorState());
            } else {
                stateLiveData.postValue(new DataLoadedState<>(data));
            }
        });
    }

    void getMoreData() {
        AsyncTask.execute(() -> {
            List<T> data = dataProvider.getMoreData();
            if (data == null) {
                stateLiveData.postValue(new MoreDataErrorState());
            } else {
                stateLiveData.postValue(new MoreDataLoadedState<>(data));
            }
        });
    }

    LiveData<State> getStateLiveData() {
        return stateLiveData;
    }

}