package com.example.testnavigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DateSeparatorDecoration extends RecyclerView.ItemDecoration {
    private final Paint paint;

    public DateSeparatorDecoration(Context context) {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.red)); // Define the separator color
        paint.setStrokeWidth(15); // Set the thickness of the separator line
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) { // Loop through the children minus one
            View child = parent.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);

            if (viewHolder instanceof TaskAdapter.TaskViewHolder) {
                int position = viewHolder.getAdapterPosition();
                if (position < parent.getAdapter().getItemCount() - 1 && shouldDrawSeparator(position, parent)) {
                    float left = parent.getPaddingLeft();
                    float right = parent.getWidth() - parent.getPaddingRight();
                    float top = child.getBottom() + ((RecyclerView.LayoutParams) child.getLayoutParams()).bottomMargin;
                    float bottom = top + 2; // Height of the separator line

                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }
        }
    }

    private boolean shouldDrawSeparator(int position, RecyclerView parent) {
        TaskAdapter adapter = (TaskAdapter) parent.getAdapter();

        if (adapter == null) {
            return false; // Safety check in case adapter is null
        }

        Task currentTask = adapter.getTask(position);
        Task nextTask = adapter.getTask(position + 1);

        // Extract the date part (before the space) from both tasks
        String currentDate = currentTask.getDueDate().split(" ")[0]; // Extracts "YYYY-MM-DD"
        String nextDate = nextTask.getDueDate().split(" ")[0]; // Extracts "YYYY-MM-DD"

        // Compare the dates
        return !currentDate.equals(nextDate);
    }

}