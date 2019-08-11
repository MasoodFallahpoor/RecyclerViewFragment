package ir.fallahpoor.recyclerviewfragment.viewstate;

import java.util.List;

public class DataLoadedState<T> implements ViewState {

    private List<T> data;

    public DataLoadedState(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
