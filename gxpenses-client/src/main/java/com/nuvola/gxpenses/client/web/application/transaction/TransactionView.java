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
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.nuvola.gxpenses.client.gin.Currency;
import com.nuvola.gxpenses.client.gin.PageSize;
import com.nuvola.gxpenses.client.mvp.ViewWithUiHandlers;
import com.nuvola.gxpenses.client.mvp.uihandler.UiHandlersStrategy;
import com.nuvola.gxpenses.client.request.proxy.TransactionProxy;
import com.nuvola.gxpenses.client.resource.Resources;
import com.nuvola.gxpenses.client.resource.message.MessageBundle;
import com.nuvola.gxpenses.client.resource.style.list.TransactionListStyle;
import com.nuvola.gxpenses.client.web.application.transaction.renderer.TransactionCellFactory;
import com.nuvola.gxpenses.client.web.application.ui.ShowMorePagerPanel;

import java.util.List;

public class TransactionView extends ViewWithUiHandlers<TransactionUiHandlers>
        implements TransactionPresenter.MyView {
    public interface Binder extends UiBinder<Widget, TransactionView> {
    }

    CellList<TransactionProxy> transactionList;

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

    private final ProvidesKey<TransactionProxy> keyProvider;
    private final AsyncDataProvider<TransactionProxy> dataProvider;
    private final SingleSelectionModel<TransactionProxy> selectionModel;
    private final MessageBundle messageBundle;
    private final Resources resources;

    private String currency;

    @Inject
    public TransactionView(final Binder uiBinder,
                           final UiHandlersStrategy<TransactionUiHandlers> uiHandlers,
                           final TransactionCellFactory transactionCellFactory,
                           final TransactionListStyle listResources,
                           final Resources resources, final MessageBundle messageBundle,
                           @Currency String currency, @PageSize Integer pageSize) {
        super(uiHandlers);

        this.resources = resources;
        this.currency = currency;
        this.messageBundle = messageBundle;

        keyProvider = setupKeyProvider();
        dataProvider = setupDataProvider();
        selectionModel = new SingleSelectionModel<TransactionProxy>(keyProvider);
        pagerPanel = new ShowMorePagerPanel(pageSize);
        transactionList = new CellList<TransactionProxy>(transactionCellFactory.create(setupRemoveAction()), listResources);

        initWidget(uiBinder.createAndBindUi(this));
        hideTransactionsPanel();

        pagerPanel.setDisplay(transactionList);
        dataProvider.addDataDisplay(transactionList);
        transactionList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
        transactionList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
        transactionList.setPageSize(pageSize);
        transactionList.setSelectionModel(selectionModel);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
            }
        });
    }

    @Override
    public void setData(List<TransactionProxy> data, Integer start, Integer totalCount) {
        dataProvider.updateRowData(start, data);
        dataProvider.updateRowCount(totalCount, false);
    }

    @Override
    public void clearSelection() {
        selectionModel.clear();
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
        if (total != null) {
            accountBalance.setText(messageBundle.transactionTotal(
                    NumberFormat.getCurrencyFormat(currency).format(total)));
        }
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
        addTransactionButton.removeStyleName(resources.buttonStyleCss().addButtonAlt());
        addTransactionButton.addStyleName(resources.buttonStyleCss().addButtonAltText());
    }

    private ProvidesKey<TransactionProxy> setupKeyProvider() {
        return new ProvidesKey<TransactionProxy>() {
            @Override
            public Object getKey(TransactionProxy transaction) {
                return transaction == null ? null : transaction.getId();
            }
        };
    }

    private AsyncDataProvider<TransactionProxy> setupDataProvider() {
        return new AsyncDataProvider<TransactionProxy>(keyProvider) {
            @Override
            protected void onRangeChanged(HasData<TransactionProxy> display) {
                fetchData(display);
            }
        };
    }

    private ActionCell.Delegate<TransactionProxy> setupRemoveAction() {
        return new ActionCell.Delegate<TransactionProxy>() {
            @Override
            public void execute(TransactionProxy transaction) {
                getUiHandlers().removeTransaction(transaction);
            }
        };
    }

    private void fetchData(HasData<TransactionProxy> display) {
        Range range = display.getVisibleRange();

        if (getUiHandlers() != null) {
            getUiHandlers().loadTransactions(range.getStart(), range.getLength());
        }
    }
}
