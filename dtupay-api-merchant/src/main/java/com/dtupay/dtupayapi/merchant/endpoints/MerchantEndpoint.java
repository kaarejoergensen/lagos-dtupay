package com.dtupay.dtupayapi.merchant.endpoints;


import clients.BankClient;
import clients.TokenClient;
import exceptions.ClientException;
import models.AccountInfo;
import models.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;


@Path("/v1/merchant")
public class MerchantEndpoint {

    private TokenClient tokenClient;
    private BankClient bankClient;

    public void setRabbitMQInfo(String host, String username, String password) throws IOException, TimeoutException {
        if (host == null || username == null || password == null)
            throw new IllegalArgumentException("No arguments can be null!");
        this.tokenClient = new TokenClient(host, username, password);
        this.bankClient = new BankClient(host, username, password);
    }

    /*
        Refund das money
        - Assumes Description is identifcation nr of that payment
     */

    @POST
    @Path("/refund")
    public Response refund(@QueryParam("userId") String user, @QueryParam("merchId") String merchId, @QueryParam("description") String description){
        try {
            List<Transaction> transactions = bankClient.getAccount(merchId).getTransactions();
            //Finner Transaction til kunden
            for(Transaction t : transactions){
                if(t.getDebtor() == user && t.getDescription().equals(description)){
                    bankClient.transferMoneyFromTo(merchId,user,t.getAmount(),"Refunded::" + description);
                    return Response.status(Response.Status.OK).entity("Transaction '" +  description + "' successfully refunded to user " + user).build();
                }
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Transaction or users not found").build();
    }



    /*
        Use token to transfer money
     */

    @POST
    @Path("/payment")
    public Response payment(@QueryParam("token") String token,
                            @QueryParam("merchId") String merchId,
                            @QueryParam("price") BigDecimal price,
                            @QueryParam("description") String description) {
        try {
            String userid = tokenClient.getUserIdFromToken(token);
            bankClient.transferMoneyFromTo(userid, merchId, price, description);
            tokenClient.useToken(token);
        } catch (ClientException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).build();
    }


    /*
        Returns a list of anonymised transactions in some time frame
        DateFormat = "dd-MM-yyyy"
     */

    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestReport(@QueryParam("merchId") String merchId,
                                  @QueryParam("from") String from,
                                  @QueryParam("to") String to) {
        try {
            List<Transaction> transactions = bankClient.getAccount(merchId).getTransactions();

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

            Date raw_fromdate = df.parse(from);
            Calendar parsed_fromDate = new GregorianCalendar();
            parsed_fromDate.setTime(raw_fromdate);

            Date raw_todate = df.parse(to);
            Calendar parsed_toDate = new GregorianCalendar();
            parsed_toDate.setTime(raw_todate);

            for(Transaction t : transactions){
                long mill = t.getTime().getMillisecond();
                if(mill < parsed_toDate.getTimeInMillis() || mill > parsed_toDate.getTimeInMillis()){
                    transactions.remove(t);
                }
            }
            return Response.status(Response.Status.OK).entity(transactions).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

}