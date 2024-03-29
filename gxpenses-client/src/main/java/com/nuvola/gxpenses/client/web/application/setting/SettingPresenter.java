package com.nuvola.gxpenses.client.web.application.setting;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.nuvola.gxpenses.client.event.SetVisibleSiderEvent;
import com.nuvola.gxpenses.client.place.NameTokens;
import com.nuvola.gxpenses.client.security.LoggedInGatekeeper;
import com.nuvola.gxpenses.client.web.application.ApplicationPresenter;
import com.nuvola.gxpenses.client.web.application.setting.event.SettingsMenuChangedEvent;
import com.nuvola.gxpenses.client.web.application.setting.widget.GeneralSettingPresenter;
import com.nuvola.gxpenses.client.web.application.setting.widget.PasswordSettingPresenter;
import com.nuvola.gxpenses.client.web.application.setting.widget.SettingSiderPresenter;
import com.nuvola.gxpenses.client.web.application.setting.widget.TagSettingPresenter;

public class SettingPresenter extends Presenter<SettingPresenter.MyView, SettingPresenter.MyProxy>
        implements SettingsMenuChangedEvent.SettingsChangedEventHandler {
    public interface MyView extends View {
    }

    @ProxyStandard
    @NameToken(NameTokens.setting)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface MyProxy extends ProxyPlace<SettingPresenter> {
    }

    public static final Object TYPE_SetMainContent = new Object();

    private final SettingSiderPresenter settingSiderPresenter;
    private final GeneralSettingPresenter generalSettingPresenter;
    private final PasswordSettingPresenter passwordSettingPresenter;
    private final TagSettingPresenter tagSettingPresenter;

    @Inject
    SettingPresenter(EventBus eventBus,
                     MyView view,
                     MyProxy proxy,
                     SettingSiderPresenter settingSiderPresenter,
                     GeneralSettingPresenter generalSettingPresenter,
                     PasswordSettingPresenter passwordSettingPresenter,
                     TagSettingPresenter tagSettingPresenter) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);

        this.settingSiderPresenter = settingSiderPresenter;
        this.generalSettingPresenter = generalSettingPresenter;
        this.passwordSettingPresenter = passwordSettingPresenter;
        this.tagSettingPresenter = tagSettingPresenter;
    }

    @Override
    public void onSettingsChanged(SettingsMenuChangedEvent event) {
        switch (event.getSelectedMenu()) {
            case GENERAL:
                setInSlot(TYPE_SetMainContent, generalSettingPresenter);
                break;
            case PASSWORD:
                setInSlot(TYPE_SetMainContent, passwordSettingPresenter);
                break;
            case TAGS:
                setInSlot(TYPE_SetMainContent, tagSettingPresenter);
                break;
            default:
                setInSlot(TYPE_SetMainContent, generalSettingPresenter);
                break;
        }
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(SettingsMenuChangedEvent.getType(), this);
    }

    @Override
    protected void onReveal() {
        SetVisibleSiderEvent.fire(this, settingSiderPresenter);
        setInSlot(TYPE_SetMainContent, generalSettingPresenter);
    }
}
