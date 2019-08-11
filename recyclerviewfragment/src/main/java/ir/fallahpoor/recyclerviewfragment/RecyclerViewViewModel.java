package ir.fallahpoor.recyclerviewfragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import ir.fallahpoor.recyclerviewfragment.viewstate.ViewState;

class RecyclerViewViewModel<T> extends ViewModel {

    private DataProvider<T> dataProvider;

    RecyclerViewViewModel(DataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    void getData() {
        dataProvider.getData();
    }

    void getMoreData() {
        dataProvider.getMoreData();
    }

    LiveData<ViewState> getViewStateLiveData() {
        return dataProvider.getDataLiveData();
    }

}
