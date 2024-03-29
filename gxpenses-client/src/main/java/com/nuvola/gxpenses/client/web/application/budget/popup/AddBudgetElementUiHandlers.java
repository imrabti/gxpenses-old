package com.nuvola.gxpenses.client.web.application.budget.popup;

import com.gwtplatform.mvp.client.UiHandlers;
import com.nuvola.gxpenses.common.shared.business.BudgetElement;

public interface AddBudgetElementUiHandlers extends UiHandlers {
    void addNewBudgetElement(BudgetElement budgetElement);

    void removeBudgetElement(BudgetElement budgetElement);

    void close();
}
