package ir.fallahpoor.recyclerviewfragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private static final int THRESHOLD_ROWS = 8;

    private int previousTotalRowsCount;
    private boolean isLoading = true;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

        super.onScrolled(recyclerView, dx, dy);

        int totalRowsCount = getTotalRowsCount(recyclerView.getLayoutManager());
        int lastVisibleRowPosition = getLastVisibleRowPosition(recyclerView.getLayoutManager());

        if (isLoading) {
            if (totalRowsCount > previousTotalRowsCount) {
                previousTotalRowsCount = totalRowsCount;
                isLoading = false;
            }
        }

        if (!isLoading && (totalRowsCount - lastVisibleRowPosition) <= THRESHOLD_ROWS) {
            isLoading = true;
            onLoadMore();
        }

    }

    private int getTotalRowsCount(RecyclerView.LayoutManager layoutManager) {

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            return gridLayoutManager.getItemCount() / gridLayoutManager.getSpanCount();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            return linearLayoutManager.getItemCount();
        } else {
            throw new IllegalArgumentException("LayoutManager MUST either be LinearLayoutManager or GridLayoutManager");
        }

    }

    void reset() {
        previousTotalRowsCount = 0;
        isLoading = true;
    }

    public abstract void onLoadMore();

    private int getLastVisibleRowPosition(RecyclerView.LayoutManager layoutManager) {

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            return gridLayoutManager.findLastVisibleItemPosition() / gridLayoutManager.getSpanCount();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            return linearLayoutManager.findLastVisibleItemPosition();
        } else {
            throw new IllegalArgumentException("LayoutManager MUST either be LinearLayoutManager or GridLayoutManager");
        }

    }

}