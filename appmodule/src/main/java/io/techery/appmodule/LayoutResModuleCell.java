package io.techery.appmodule;

import android.view.View;

import io.techery.celladapter.Cell;
import io.techery.celladapter.LayoutName;

@LayoutName("test_cell")
public class LayoutResModuleCell extends Cell<LMViewModel, Cell.Listener<LMViewModel>> {

    public LayoutResModuleCell(View view) {
        super(view);
    }

    @Override
    protected void syncUiWithItem() {

    }

}
