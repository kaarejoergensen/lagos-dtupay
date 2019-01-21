package com.dtupay.dtupayapi.customer.endpoints;


import clients.BankClient;
import clients.TokenClient;
import com.dtupay.dtupayapi.customer.application.CustomerUtils;
import com.dtupay.dtupayapi.customer.models.TokenBarcodePathPair;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import exceptions.ClientException;
import models.Transaction;
import org.apache.commons.lang3.RandomStringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;


@Path("/v1/customer")
public class CustomerEndpoint {
    private TokenClient tokenClient;
    private BankClient bankClient;
    private CustomerUtils utils;

	//private BarcodeProvider barcodeProvider = new BarcodeProvider();

    public void setRabbitMQInfo(String host, String username, String password) throws IOException, TimeoutException {
        if (host == null || username == null || password == null)
            throw new IllegalArgumentException("No arguments can be null!");
        this.tokenClient = new TokenClient(host, username, password);
        this.bankClient = new BankClient(host, username, password);
        utils = new CustomerUtils(tokenClient, bankClient);
    }


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
        return utils.getBarcode(fileName);
    }


    /*
        Requires the date format 'dd-MM-yyyy'
     */
    
    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactions(String userId, String fromDate, String toDate){
        try{
            List<Transaction> transactions = utils.getTransactions(userId,fromDate,toDate);
            return Response.ok(transactions).build();
        } catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Could not parse date").build();
        } catch (ClientException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not connect bank client").build();
        }
    }





}
