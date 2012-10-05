package com.nuvola.gxpenses.client.web.application.transaction.widget;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.nuvola.gxpenses.client.event.GlobalMessageEvent;
import com.nuvola.gxpenses.client.event.NoElementFoundEvent;
import com.nuvola.gxpenses.client.event.PopupClosedEvent;
import com.nuvola.gxpenses.client.resource.message.MessageBundle;
import com.nuvola.gxpenses.client.rest.AccountService;
import com.nuvola.gxpenses.client.rest.MethodCallbackImpl;
import com.nuvola.gxpenses.client.web.application.transaction.event.AccountBalanceChangedEvent;
import com.nuvola.gxpenses.client.web.application.transaction.event.AccountChangedEvent;
import com.nuvola.gxpenses.client.web.application.transaction.event.TransactionFiltreChangedEvent;
import com.nuvola.gxpenses.client.web.application.transaction.popup.AddAccountPresenter;
import com.nuvola.gxpenses.client.web.application.transaction.popup.TransferTransactionPresenter;
import com.nuvola.gxpenses.shared.domaine.Account;
import com.nuvola.gxpenses.shared.type.PeriodType;
import com.nuvola.gxpenses.shared.type.TransactionType;
import org.fusesource.restygwt.client.Method;

import java.util.List;

public class AccountSiderPresenter extends PresenterWidget<AccountSiderPresenter.MyView>
        implements AccountSiderUiHandlers, AccountBalanceChangedEvent.AccountBalanceChangedHandler,
        PopupClosedEvent.PopupClosedHandler {

    public interface MyView extends View, HasUiHandlers<AccountSiderUiHandlers> {
        void setData(List<Account> accounts);

        void showTransferButton(Boolean visible);

        void switchTransferStyle();
    }

    private final AccountService accountService;
    private final AddAccountPresenter addAccountPresenter;
    private final TransferTransactionPresenter transferTransactionPresenter;
    private final MessageBundle messageBundle;

    private PeriodType currentPeriodType;
    private TransactionType currentTransactionType;

    @Inject
    public AccountSiderPresenter(EventBus eventBus, MyView view,
                                 final MessageBundle messageBundle,
                                 final AccountService accountService,
                                 final AddAccountPresenter addAccountPresenter,
                                 final TransferTransactionPresenter transferTransactionPresenter) {
        super(eventBus, view);

        this.messageBundle = messageBundle;
        this.accountService = accountService;
        this.addAccountPresenter = addAccountPresenter;
        this.transferTransactionPresenter = transferTransactionPresenter;

        currentPeriodType = PeriodType.THIS_MONTH;
        currentTransactionType = TransactionType.ALL;

        getView().setUiHandlers(this);
    }

    @Override
    public void onPopupClosed(PopupClosedEvent event) {
        getView().switchTransferStyle();
    }

    @Override
    public void onAccountBalanceChanged(AccountBalanceChangedEvent event) {
        fireLoadListAccounts();
    }

    @Override
    public void addNewAccount(Widget relativeTo) {
        addAccountPresenter.setRelativeTo(relativeTo);
        addToPopupSlot(addAccountPresenter, false);
    }

    @Override
    public void addNewTransfert(Widget relativeTo) {
        transferTransactionPresenter.setRelativeTo(relativeTo);
        addToPopupSlot(transferTransactionPresenter, false);
    }

    @Override
    public void removeAccount(Account account) {
        Boolean decision = Window.confirm("Are you sure about removing this account ?");
        if (decision) {
            accountService.removeAccount(account.getId(), new MethodCallbackImpl<Void>() {
                @Override
                public void onSuccess(Method method, Void aVoid) {
                    AccountChangedEvent.fire(this);
                    GlobalMessageEvent.fire(this, messageBundle.accountRemoved());
                    fireLoadListAccounts();
                }
            });
        }
    }

    @Override
    public void accountSelected(Account account) {
        AccountChangedEvent.fire(this, account, currentPeriodType, currentTransactionType);
    }

    @Override
    public void filterChange(PeriodType newPeriod, TransactionType newType) {
        currentPeriodType = newPeriod;
        currentTransactionType = newType;
        TransactionFiltreChangedEvent.fire(this, newPeriod, newType);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        fireLoadListAccounts();
    }

    @Override
    protected void onBind() {
        super.onBind();

        addRegisteredHandler(AccountBalanceChangedEvent.getType(), this);
        addRegisteredHandler(PopupClosedEvent.getType(), this);
    }

    private void fireLoadListAccounts() {
        accountService.getAccounts(new MethodCallbackImpl<List<Account>>() {
            @Override
            public void onSuccess(Method method, List<Account> accounts) {
                getView().showTransferButton(accounts.size() >= 2);
                getView().setData(accounts);

                NoElementFoundEvent.fire(this, accounts.size());
            }
        });
    }

}