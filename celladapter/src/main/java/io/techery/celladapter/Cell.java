package io.techery.celladapter;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class Cell<ITEM, LISTENER extends Cell.Listener<ITEM>> extends RecyclerView.ViewHolder {

    private ITEM item;
    private LISTENER listener;

    private View.OnAttachStateChangeListener viewStateChangeListener;

    public Cell(View view) {
        super(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onCellClicked(getItem());
            }
        });
        attachListener(view);
    }

    private void attachListener(View view) {
        if (viewStateChangeListener == null) {
            viewStateChangeListener = new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    attachResources();
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    clearResources();
                }
            };
            view.addOnAttachStateChangeListener(viewStateChangeListener);
        } else {
            view.removeOnAttachStateChangeListener(viewStateChangeListener);
            viewStateChangeListener = null;
            attachListener(view);
        }
    }

    protected final ITEM getItem() {
        return item;
    }

    protected LISTENER getListener() {
        return listener;
    }

    protected abstract void syncUiWithItem();

    protected void prepareForReuse() {
    }

    protected void clearResources() {
    }

    protected void attachResources() {
    }

    void setCellDelegate(LISTENER listener) {
        this.listener = listener;
    }

    void setItem(ITEM item) {
        this.item = item;
    }

    void fillWithItem(ITEM item) {
        setItem(item);
        syncUiWithItem();
    }

    @Nullable
    public RecyclerView onNestedRecyclerView() {
        return null;
    }

    public interface Listener<ITEM> {

        void onCellClicked(ITEM item);
    }
}
