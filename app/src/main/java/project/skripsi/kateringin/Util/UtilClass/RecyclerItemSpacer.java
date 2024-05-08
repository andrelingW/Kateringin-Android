package project.skripsi.kateringin.Util.UtilClass;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemSpacer extends RecyclerView.ItemDecoration {
    private final int space;

    public RecyclerItemSpacer(Context context, int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        int columnCount = 2;
        int columnIndex = position % columnCount;
        int spacing = space / (columnCount + 1);
        outRect.left = spacing - columnIndex * spacing / columnCount;
        outRect.right = (columnIndex + 1) * spacing / columnCount;
        outRect.top = spacing;
        if (isLastRow(position, columnCount, parent)) {
            outRect.bottom = spacing;
        } else {
            outRect.bottom = 0;
        }
    }
    private boolean isLastRow(int position, int columnCount, RecyclerView parent) {
        int itemCount = parent.getAdapter().getItemCount();
        int rowCount = itemCount / columnCount + (itemCount % columnCount == 0 ? 0 : 1);
        int currentRow = position / columnCount + 1;
        return currentRow == rowCount;
    }
}
