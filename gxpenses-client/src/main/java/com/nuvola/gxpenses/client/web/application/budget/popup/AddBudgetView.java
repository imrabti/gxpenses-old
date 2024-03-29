package com.nuvola.gxpenses.client.web.application.budget.popup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.nuvola.gxpenses.client.web.application.budget.popup.ui.BudgetEditor;
import com.nuvola.gxpenses.common.shared.business.Budget;

public class AddBudgetView extends PopupViewWithUiHandlers<AddBudgetUiHandler> implements AddBudgetPresenter.MyView {
    public interface Binder extends UiBinder<PopupPanel, AddBudgetView> {
    }

    @UiField
    PopupPanel popup;
    @UiField(provided = true)
    BudgetEditor budgetEditor;

    @Inject
    AddBudgetView(EventBus eventBus,
                  Binder uiBinder,
                  BudgetEditor budgetEditor) {
        super(eventBus);

        this.budgetEditor = budgetEditor;

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void showRelativeTo(Widget widget) {
        int left = widget.getAbsoluteLeft() - (popup.getOffsetWidth() - widget.getOffsetWidth());
        int top = widget.getAbsoluteTop() + widget.getOffsetHeight();
        setPosition(left, top);
    }

    @Override
    public void edit(Budget budget) {
        budgetEditor.edit(budget);
    }

    @UiHandler("save")
    void onSave(ClickEvent event) {
        Budget budget = budgetEditor.get();
        if (budget != null) {
            getUiHandlers().saveBudget(budget);
            hide();
        }
    }

    @UiHandler("cancel")
    void onCancel(ClickEvent event) {
        hide();
    }
}
