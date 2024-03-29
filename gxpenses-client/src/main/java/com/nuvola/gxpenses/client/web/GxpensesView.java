package com.nuvola.gxpenses.client.web;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class GxpensesView extends ViewImpl implements GxpensesPresenter.MyView {
    public interface Binder extends UiBinder<Widget, GxpensesView> {
    }

    @UiField
    SimpleLayoutPanel main;

    @Inject
    GxpensesView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (content != null) {
            if (slot == GxpensesPresenter.TYPE_SetMainContent) {
                main.setWidget(content);
            }
        }
    }

    @Override
    public void hideLoading() {
        Element loading = Document.get().getElementById("loading");
        loading.getParentElement().removeChild(loading);
    }
}
