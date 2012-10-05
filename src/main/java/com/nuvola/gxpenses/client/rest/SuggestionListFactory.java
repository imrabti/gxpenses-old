package com.nuvola.gxpenses.client.rest;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.fusesource.restygwt.client.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestionListFactory {

    private final UserService userService;

    private List<String> listTags;
    private List<String> listPayee;

    @Inject
    public SuggestionListFactory(final UserService userService) {
        this.userService = userService;
    }

    public List<String> getListTags() {
        if (listTags == null) {
            userService.getTags(new MethodCallbackImpl<List<String>>() {
                @Override
                public void onSuccess(Method method, List<String> tags) {
                    listTags = tags;
                }
            });
        }

        return listTags;
    }

    public List<String> getListPayee() {
        if (listPayee == null) {
            userService.getPayees(new MethodCallbackImpl<List<String>>() {
                @Override
                public void onSuccess(Method method, List<String> payees) {
                    listPayee = payees;
                }
            });
        }

        return listPayee;
    }

    public void updatePayeeList(String payee) {
        if (!listPayee.contains(payee)) {
            listPayee.add(payee);
        }
    }

    public void updateTagsList(String tag) {
        if (!Strings.isNullOrEmpty(tag)) {
            List<String> tags = Arrays.asList(tag.split(","));
            List<String> toAdd = new ArrayList<String>();

            for (String item : tags) {
                if (!listTags.contains(item.trim().toLowerCase())) {
                    listTags.add(item);
                    toAdd.add(item);
                }
            }

            userService.updateTags(toAdd, new MethodCallbackImpl<Void>() {
                @Override
                public void onSuccess(Method method, Void aVoid) {
                }
            });
        }
    }

}