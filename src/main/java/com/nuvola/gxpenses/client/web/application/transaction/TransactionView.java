package com.nuvola.gxpenses.client.web.application.transaction;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.nuvola.gxpenses.client.gin.Currency;
import com.nuvola.gxpenses.client.gin.PageSize;
import com.nuvola.gxpenses.client.mvp.ViewWithUiHandlers;
import com.nuvola.gxpenses.client.mvp.uihandler.UiHandlersStrategy;
import com.nuvola.gxpenses.client.resource.GxpensesRes;
import com.nuvola.gxpenses.client.resource.GxpensesTransactionListStyle;
import com.nuvola.gxpenses.client.resource.message.MessageBundle;
import com.nuvola.gxpenses.client.web.application.transaction.renderer.TransactionCellFactory;
import com.nuvola.gxpenses.client.web.application.ui.ShowMorePagerPanel;
import com.nuvola.gxpenses.shared.domaine.Transaction;

import java.util.List;

public class TransactionView extends ViewWithUiHandlers<TransactionUiHandlers>
        implements TransactionPresenter.MyView {

    public interface Binder extends UiBinder<Widget, TransactionView> {
    }

    private String currency;

    CellList<Transaction> transactionList;

    @UiField
    Label message;
    @UiField
    Label accountName;
    @UiField
    Label period;
    @UiField
    Label accountBalance;
    @UiField
    HTMLPanel titlePanel;
    @UiField
    HTMLPanel emptyPanel;
    @UiField
    HTMLPanel noTransactionsPanel;
    @UiField
    HTMLPanel headerPanel;
    @UiField
    HTMLPanel footerPanel;
    @UiField(provided = true)
    ShowMorePagerPanel pagerPanel;
    @UiField
    Button addTransactionButton;

    private final ProvidesKey<Transaction> keyProvider;
    private final AsyncDataProvider<Transaction> dataProvider;
    private final SelectionModel<Transaction> selectionModel;
    private final MessageBundle messageBundle;
    private final GxpensesRes resources;

    @Inject
    public TransactionView(final Binder uiBinder,
                           final UiHandlersStrategy<TransactionUiHandlers> uiHandlers,
                           final TransactionCellFactory transactionCellFactory,
                           final GxpensesTransactionListStyle listResources,
                           final GxpensesRes resources, final MessageBundle messageBundle,
                           @Currency String currency, @PageSize Integer pageSize) {
        super(uiHandlers);

        this.resources = resources;
        this.currency = currency;
        this.messageBundle = messageBundle;

        keyProvider = setupKeyProvider();
        dataProvider = setupDataProvider();
        selectionModel = new SingleSelectionModel<Transaction>(keyProvider);

        pagerPanel = new ShowMorePagerPanel(pageSize);
        transactionList = new CellList<Transaction>(transactionCellFactory.create(setupRemoveAction()), listResources);
        transactionList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
        transactionList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
        transactionList.setPageSize(pageSize);
        transactionList.setSelectionModel(selectionModel);
        pagerPanel.setDisplay(transactionList);
        dataProvider.addDataDisplay(transactionList);

        initWidget(uiBinder.createAndBindUi(this));

        titlePanel.setVisible(false);
        pagerPanel.setVisible(false);
        headerPanel.setVisible(false);
        footerPanel.setVisible(false);
        noTransactionsPanel.setVisible(false);
        emptyPanel.setVisible(true);
    }

    @Override
    public void setData(List<Transaction> data, Integer start, Integer totalCount) {
        dataProvider.updateRowData(start, data);
        dataProvider.updateRowCount(totalCount, true);
    }

    @Override
    public void showTransactionsPanel() {
        emptyPanel.setVisible(false);
        noTransactionsPanel.setVisible(false);
        titlePanel.setVisible(true);
        pagerPanel.setVisible(true);
        headerPanel.setVisible(true);
        footerPanel.setVisible(true);
    }

    @Override
    public void hideTransactionsPanel() {
        emptyPanel.setVisible(true);
        noTransactionsPanel.setVisible(false);
        titlePanel.setVisible(false);
        pagerPanel.setVisible(false);
        headerPanel.setVisible(false);
        footerPanel.setVisible(false);
    }

    @Override
    public void showNoTransactionsPanel() {
        noTransactionsPanel.setVisible(true);
        titlePanel.setVisible(true);
        pagerPanel.setVisible(false);
        headerPanel.setVisible(false);
        footerPanel.setVisible(false);
    }

    @Override
    public void hideNoTransactionsPanel() {
        noTransactionsPanel.setVisible(false);
        titlePanel.setVisible(true);
        pagerPanel.setVisible(true);
        headerPanel.setVisible(true);
        footerPanel.setVisible(true);
    }

    @Override
    public Boolean isEmptyVisible() {
        return emptyPanel.isVisible();
    }

    @Override
    public void setAccountName(String name) {
        accountName.setText(name);
    }

    @Override
    public void setPeriod(String periodStr) {
        period.setText(periodStr);
    }

    @Override
    public void setTransactionTotal(Double total) {
        accountBalance.setText(messageBundle.transactionTotal(NumberFormat.getCurrencyFormat(currency).format(total)));
    }

    @Override
    public void setEmptyMessage(String message) {
        this.message.setText(message);
    }

    @Override
    public void switchAddTransactionStyle() {
        if (!addTransactionButton.getText().equals("")) {
            addTransactionButton.setText("");
            addTransactionButton.removeStyleName(resources.buttonStyleCss().addButtonAltText());
            addTransactionButton.addStyleName(resources.buttonStyleCss().addButtonAlt());
        }
    }

    @UiHandler("addTransactionButton")
    void onAddTransaction(ClickEvent event) {
        getUiHandlers().addNewTransaction((Widget) event.getSource());

        addTransactionButton.setText(messageBundle.transactionNew());
        addTransactionButton.removeStyleName(resources.buttonStyleCss().settingButton());
        addTransactionButton.addStyleName(resources.buttonStyleCss().addButtonAltText());
    }

    private ProvidesKey<Transaction> setupKeyProvider() {
        return new ProvidesKey<Transaction>() {
            @Override
            public Object getKey(Transaction transaction) {
                return transaction == null ? null : transaction.getId();
            }
        };
    }

    private AsyncDataProvider<Transaction> setupDataProvider() {
        return new AsyncDataProvider<Transaction>(keyProvider) {
            @Override
            protected void onRangeChanged(HasData<Transaction> display) {
                fetchData(display);
            }
        };
    }

    private ActionCell.Delegate<Transaction> setupRemoveAction() {
        return new ActionCell.Delegate<Transaction>() {
            @Override
            public void execute(Transaction transaction) {
                getUiHandlers().removeTransaction(transaction);
            }
        };
    }

    private void fetchData(HasData<Transaction> display) {
        Range range = display.getVisibleRange();

        if (getUiHandlers() != null) {
            getUiHandlers().loadTransactions(range.getStart(), range.getLength());
        }
    }

}