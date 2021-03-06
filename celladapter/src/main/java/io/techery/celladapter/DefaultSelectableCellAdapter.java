package io.techery.celladapter;


import android.content.Context;

import java.util.List;
import java.util.Set;

import io.techery.celladapter.select.SelectableCellAdapter;
import io.techery.celladapter.select.mode.SelectionManager;

public class DefaultSelectableCellAdapter<ITEM> extends SelectableCellAdapter<ITEM> {

    public DefaultSelectableCellAdapter(Context context, SelectionManager selectionManager) {
        super(context, selectionManager);
    }

    public DefaultSelectableCellAdapter(Context context, Set<Integer> selectedPositions, SelectionManager selectionManager) {
        super(context, selectedPositions, selectionManager);
    }

    public void addItem(ITEM item) {
        int position = items.size();
        items.add(item);
        notifyItemInserted(position);
    }

    public void addItem(int position, ITEM item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void addItems(List<ITEM> items) {
        if (items != null) {
            int position = this.items.size();
            this.items.addAll(items);
            notifyItemRangeInserted(position, items.size());
        }
    }

    public void addItems(int position, List<ITEM> items) {
        if (items != null) {
            this.items.addAll(position, items);
            notifyItemRangeInserted(position, items.size());
        }
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        final ITEM item = items.remove(fromPosition);
        items.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void replaceItem(int position, ITEM item) {
        items.set(position, item);
        notifyItemChanged(position);
    }

    public void remove(ITEM item) {
        if (item != null) {
            int position = items.indexOf(item);
            if (position != -1) remove(position);
            notifyItemRemoved(position);
        }
    }

    public void remove(int position) {
        if (items.size() > position) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }
}