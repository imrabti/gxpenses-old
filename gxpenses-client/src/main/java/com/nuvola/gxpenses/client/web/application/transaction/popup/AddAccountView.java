package com.nuvola.gxpenses.client.web.application.transaction.popup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.nuvola.gxpenses.client.web.application.transaction.popup.ui.AccountEditor;
import com.nuvola.gxpenses.common.shared.business.Account;

public class AddAccountView extends PopupViewWithUiHandlers<AddAccountUiHandlers>
        implements AddAccountPresenter.MyView {
    public interface Binder extends UiBinder<PopupPanel, AddAccountView> {
    }

    @UiField
    PopupPanel popup;
    @UiField(provided = true)
    AccountEditor accountEditor;

    @Inject
    AddAccountView(EventBus eventBus,
                   Binder uiBinder,
                   AccountEditor accountEditor) {
        super(eventBus);

        this.accountEditor = accountEditor;

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void showRelativeTo(Widget widget) {
        int left = widget.getAbsoluteLeft() - (popup.getOffsetWidth() - widget.getOffsetWidth());
        int top = widget.getAbsoluteTop() + widget.getOffsetHeight();
        setPosition(left, top);
    }

    @Override
    public void edit(Account account) {
        accountEditor.edit(account);
    }

    @UiHandler("save")
    void onSave(ClickEvent event) {
        Account account = accountEditor.get();
        if (account != null) {
            getUiHandlers().saveAccount(account);
            hide();
        }
    }

    @UiHandler("cancel")
    void onCancel(ClickEvent event) {
        hide();
    }
}
