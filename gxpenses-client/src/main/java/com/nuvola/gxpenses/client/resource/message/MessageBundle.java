package com.nuvola.gxpenses.client.resource.message;

import com.google.gwt.i18n.client.Messages;

public interface MessageBundle extends Messages {
    String wrongLoginOrPassword();

    String loginPasswordRequired();

    String registerInfoMissing();

    String noAccounts();

    String noSelectedAccount();

    String transactionAdded();

    String transactionConf();

    String transactionRemoved();

    String accountAdded();

    String accountConf();

    String accountRemoved();

    String transfertAdded();

    String transactionNew();

    String transactionTotal(String total);

    String settingsUpdated();

    String passwordUpdated();

    String tagsUpdated();

    String budgetAdded();

    String budgetElementConf();

    String budgetElementAdded();

    String budgetElementRemoved();

    String elementSettings();

    String noBudgets();

    String noSelectedBudget();
}
