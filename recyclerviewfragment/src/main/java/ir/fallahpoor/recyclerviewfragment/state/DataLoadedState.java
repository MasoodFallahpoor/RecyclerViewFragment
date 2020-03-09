package ir.fallahpoor.recyclerviewfragment.state;

import java.util.List;

public class DataLoadedState<T> implements State<T> {

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
