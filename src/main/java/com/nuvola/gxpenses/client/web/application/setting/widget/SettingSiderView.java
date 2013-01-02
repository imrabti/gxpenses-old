package com.nuvola.gxpenses.client.web.application.setting.widget;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.ValuePicker;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.nuvola.gxpenses.client.mvp.ViewWithUiHandlers;
import com.nuvola.gxpenses.client.mvp.uihandler.UiHandlersStrategy;
import com.nuvola.gxpenses.client.resource.GxpensesSiderMenuStyle;
import com.nuvola.gxpenses.client.web.application.renderer.EnumCell;

import java.util.Arrays;

public class SettingSiderView extends ViewWithUiHandlers<SettingSiderUiHandlers>
        implements SettingSiderPresenter.MyView {

    public enum SettingsEnum {
        GENERAL("General"),
        PASSWORD("Password"),
        TAGS("Tags"),
        IMPORT("Import"),
        EXPORT("Export");

        private String label;

        private SettingsEnum(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public interface Binder extends UiBinder<Widget, SettingSiderView> {
    }

    @UiField(provided = true)
    ValuePicker<SettingsEnum> settingsMenu;

    @Inject
    public SettingSiderView(final Binder uiBinder,
                            final UiHandlersStrategy<SettingSiderUiHandlers> uiHandlers,
                            final GxpensesSiderMenuStyle listResources) {
        super(uiHandlers);

        settingsMenu = new ValuePicker<SettingsEnum>(new CellList<SettingsEnum>(new EnumCell<SettingsEnum>(), listResources));
        settingsMenu.setAcceptableValues(Arrays.asList(SettingsEnum.values()));
        settingsMenu.setValue(SettingsEnum.GENERAL);

        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("settingsMenu")
    public void onMenuChanged(ValueChangeEvent<SettingsEnum> event) {
        getUiHandlers().changeMenu(event.getValue());
    }

}

