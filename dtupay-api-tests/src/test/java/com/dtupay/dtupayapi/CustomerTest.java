package com.dtupay.dtupayapi;

import com.dtupay.dtupayapi.customer.models.TokenBarcodePathPair;
import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.reflect.TypeToken;
import models.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@Ignore
public class  CustomerTest {

    private static String customerUid;
    private static String merchantUid;

    @Test
    public void test() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/v1/customer");
        String result = webTarget.path("").request().get(String.class);
        System.out.println(result);
    }

    @Test
    public void testRequestTokens() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080").path("v1/customer/requestTokens");

        Response response = webTarget
                .queryParam("name", "Lagos")
                .queryParam("uid", customerUid)
                .queryParam("count", 1)
                .request(MediaType.APPLICATION_JSON).post(Entity.entity("", MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testBarcode() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080").path("v1/customer/barcode/");

    }

    //Customer report
    @Test
    public void testGetCustomerTransactions() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080").path("v1/customer/transactions");

        String response = webTarget
                .queryParam("userId", customerUid)
                .queryParam("from", "07-01-2019")
                .queryParam("to", "26-01-2019")
                .request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Transaction>>(){}.getType();
        List<Transaction> transactionList = gson.fromJson(response, listType);
        assertThat(transactionList, is(notNullValue()));
    }

    //Merchant report
    @Test
    public void testGetMerchantTransactions() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8081").path("v1/merchant/transactions");

        String response = webTarget
                .queryParam("merchId", merchantUid)
                .queryParam("from", "07-01-2019")
                .queryParam("to", "26-01-2019")
                .request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Transaction>>(){}.getType();
        List<Transaction> transactionList = gson.fromJson(response, listType);
        assertThat(transactionList, is(notNullValue()));
    }


    @Test
    public void testTransfer() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080").path("v1/customer/requestTokens");

        String response = webTarget
                .queryParam("name", "Lagos")
                .queryParam("uid", customerUid)
                .queryParam("count", 1)
                .request(MediaType.APPLICATION_JSON).post(Entity.entity("", MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TokenBarcodePathPair>>(){}.getType();
        List<TokenBarcodePathPair> tokens = gson.fromJson(response, listType);
        assertThat(tokens.size(), is(1));
        System.out.println(tokens.get(0).getToken());

        WebTarget webTarget2 = client.target("http://localhost:8081").path("v1/merchant/payment");

        Response response2 = webTarget2
                .queryParam("token", tokens.get(0).getToken())
                .queryParam("merchId", merchantUid)
                .queryParam("price", new BigDecimal(100))
                .queryParam("description", "some money")
                .request(MediaType.APPLICATION_JSON).post(Entity.entity("", MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response2.getStatus());

    }



    @BeforeClass
    public static void setup () {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget;

        webTarget = client.target("http://localhost:8080").path("v1/customer/createUser");

        customerUid = webTarget
                .queryParam("username", "LagosCustomer")
                .queryParam("cprNumber", "1243224389")
                .queryParam("firstName", "LagosCustomer")
                .queryParam("lastName", "DTUpay")
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);

        merchantUid = webTarget
                .queryParam("username", "LagosMerchant")
                .queryParam("cprNumber", "1234556456")
                .queryParam("firstName", "LagosMerchant")
                .queryParam("lastName", "DTUpay")
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);

        System.out.println(customerUid);
        System.out.println(merchantUid);
    }

    @AfterClass
    public static void tearDown () {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8082").path("v1/manager/user/retireAccount");
        String result = webTarget.queryParam("accountId", customerUid)
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);
        result = webTarget.queryParam("accountId", merchantUid)
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);
    }

    private void retireAccount(String accountId) {

    }


}
