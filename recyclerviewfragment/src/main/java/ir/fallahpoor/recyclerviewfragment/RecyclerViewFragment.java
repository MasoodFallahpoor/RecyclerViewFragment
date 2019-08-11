package ir.fallahpoor.recyclerviewfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ir.fallahpoor.recyclerviewfragment.viewstate.DataErrorState;
import ir.fallahpoor.recyclerviewfragment.viewstate.DataLoadedState;
import ir.fallahpoor.recyclerviewfragment.viewstate.MoreDataErrorState;
import ir.fallahpoor.recyclerviewfragment.viewstate.MoreDataLoadedState;

// TODO handle the click event of 'tryAgainButton'.
// TODO handle screen rotation.

public abstract class RecyclerViewFragment<T> extends Fragment {

    public enum State {
        DATA_LOADED,
        MORE_DATA_LOADED,
        DATA_ERROR,
        MORE_DATA_ERROR,
        LOADING,
        LOADING_MORE
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View loadingMoreView;
    private View loadingView;
    private View noDataView;
    private View errorView;

    private RecyclerViewAdapter<T, BaseViewHolder<T>> adapter;
    private RecyclerViewViewModel<T> viewModel;
    private MutableLiveData<State> stateLiveData = new MutableLiveData<>();
    private EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore() {
            setState(State.LOADING_MORE);
            setVisibilityOfLoadingMoreView(View.VISIBLE);
            viewModel.getMoreData();
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getProperLayoutId(), container, false);
        bindAndSetupViews(view);
        return view;
    }

    private int getProperLayoutId() {

        int layoutId = getLayoutId();

        if (layoutId == -1) {
            if (isSwipeToRefreshSupported()) {
                layoutId = R.layout.fragment_recycler_view_1;
            } else {
                layoutId = R.layout.fragment_recycler_view_2;
            }
        }

        return layoutId;

    }

    private void bindAndSetupViews(View view) {

        loadingMoreView = view.findViewById(R.id.loadingMoreView);
        recyclerView = view.findViewById(R.id.recyclerView);
        loadingView = view.findViewById(R.id.loadingView);
        noDataView = view.findViewById(R.id.noDataView);
        errorView = view.findViewById(R.id.errorView);

        loadingView.setVisibility(View.VISIBLE);
        setVisibilityOfLoadingMoreView(View.GONE);
        recyclerView.setVisibility(View.GONE);
        setVisibilityOfNoDataView(View.GONE);
        setVisibilityOfErrorView(View.GONE);

        setupSwipeToRefresh(view);
        setupRecyclerView();

    }

    private void setupSwipeToRefresh(View view) {

        if (isSwipeToRefreshSupported()) {
            swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                endlessScrollListener.reset();
                setState(State.LOADING);
                setVisibilityOfNoDataView(View.GONE);
                setVisibilityOfErrorView(View.GONE);
                viewModel.getData();
            });
        }

    }

    private void setupRecyclerView() {

        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        throwExceptionIfNull(layoutManager, "getLayoutManager");
        recyclerView.setLayoutManager(layoutManager);

        adapter = getAdapter();
        throwExceptionIfNull(adapter, "getAdapter");
        adapter.setListener(getItemClickListener());

        recyclerView.setAdapter(adapter);
        if (isLoadMoreSupported()) {
            recyclerView.addOnScrollListener(endlessScrollListener);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewModel();
        subscribeViewModel();
        loadData();
    }

    private void setupViewModel() {
        RecyclerViewDataProvider<T> recyclerViewDataProvider = getDataProvider();
        throwExceptionIfNull(recyclerViewDataProvider, "getDataProvider");
        ViewModelFactory<T> viewModelFactory = new ViewModelFactory<>(new DataProvider<>(recyclerViewDataProvider));
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecyclerViewViewModel.class);
    }

    private void subscribeViewModel() {
        viewModel.getViewStateLiveData().observe(getViewLifecycleOwner(), viewState -> {
            if (viewState instanceof DataLoadedState) {
                DataLoadedState<T> dataLoadedState = (DataLoadedState<T>) viewState;
                handleDataLoadedState(dataLoadedState);
            } else if (viewState instanceof DataErrorState) {
                handleDataErrorState();
            } else if (viewState instanceof MoreDataLoadedState) {
                MoreDataLoadedState<T> moreDataLoadedState = (MoreDataLoadedState<T>) viewState;
                handleMoreDataLoadedState(moreDataLoadedState);
            } else if (viewState instanceof MoreDataErrorState) {
                handleMoreDataErrorState();
            }
        });
    }

    private void handleDataLoadedState(DataLoadedState<T> dataLoadedState) {

        if (dataLoadedState.getData().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            setVisibilityOfNoDataView(View.VISIBLE);
            setVisibilityOfErrorView(View.GONE);
        } else {
            adapter.setItems(dataLoadedState.getData());
            recyclerView.setVisibility(View.VISIBLE);
            setVisibilityOfNoDataView(View.GONE);
            setVisibilityOfErrorView(View.GONE);
        }

        setState(State.DATA_LOADED);
        hideLoading();

    }

    private void handleMoreDataLoadedState(MoreDataLoadedState<T> moreDataLoadedState) {
        setVisibilityOfLoadingMoreView(View.INVISIBLE);
        setState(State.MORE_DATA_LOADED);
        adapter.addItems(moreDataLoadedState.getData());
    }

    private void handleDataErrorState() {
        setState(State.DATA_ERROR);
        recyclerView.setVisibility(View.GONE);
        setVisibilityOfNoDataView(View.GONE);
        setVisibilityOfErrorView(View.VISIBLE);
        hideLoading();
    }

    private void handleMoreDataErrorState() {
        setVisibilityOfLoadingMoreView(View.INVISIBLE);
        setState(State.MORE_DATA_ERROR);
    }

    private void loadData() {
        setState(State.LOADING);
        showLoading();
        viewModel.getData();
    }

    private void showLoading() {
        recyclerView.setVisibility(View.GONE);
        setVisibilityOfNoDataView(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        if (isSwipeToRefreshSupported()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        loadingView.setVisibility(View.GONE);
    }

    private void throwExceptionIfNull(Object object, String methodName) {
        if (object == null) {
            throw new NullPointerException(methodName + "() should not return null.");
        }
    }

    private void setVisibilityOfLoadingMoreView(int visibility) {

        if (loadingMoreView == null) {
            return;
        }

        if (visibility == View.VISIBLE) {
            if (isLoadMoreSupported()) {
                loadingMoreView.setVisibility(visibility);
            }
        } else {
            loadingMoreView.setVisibility(visibility);
        }

    }

    private void setVisibilityOfNoDataView(int visibility) {

        if (noDataView == null) {
            return;
        }

        if (visibility == View.VISIBLE) {
            if (isNoDataViewSupported()) {
                noDataView.setVisibility(visibility);
            }
        } else {
            noDataView.setVisibility(visibility);
        }

    }

    private void setVisibilityOfErrorView(int visibility) {

        if (errorView == null) {
            return;
        }

        if (visibility == View.VISIBLE) {
            if (isErrorViewSupported()) {
                errorView.setVisibility(visibility);
            }
        } else {
            errorView.setVisibility(visibility);
        }

    }

    private void setState(State state) {
        stateLiveData.setValue(state);
    }

    public LiveData<State> getState() {
        return stateLiveData;
    }

    /**
     * Determines whether {@link RecyclerViewFragment} should support 'Swipe to refresh'.
     * <p>
     * The default value is {@code true}. So if you want to disable 'Swipe to refresh'
     * then override this method and return {@code false}.
     *
     * @return {@code true} if 'Swipe to refresh' should be supported, {@code false} otherwise.
     */
    protected boolean isSwipeToRefreshSupported() {
        return true;
    }

    /**
     * Determines if {@link RecyclerViewFragment} should support 'load more'.
     * <p>
     * The default value is {@code true}. If you want to disable 'load more' then override
     * this method and return {@code false}.
     *
     * @return {@code true} if 'load more' should be supported, {@code false} otherwise.
     */
    protected boolean isLoadMoreSupported() {
        return true;
    }

    /**
     * Determines if {@link RecyclerViewFragment} should display a special View when
     * there is no data available. The default value is {@code true}.
     * <p>
     * NOTE: When this method returns {@code true} and you want to specify a custom layout using
     * {@link RecyclerViewFragment#getLayoutId()} then in your layout contain a View named
     * 'noDataView'.
     *
     * @return {@code true} if 'noDataView' should be supported, {@code false} otherwise.
     */
    protected boolean isNoDataViewSupported() {
        return true;
    }

    /**
     * Determines if {@link RecyclerViewFragment} should display a special View when
     * fetching data fails. The default value is {@code true}.
     * <p>
     * NOTE: When this method returns {@code true} and you want to specify a custom layout using
     * {@link RecyclerViewFragment#getLayoutId()} then in your layout provide a View named
     * 'errorView'.
     *
     * @return {@code true} if 'errorView' should be supported, {@code false} otherwise.
     */
    protected boolean isErrorViewSupported() {
        return true;
    }

    /**
     * Return your implementation of {@link RecyclerViewDataProvider} which is used to populate
     * the RecyclerView of {@link RecyclerViewFragment}.
     *
     * @return an implementation of {@link RecyclerViewDataProvider}
     */
    @NonNull
    protected abstract RecyclerViewDataProvider<T> getDataProvider();

    /**
     * Return your implementation of {@link RecyclerViewAdapter} to use as the adapter of
     * {@link RecyclerViewFragment}'s RecyclerView.
     *
     * @return an instance of {@link RecyclerViewAdapter}
     */
    @NonNull
    protected abstract RecyclerViewAdapter<T, BaseViewHolder<T>> getAdapter();

    /**
     * Determines the LayoutManager to be used as the LayoutManager of
     * {@link RecyclerViewFragment}'s RecyclerView.
     * <p>
     * The default LayoutManager is LinearLayoutManager. If you want a non-default
     * LayoutManager (e.g. GridLayoutManager) then override this method and return
     * your LayoutManager of choice.
     * <p>
     * NOTE: If {@link RecyclerViewFragment#isLoadMoreSupported()} returns {@code true} and this
     * method is overridden be sure to return either a LinearLayoutManager or GridLayoutManager.
     *
     * @return the LayoutManager to be used for {@link RecyclerViewFragment}'s RecyclerView.
     */
    @NonNull
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    /**
     * Specifies the {@link ItemClickListener} to respond to click events of
     * {@link RecyclerViewFragment}'s RecyclerView items.
     * <p>
     * The default {@link ItemClickListener} is {@code null}.
     *
     * @return an instance of {@link ItemClickListener} or {@code null} if no listener is needed.
     */
    @Nullable
    protected ItemClickListener<T> getItemClickListener() {
        return null;
    }

    /**
     * Override this method to specify the id of layout to be used for {@link RecyclerViewFragment}.
     * A default layout is used if this method is not overridden.
     * <p>
     * The layout you provide MUST meet the following requirements:
     * <ul>
     * <li>It MUST (among possibly other Views) contain a RecyclerView named
     * 'recyclerView' and a View named 'loadingView'.</li>
     * <li>If {@link RecyclerViewFragment#isNoDataViewSupported()} returns {@code true} then
     * contain a View named 'noDataView' in your layout.</li>
     * <li>If {@link RecyclerViewFragment#isErrorViewSupported()} returns {@code true} then
     * contain a View named 'errorView' in your layout.</li>
     * <li>When {@link RecyclerViewFragment#isSwipeToRefreshSupported()} is {@code true} then it
     * MUST contain a SwipeRefreshLayout named 'swipeRefreshLayout'.</li>
     * <li>When {@link RecyclerViewFragment#isLoadMoreSupported()} returns {@code true} then
     * it MUST contain a View named 'loadingMoreView'.</li>
     * </ul>
     * <p>
     * NOTE: Failing to satisfy the aforementioned constraints will cause runtime EXCEPTIONS.
     *
     * @return the id of layout to be used for {@link RecyclerViewFragment}.
     */
    @LayoutRes
    protected int getLayoutId() {
        if (isSwipeToRefreshSupported()) {
            return R.layout.fragment_recycler_view_1;
        } else {
            return R.layout.fragment_recycler_view_2;
        }
    }

}