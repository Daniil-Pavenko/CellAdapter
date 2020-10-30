package io.techery.celladapter;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param <ITEM> is yours POJO model
 */
public class CellAdapter<ITEM> extends RecyclerView.Adapter<Cell> {

    /**
     * Map<model, Cell child for introducing model></>
     */
    private final Map<Class, Class<? extends Cell>> itemCellMap = new HashMap<>();
    private final List<Class> viewTypes = new ArrayList<>();
    private final SparseArray<Cell.Listener> typeListenerMapping = new SparseArray<>();
    protected List<ITEM> items = new ArrayList<>();
    protected RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    protected SparseArrayCompat<Parcelable> recyclerStates = new SparseArrayCompat<>();

    private static final String TAG = "CellAdapter";

    private LayoutInflater layoutInflater;
    private Context context;

    public CellAdapter(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void registerCell(Class<?> itemClass,
                             Class<? extends Cell> cellClass) {
        registerCell(itemClass, cellClass, null);
    }

    public <ITEM_CLASS> void registerCell(Class<? extends ITEM_CLASS> itemClass,
                                          Class<? extends Cell> cellClass,
                                          @Nullable Cell.Listener<?> cellListener) {
        itemCellMap.put(itemClass, cellClass);
        int type = viewTypes.indexOf(itemClass);
        if (type == -1) {
            viewTypes.add(itemClass);
        }
        registerListener(itemClass, cellListener);
    }

    private <ITEM_CLASS> void registerListener(Class<? extends ITEM_CLASS> itemClass,
                                               @Nullable Cell.Listener<?> cellListener) {
        int index = viewTypes.indexOf(itemClass);
        if (index < 0)
            throw new IllegalStateException(itemClass.getSimpleName() + " is not registered as Cell");
        typeListenerMapping.put(index, cellListener);
    }

    @Override
    public Cell onCreateViewHolder(ViewGroup parent, int viewType) {
        Class itemClass = viewTypes.get(viewType);
        Class<? extends Cell> cellClass = itemCellMap.get(itemClass);
        Cell cell = buildCell(cellClass, parent);
        cell.setCellDelegate(typeListenerMapping.get(viewType));
        setupNestedRecyclerView(cell);
        return cell;
    }

    protected void setupNestedRecyclerView(Cell cell) {
        if (cell.onNestedRecyclerView() != null) {
            cell.onNestedRecyclerView().setRecycledViewPool(recycledViewPool);
        }
    }

    private Cell buildCell(Class<? extends Cell> cellClass, ViewGroup parent) {
        int layoutResId;
        if (cellClass.getAnnotation(Layout.class) != null) {
            Layout layoutAnnotation = cellClass.getAnnotation(Layout.class);
            layoutResId = layoutAnnotation.value();
        } else if (cellClass.getAnnotation(LayoutName.class) != null) {
            LayoutName layoutAnnotation = cellClass.getAnnotation(LayoutName.class);
            layoutResId = context.getResources().getIdentifier(layoutAnnotation.value(), "layout", context.getPackageName());
        } else {
            throw new IllegalArgumentException("Please annotate your class for declare layout res id");
        }

        View cellView = layoutInflater.inflate(layoutResId, parent, false);
        RecyclerView.ViewHolder cellObject = null;
        try {
            Constructor<? extends RecyclerView.ViewHolder> constructor = cellClass.getConstructor(View.class);
            cellObject = constructor.newInstance(cellView);
        } catch (Exception e) {
            Log.e("CellAdapter", "Can't create cell: " + e.getMessage());
        }
        return (Cell) cellObject;
    }

    @Override
    public void onBindViewHolder(Cell cell, int position) {
        ITEM item = getItem(position);
        cell.prepareForReuse();
        cell.fillWithItem(item);
        if (cell.onNestedRecyclerView() != null) {
            cell.onNestedRecyclerView().getLayoutManager().onRestoreInstanceState(recyclerStates.get(position));
        }
    }

    @Override
    public void onViewRecycled(@NonNull Cell holder) {
        if (holder.onNestedRecyclerView() != null) {
            recyclerStates.append(holder.getAdapterPosition(), holder.onNestedRecyclerView().getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public int getClassItemViewType(Class<?> itemClass) {
        int index = viewTypes.indexOf(itemClass);
        if (index < 0) {
            throw new IllegalArgumentException(itemClass.getSimpleName() + " is not registered");
        }
        return index;
    }

    @Override
    public int getItemViewType(int position) {
        ITEM ITEM = items.get(position);
        try {
            Class itemClass = ITEM.getClass();
            return getClassItemViewType(itemClass);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "getItemViewType: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            Log.e(TAG, "getItemCount() items is null");
            return 0;
        }
    }

    @Nullable
    public ITEM getItem(int position) {
        if (items != null) {
            return items.get(position);
        } else {
            return null;
        }
    }

    @Nullable
    public List<ITEM> getItems() {
        return items;
    }

    public void setItems(List<ITEM> items) {
        this.items = items;
    }

    public void clear() {
        if (items != null) {
            items.clear();
            notifyDataSetChanged();
        } else {
            Log.e(TAG, "clear() items is null");
        }
    }
}