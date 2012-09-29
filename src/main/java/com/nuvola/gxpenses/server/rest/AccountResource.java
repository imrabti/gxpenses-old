package com.nuvola.gxpenses.server.rest;

import com.nuvola.gxpenses.server.service.AccountService;
import com.nuvola.gxpenses.shared.domaine.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/account")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class AccountResource {

    @Autowired
    private AccountService accountService;

    @POST
    public Response createAccount(Account account) {
        accountService.createAccount(account);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeAccount(@PathParam("id") Long id) {
        accountService.removeAccount(id);
        return Response.ok().build();
    }

}