package ir.fallahpoor.recyclerviewfragment.state;

import java.util.List;

public class MoreDataLoadedState<T> implements State {

    private List<T> data;

    public MoreDataLoadedState(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
