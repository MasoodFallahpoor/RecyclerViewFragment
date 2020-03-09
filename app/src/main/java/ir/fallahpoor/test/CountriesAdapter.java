package ir.fallahpoor.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import ir.fallahpoor.recyclerviewfragment.BaseViewHolder;
import ir.fallahpoor.recyclerviewfragment.RecyclerViewAdapter;

public class CountriesAdapter extends RecyclerViewAdapter<Country, BaseViewHolder<Country>> {

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new CountryViewHolder(view);
    }

    static class CountryViewHolder extends BaseViewHolder<Country> {

        TextView countryNameTextView;

        CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            countryNameTextView = itemView.findViewById(R.id.countryNameTextView);
        }

        @Override
        public void onBind(Country country) {
            countryNameTextView.setText(country.getName());
        }

    }

}
