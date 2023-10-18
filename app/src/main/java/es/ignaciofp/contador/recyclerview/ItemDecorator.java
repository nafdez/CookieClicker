package es.ignaciofp.contador.recyclerview;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDecorator extends RecyclerView.ItemDecoration {

    private final int topSpaceHeight;
    private final int bottomSpaceHeight;
    private final int leftSpaceHeight;
    private final int rightSpaceHeight;

    public ItemDecorator(int topSpaceHeight, int bottomSpaceHeight, int leftSpaceHeight, int rightSpaceHeight) {
        this.topSpaceHeight = topSpaceHeight;
        this.bottomSpaceHeight = bottomSpaceHeight;
        this.leftSpaceHeight = leftSpaceHeight;
        this.rightSpaceHeight = rightSpaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = topSpaceHeight;
        outRect.bottom = bottomSpaceHeight;
        outRect.left = leftSpaceHeight;
        outRect.right = rightSpaceHeight;
    }
}
