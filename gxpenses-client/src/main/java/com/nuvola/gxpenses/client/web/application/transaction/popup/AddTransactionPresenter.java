package com.nuvola.gxpenses.client.web.application.transaction.popup;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.nuvola.gxpenses.client.event.GlobalMessageEvent;
import com.nuvola.gxpenses.client.event.PopupClosedEvent;
import com.nuvola.gxpenses.client.resource.message.MessageBundle;
import com.nuvola.gxpenses.client.rest.TransactionService;
import com.nuvola.gxpenses.client.util.SuggestionListFactory;
import com.nuvola.gxpenses.client.web.application.transaction.event.AccountBalanceChangedEvent;
import com.nuvola.gxpenses.common.client.rest.AsyncCallbackImpl;
import com.nuvola.gxpenses.common.shared.business.Account;
import com.nuvola.gxpenses.common.shared.business.Transaction;
import com.nuvola.gxpenses.common.shared.type.TransactionType;

public class AddTransactionPresenter extends PresenterWidget<AddTransactionPresenter.MyView>
        implements AddTransactionUiHandler {
    public interface MyView extends PopupView, HasUiHandlers<AddTransactionUiHandler> {
        void showRelativeTo(Widget widget);

        void edit(Transaction transfer);
    }

    private final RestDispatchAsync dispatcher;
    private final TransactionService transactionService;
    private final SuggestionListFactory suggestionListFactory;
    private final MessageBundle messageBundle;

    private Widget relativeTo;
    private Account selectedAccount;

    @Inject
    AddTransactionPresenter(EventBus eventBus,
                            MyView view,
                            RestDispatchAsync dispatcher,
                            TransactionService transactionService,
                            SuggestionListFactory suggestionListFactory,
                            MessageBundle messageBundle) {
        super(eventBus, view);
        this.dispatcher = dispatcher;
        this.transactionService = transactionService;
        this.suggestionListFactory = suggestionListFactory;
        this.messageBundle = messageBundle;

        getView().setUiHandlers(this);
    }

    @Override
    public void saveTransaction(final Transaction transaction) {
        dispatcher.execute(transactionService.createNewTransaction(transaction), new AsyncCallbackImpl<Void>() {
            @Override
            public void onReceive(Void response) {
                suggestionListFactory.updatePayeeList(transaction.getPayee());
                suggestionListFactory.updateTagsList(transaction.getTags());

                GlobalMessageEvent.fire(this, messageBundle.transactionAdded());
                AccountBalanceChangedEvent.fire(this);
            }
        });
    }

    @Override
    public void close() {
        PopupClosedEvent.fire(this);
    }

    public void setRelativeTo(Widget relativeTo) {
        this.relativeTo = relativeTo;
    }

    public void setSelectedAccount(Account selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    @Override
    protected void onReveal() {
        Transaction newTransaction = new Transaction();
        newTransaction.setType(TransactionType.EXPENSE);
        newTransaction.setAccount(selectedAccount);

        getView().edit(newTransaction);
        getView().showRelativeTo(relativeTo);
    }
}
