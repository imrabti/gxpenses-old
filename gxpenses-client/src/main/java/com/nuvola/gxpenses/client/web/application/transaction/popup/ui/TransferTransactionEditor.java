package com.nuvola.gxpenses.client.web.application.transaction.popup.ui;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.inject.Inject;
import com.nuvola.gxpenses.client.util.ValueListFactory;
import com.nuvola.gxpenses.client.web.application.transaction.renderer.AccountRenderer;
import com.nuvola.gxpenses.common.client.util.EditorView;
import com.nuvola.gxpenses.common.shared.business.Account;
import com.nuvola.gxpenses.common.shared.dto.TransferTransaction;

public class TransferTransactionEditor extends Composite implements EditorView<TransferTransaction> {
    public interface Binder extends UiBinder<HTMLPanel, TransferTransactionEditor> {
    }

    public interface Driver extends SimpleBeanEditorDriver<TransferTransaction, TransferTransactionEditor> {
    }

    @UiField(provided = true)
    ValueListBox<Account> sourceAccount;
    @UiField(provided = true)
    ValueListBox<Account> targetAccount;
    @UiField
    DoubleBox amount;

    private final ValueListFactory valueListFactory;
    private final Driver driver;

    @Inject
    TransferTransactionEditor(Binder uiBinder,
                              Driver driver,
                              ValueListFactory valueListFactory) {
        this.driver = driver;
        this.valueListFactory = valueListFactory;

        sourceAccount = new ValueListBox<>(new AccountRenderer());
        targetAccount = new ValueListBox<>(new AccountRenderer());

        initWidget(uiBinder.createAndBindUi(this));
        driver.initialize(this);
    }

    @Override
    public void edit(TransferTransaction transfertTransaction) {
        sourceAccount.setAcceptableValues(valueListFactory.getListAccounts());
        targetAccount.setAcceptableValues(valueListFactory.getListAccounts());

        amount.setFocus(true);
        driver.edit(transfertTransaction);
    }

    @Override
    public TransferTransaction get() {
        TransferTransaction transfertTransaction = driver.flush();
        if (driver.hasErrors()) {
            return null;
        } else {
            return transfertTransaction;
        }
    }
}
