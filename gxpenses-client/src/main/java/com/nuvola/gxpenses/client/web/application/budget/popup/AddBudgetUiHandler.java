package com.nuvola.gxpenses.client.web.application.budget.popup;

import com.gwtplatform.mvp.client.UiHandlers;
import com.nuvola.gxpenses.common.shared.business.Budget;

public interface AddBudgetUiHandler extends UiHandlers {
    void saveBudget(Budget budget);
}
