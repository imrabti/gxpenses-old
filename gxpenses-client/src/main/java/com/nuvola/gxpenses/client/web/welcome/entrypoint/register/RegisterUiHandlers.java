package com.nuvola.gxpenses.client.web.welcome.entrypoint.register;

import com.gwtplatform.mvp.client.UiHandlers;
import com.nuvola.gxpenses.common.shared.business.User;

public interface RegisterUiHandlers extends UiHandlers {
    void register(User user);
}
