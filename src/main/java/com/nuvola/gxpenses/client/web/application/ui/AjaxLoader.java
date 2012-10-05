package com.nuvola.gxpenses.client.web.application.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.nuvola.gxpenses.client.resource.GxpensesRes;

public class AjaxLoader extends Widget implements HasText {

    private Timer delayedTimer = new Timer() {
        @Override
        public void run() {
            setVisible(true);
        }
    };

    @Inject
    public AjaxLoader(final GxpensesRes resources) {
        setElement(DOM.createElement("div"));
        getElement().setInnerText("Loading...");
        setStyleName(getElement(), resources.generalStyleCss().ajaxLoader());
        setVisible(false);
    }

    public void display(int time) {
        delayedTimer.schedule(time);
    }

    public void hide() {
        delayedTimer.cancel();
        setVisible(false);
    }

    @Override
    public String getText() {
        return getElement().getInnerText();
    }

    @Override
    public void setText(String text) {
        getElement().setInnerText(text);
    }

}