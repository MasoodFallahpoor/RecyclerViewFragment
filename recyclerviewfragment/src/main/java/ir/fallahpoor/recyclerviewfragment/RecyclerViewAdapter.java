package ir.fallahpoor.recyclerviewfragment;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerViewAdapter<T, VH extends BaseViewHolder<T>> extends RecyclerView.Adapter<VH> {

    private List<T> items;
    private ItemClickListener<T> listener;

    public RecyclerViewAdapter() {
        items = new ArrayList<>();
    }

    void setItems(List<T> items) {
        if (items == null) {
            return;
        }
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    void setListener(ItemClickListener<T> listener) {
        this.listener = listener;
    }

    void addItems(List<T> items) {
        if (items == null) {
            return;
        }
        this.items.addAll(items);
        notifyItemRangeInserted(this.items.size() - items.size(), items.size());
    }

    @Override
    @NonNull
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(@NonNull VH viewHolder, int position) {
        T item = items.get(position);
        viewHolder.onBind(item);
        if (listener != null) {
            viewHolder.itemView.setOnClickListener(view -> listener.itemClicked(position, item));
        }
    }

    @Override
    public final int getItemCount() {
        return items.size();
    }

}
