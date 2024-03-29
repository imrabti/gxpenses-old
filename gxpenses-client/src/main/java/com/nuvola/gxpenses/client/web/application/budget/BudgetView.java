package com.nuvola.gxpenses.client.web.application.budget;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.nuvola.gxpenses.client.gin.Currency;
import com.nuvola.gxpenses.client.resource.Resources;
import com.nuvola.gxpenses.client.resource.message.MessageBundle;
import com.nuvola.gxpenses.client.resource.style.table.BigTableStyle;
import com.nuvola.gxpenses.client.web.application.budget.renderer.BudgetProgressCell;
import com.nuvola.gxpenses.client.web.application.budget.renderer.BudgetProgressFooterCell;
import com.nuvola.gxpenses.client.web.application.renderer.AmountCell;
import com.nuvola.gxpenses.client.web.application.renderer.TowSideTextCell;
import com.nuvola.gxpenses.common.shared.business.BudgetElement;
import com.nuvola.gxpenses.common.shared.dto.BudgetProgressTotal;

import java.util.Arrays;
import java.util.List;

public class BudgetView extends ViewWithUiHandlers<BudgetUiHandlers> implements BudgetPresenter.MyView {
    public interface Binder extends UiBinder<Widget, BudgetView> {
    }

    @UiField(provided=true)
    DataGrid<BudgetElement> elementsTable;
    @UiField
    Label period;
    @UiField
    Button settingButton;
    @UiField
    Label message;
    @UiField
    HTMLPanel titlePanel;
    @UiField
    HTMLPanel emptyPanel;
    @UiField
    HTMLPanel noElementsPanel;

    private final ProvidesKey<BudgetElement> keyProvider;
    private final ListDataProvider<BudgetElement> dataProvider;
    private final Resources resources;
    private final MessageBundle messageBundle;

    private final TowSideTextCell towSideTextCell;
    private final BudgetProgressCell budgetProgressCell;
    private final BudgetProgressFooterCell budgetProgressFooterCell;
    private final AmountCell amountCell;

    private BudgetProgressTotal budgetTotal;
    private String currency;

    @Inject
    BudgetView(Binder uiBinder,
               BigTableStyle bigTableStyle,
               Resources resources,
               MessageBundle messageBundle,
               BudgetProgressCell budgetProgressCell,
               BudgetProgressFooterCell budgetProgressFooterCell,
               TowSideTextCell towSideTextCell,
               AmountCell amountCell,
               @Currency String currency) {
        this.resources = resources;
        this.messageBundle = messageBundle;
        this.currency = currency;
        this.keyProvider = setupKeyProvider();
        this.dataProvider = new ListDataProvider<>(keyProvider);
        this.budgetTotal = new BudgetProgressTotal();
        this.budgetProgressCell = budgetProgressCell;
        this.budgetProgressFooterCell = budgetProgressFooterCell;
        this.towSideTextCell = towSideTextCell;
        this.amountCell = amountCell;

        elementsTable = new DataGrid<>(20, bigTableStyle);
        elementsTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        dataProvider.addDataDisplay(elementsTable);

        //Init The UI Binder
        initWidget(uiBinder.createAndBindUi(this));
        hideElementsPanel();
        initCellTable();
    }

    @Override
    public void setData(List<BudgetElement> budgetElements, BudgetProgressTotal total) {
        budgetTotal = total;
        dataProvider.getList().clear();
        dataProvider.getList().addAll(budgetElements);
        dataProvider.refresh();
    }

    @Override
    public void setPeriod(String periodName) {
        period.setText(periodName);
    }

    @Override
    public void setEmptyMessage(String message) {
        this.message.setText(message);
    }

    @Override
    public void showElementsPanel() {
        emptyPanel.setVisible(false);
        noElementsPanel.setVisible(false);
        titlePanel.setVisible(true);
        elementsTable.setVisible(true);
    }

    @Override
    public void hideElementsPanel() {
        emptyPanel.setVisible(true);
        noElementsPanel.setVisible(false);
        titlePanel.setVisible(false);
        elementsTable.setVisible(false);
    }

    @Override
    public void showNoBudgetsPanel() {
        noElementsPanel.setVisible(true);
        titlePanel.setVisible(true);
        elementsTable.setVisible(false);
    }

    @Override
    public void hideNoBudgetsPanel() {
        noElementsPanel.setVisible(false);
        titlePanel.setVisible(true);
        elementsTable.setVisible(true);
    }

    @Override
    public void switchBudgetSettingsStyle() {
        if (!settingButton.getText().equals("")) {
            settingButton.setText("");
            settingButton.removeStyleName(resources.buttonStyleCss().settingButtonText());
            settingButton.addStyleName(resources.buttonStyleCss().settingButton());
        }
    }

    @Override
    public Boolean isEmptyVisible() {
        return emptyPanel.isVisible();
    }

    @UiHandler("settingButton")
    void onSettingClicked(ClickEvent event) {
        getUiHandlers().elementSetting((Widget) event.getSource());

        settingButton.setText(messageBundle.elementSettings());
        settingButton.removeStyleName(resources.buttonStyleCss().settingButton());
        settingButton.addStyleName(resources.buttonStyleCss().settingButtonText());
    }

    @UiHandler("nextButton")
    void onNextClicked(ClickEvent event) {
        getUiHandlers().nextPeriod();
    }

    @UiHandler("previousButton")
    void onPreviousClicked(ClickEvent event) {
        getUiHandlers().previousPeriod();
    }

    private ProvidesKey<BudgetElement> setupKeyProvider() {
        return new ProvidesKey<BudgetElement>() {
            @Override
            public Object getKey(BudgetElement budgetElement) {
                return budgetElement == null ? null : budgetElement.getId();
            }
        };
    }

    private void initCellTable() {
        setupTagColumn();
        setupProgressColumn();
        setupBudgetAmountColumn();
        setupBudgetLeftColumn();
    }

    private void setupTagColumn() {
        TextColumn<BudgetElement> tagColumn = new TextColumn<BudgetElement>() {
            @Override
            public String getValue(BudgetElement object) {
                return object.getTag();
            }
        };

        tagColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        elementsTable.addColumn(tagColumn, "Expenses", "Budgeted expenses");
        elementsTable.setColumnWidth(tagColumn, 150, Style.Unit.PX);
    }

    private void setupProgressColumn() {
        Column<BudgetElement, BudgetElement> progressColumn = new
                Column<BudgetElement, BudgetElement>(budgetProgressCell) {
            @Override
            public BudgetElement getValue(BudgetElement object) {
                return object;
            }
        };
        Header<List<String>> progressHeader = new Header<List<String>>(towSideTextCell) {
            @Override
            public List<String> getValue() {
                return Arrays.asList(new String[]{"0%", "100%"});
            }
        };
        Header<BudgetProgressTotal> progressFooter = new Header<BudgetProgressTotal>(budgetProgressFooterCell) {
            @Override
            public BudgetProgressTotal getValue() {
                return budgetTotal;
            }
        };

        elementsTable.addColumn(progressColumn, progressHeader, progressFooter);
        elementsTable.setColumnWidth(progressColumn, 220, Style.Unit.PX);
    }

    private void setupBudgetAmountColumn() {
        NumberCell numberCell = new NumberCell(NumberFormat.getCurrencyFormat(currency));
        Column<BudgetElement, Number> amountColumn = new Column<BudgetElement, Number>(numberCell) {
            @Override
            public Number getValue(BudgetElement object) {
                return object.getAmount();
            }
        };
        Header<String> amountFooter = new Header<String>(new TextCell()) {
            @Override
            public String getValue() {
                return NumberFormat.getCurrencyFormat(currency).format(budgetTotal.getTotalAllowed());
            }
        };

        amountColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        elementsTable.addColumn(amountColumn, new TextHeader("Goal"), amountFooter);
        elementsTable.setColumnWidth(amountColumn, 100, Style.Unit.PX);
    }

    private void setupBudgetLeftColumn() {
        Column<BudgetElement, Double> resultColumn = new Column<BudgetElement, Double>(amountCell) {
            @Override
            public Double getValue(BudgetElement object) {
                return object.getLeftAmount();
            }
        };
        Header<String> leftFooter = new Header<String>(new TextCell()) {
            @Override
            public String getValue() {
                return NumberFormat.getCurrencyFormat(currency).format(budgetTotal.getTotalLeft());
            }
        };

        resultColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        elementsTable.addColumn(resultColumn, new TextHeader("Results"), leftFooter);
        elementsTable.setColumnWidth(resultColumn, 100, Style.Unit.PX);
    }
}
