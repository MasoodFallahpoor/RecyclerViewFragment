package ir.fallahpoor.recyclerviewfragment;

import java.util.List;

import androidx.annotation.WorkerThread;

public interface RecyclerViewDataProvider<T> {

    /**
     * {@link RecyclerViewFragment} calls this method to populate its RecyclerView.
     * You should perform your data fetching logic in this method.
     * <p>
     * The returned value of this method is interpreted as follows:
     * <ul>
     * <li>Null: indicates that there is an error and the state of {@link RecyclerViewFragment} will be
     * {@link ir.fallahpoor.recyclerviewfragment.RecyclerViewFragment.State#DATA_ERROR} and errorView is displayed
     * if {@link RecyclerViewFragment#isErrorViewSupported()} returns {@code true}.</li>
     * <li>Empty List: indicates that there is no data. The state of {@link RecyclerViewFragment} will be
     * {@link ir.fallahpoor.recyclerviewfragment.RecyclerViewFragment.State#DATA_LOADED} and noDataView is displayed
     * if {@link RecyclerViewFragment#isNoDataViewSupported()} returns {@code true}.</li>
     * <li>Non-empty List: the RecyclerView of {@link RecyclerViewFragment} is populated and the state will be
     * {@link ir.fallahpoor.recyclerviewfragment.RecyclerViewFragment.State#DATA_LOADED}</li>
     * </ul>
     * <p>
     * NOTE: This method is run on a worker thread.
     *
     * @return List of items or {@code null}.
     */
    @WorkerThread
    List<T> getData();

    /**
     * If {@link RecyclerViewFragment#isLoadMoreSupported()} returns {@code true} then
     * {@link RecyclerViewFragment} calls this method to get more data.
     * <p>
     * The returned value of this method is interpreted as follows:
     * <ul>
     * <li>Null: indicates that there is an error and the state of {@link RecyclerViewFragment} will be
     * {@link ir.fallahpoor.recyclerviewfragment.RecyclerViewFragment.State#MORE_DATA_ERROR}</li>
     * <li>Empty List: indicates that there is no data. The state of {@link RecyclerViewFragment} will be
     * {@link ir.fallahpoor.recyclerviewfragment.RecyclerViewFragment.State#MORE_DATA_LOADED}.</li>
     * <li>Non-empty List: new items are appended to {@link RecyclerViewFragment}'s RecyclerView and the
     * state will be {@link ir.fallahpoor.recyclerviewfragment.RecyclerViewFragment.State#MORE_DATA_LOADED}</li>
     * </ul>
     * <p>
     * NOTE: This method is run on a worker thread.
     *
     * @return List of items or {@code null}.
     */
    @WorkerThread
    List<T> getMoreData();

}
