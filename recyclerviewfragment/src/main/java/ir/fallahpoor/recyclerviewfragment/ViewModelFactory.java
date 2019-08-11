package ir.fallahpoor.recyclerviewfragment;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory<T> implements ViewModelProvider.Factory {

    private DataProvider<T> dataProvider;

    ViewModelFactory(DataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    @NonNull
    @Override
    public <K extends ViewModel> K create(@NonNull Class<K> viewModelClass) {
        if (viewModelClass.isAssignableFrom(RecyclerViewViewModel.class)) {
            return (K) new RecyclerViewViewModel<>(dataProvider);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
