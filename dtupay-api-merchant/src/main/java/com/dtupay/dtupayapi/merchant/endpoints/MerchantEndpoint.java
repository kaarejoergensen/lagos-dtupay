package com.dtupay.dtupayapi.merchant.endpoints;


import clients.BankClient;
import clients.TokenClient;
import exceptions.ClientException;
import models.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeoutException;


@Path("/v1/merchant")
public class MerchantEndpoint {

    private TokenClient tokenClient;
    private BankClient bankClient;

    public void setRabbitMQInfo(String host, String username, String password) throws IOException, TimeoutException {
        this.tokenClient = new TokenClient(host, username, password);
        this.bankClient = new BankClient(host, username, password);
    }


    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response payment(@QueryParam("token") String token,
                            @QueryParam("merchid") String merchid,
                            @QueryParam("price") BigDecimal price,
                            @QueryParam("description") String description) {
        try {
            String userid = tokenClient.getUserIdFromToken(token);
            bankClient.transferMoneyFromTo(userid, merchid, price, description);
        } catch (ClientException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            tokenClient.useToken(token);
        } catch (ClientException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.OK).build();
    }


    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestReport(@QueryParam("userid") String userid) {
        List <Transaction> transaction;
        try {
            transaction = bankClient.getAccount(userid).getTransactions();
            return Response.status(Response.Status.OK).entity(transaction).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

}