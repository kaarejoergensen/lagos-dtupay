package com.dtupay.dtupayapi.customer.endpoints;


import clients.BankClient;
import clients.TokenClient;
import com.dtupay.dtupayapi.customer.application.CustomerUtils;
import com.dtupay.dtupayapi.customer.models.TokenBarcodePathPair;
import com.google.zxing.WriterException;
import exceptions.ClientException;
import models.Transaction;
import models.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;


@Path("/v1/customer")
public class CustomerEndpoint {
    private TokenClient tokenClient;
    private BankClient bankClient;
    private CustomerUtils utils;


    public void setRabbitMQInfo(String host, String username, String password) throws IOException, TimeoutException {
        if (host == null || username == null || password == null)
            throw new IllegalArgumentException("No arguments can be null!");
        this.tokenClient = new TokenClient(host, username, password);
        this.bankClient = new BankClient(host, username, password);
        utils = new CustomerUtils(tokenClient, bankClient);
    }

    /*
	@POST
    @Path("/createUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@QueryParam("username") String username,
                               @QueryParam("cprNumber") String cprNumber,
                               @QueryParam("firstName") String firstName,
                               @QueryParam("lastName") String lastName) {
        User user = new User(cprNumber, firstName, lastName);

        try {
            String userId = this.bankClient.createAccountWithBalance(user, new BigDecimal(1000));
            return Response.status(Response.Status.OK).entity(userId).build();
        } catch (ClientException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }*/

    @POST
    @Path("/requestTokens")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestTokens( @QueryParam("name") String username, @QueryParam("uid") String userId, @QueryParam("count") int number) {
		try {
            Set<TokenBarcodePathPair> finalTokens = utils.requestTokens(username, userId, number);
            return Response.ok(finalTokens).build();
        }catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Caught exception").build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Caught exception").build();
        } catch (ClientException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Caught expection").build();
        } catch (WriterException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Caught expection").build();
        }
    }

    @GET
    @Path("/barcode/{fileName}")
    public Response getBarcode(@PathParam("fileName") String fileName) {
        if (fileName == "hello") {
            return Response.ok("Hello from the other side").build();
        }else{
            return utils.getBarcodeImage(fileName);
        }
    }

    /*
        Requires the date format 'dd-MM-yyyy'
     */

    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactions(@QueryParam("userId") String userId,
                                    @QueryParam("from") String fromDate,
                                    @QueryParam("to") String toDate){
        try{
            List<Transaction> transactions = utils.getTransactions(userId,fromDate,toDate);
            return Response.ok(transactions).build();
        } catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Could not parse date").build();
        } catch (ClientException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not connect bank client").build();
        }
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() {
        return Response.status(Response.Status.OK).entity("You did it!!!!").build();
    }
}
