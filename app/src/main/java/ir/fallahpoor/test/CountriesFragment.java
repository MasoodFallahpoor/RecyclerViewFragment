package ir.fallahpoor.test;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.fallahpoor.recyclerviewfragment.BaseViewHolder;
import ir.fallahpoor.recyclerviewfragment.ItemClickListener;
import ir.fallahpoor.recyclerviewfragment.RecyclerViewAdapter;
import ir.fallahpoor.recyclerviewfragment.RecyclerViewDataProvider;
import ir.fallahpoor.recyclerviewfragment.RecyclerViewFragment;

public class CountriesFragment extends RecyclerViewFragment<Country> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observeState();
    }

    private void observeState() {
        getState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    logMessage("loading");
                    break;
                case LOADING_MORE:
                    logMessage("loading more");
                    break;
                case DATA_LOADED:
                    logMessage("date loaded");
                    break;
                case MORE_DATA_LOADED:
                    logMessage("more data loaded");
                    break;
                case DATA_ERROR:
                    logMessage("data error");
                    break;
                case MORE_DATA_ERROR:
                    logMessage("more data error");
                    break;
            }
        });
    }

    @NonNull
    @Override
    protected RecyclerViewDataProvider<Country> getDataProvider() {
        return new CountriesDataProvider();
    }

    @NonNull
    @Override
    protected RecyclerViewAdapter<Country, BaseViewHolder<Country>> getAdapter() {
        return new CountriesAdapter();
    }

    @NonNull
    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getActivity(), 2);
    }

    @Nullable
    @Override
    protected ItemClickListener<Country> getItemClickListener() {
        return (int position, Country country) -> logMessage(country.getName());
    }

    private void logMessage(String message) {
        Log.d("@@@@@@", message);
    }

}
