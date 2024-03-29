package com.nuvola.gxpenses.client.web.application.transaction.popup.ui;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.inject.Inject;
import com.nuvola.gxpenses.client.web.application.renderer.EnumRenderer;
import com.nuvola.gxpenses.common.client.util.EditorView;
import com.nuvola.gxpenses.common.shared.business.Account;
import com.nuvola.gxpenses.common.shared.type.AccountType;

import java.util.Arrays;

public class AccountEditor extends Composite implements EditorView<Account> {
    public interface Binder extends UiBinder<HTMLPanel, AccountEditor> {
    }

    public interface Driver extends SimpleBeanEditorDriver<Account, AccountEditor> {
    }

    @UiField
    TextBox name;
    @UiField(provided = true)
    ValueListBox<AccountType> type;
    @UiField
    DoubleBox balance;

    private final Driver driver;

    @Inject
    AccountEditor(Binder uiBinder,
                  Driver driver) {
        this.driver = driver;

        //Initialize ValusListBox elements
        type = new ValueListBox<>(new EnumRenderer<AccountType>());
        type.setValue(AccountType.CASH);
        type.setAcceptableValues(Arrays.asList(AccountType.values()));

        initWidget(uiBinder.createAndBindUi(this));
        driver.initialize(this);
    }

    @Override
    public void edit(Account account) {
        name.setFocus(true);
        driver.edit(account);
    }

    @Override
    public Account get() {
        Account account = driver.flush();
        if (driver.hasErrors()) {
            return null;
        } else {
            return account;
        }
    }
}
