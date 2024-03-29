package com.nuvola.gxpenses.client.web.application.budget.renderer;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.nuvola.gxpenses.client.web.application.renderer.ClickableIconCell;
import com.nuvola.gxpenses.common.shared.business.BudgetElement;

public class RemoveBudgetElementCell extends ClickableIconCell<BudgetElement> {
    @Inject
    RemoveBudgetElementCell(Template template,
                            @Assisted ImageResource res,
                            @Assisted ActionCell.Delegate<BudgetElement> delegate) {
        super(template, res, delegate);
    }
}
