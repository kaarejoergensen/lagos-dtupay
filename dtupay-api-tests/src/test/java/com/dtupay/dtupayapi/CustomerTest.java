package com.dtupay.dtupayapi;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;


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

        Form form = new Form();
        form.param("name", "Lagos");
        form.param("uid", customerUid);
        form.param("count", String.valueOf("0"));

        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
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

        Form form = new Form();
        form.param("uid", customerUid);
        form.param("fromDate",  "07-01-2019");
        form.param("toDate","26-01-2019");

        String s = webTarget.request("").get(String.class);
        assertEquals("works", s);
    }

    //Merchant report
    @Test
    public void testGetMerchantTransactions() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8081/v1/merchant/transactions");

        Form form = new Form();
        form.param("uid", merchantUid);
        form.param("fromDate",  "07-01-2019");
        form.param("toDate","26-01-2019");

        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }


    @Test
    public void testTransfer() {
    Client client = ClientBuilder.newClient();
    WebTarget webTarget = client.target("http://localhost:8081/v1/merchant/");

    Form form = new Form();
    form.param("cuid", customerUid);
    form.param("muid", merchantUid);
    form.param("price", "100");
    form.param("description", "some money");

    Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    }



    @BeforeClass
    public static void setup () {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget;

        webTarget = client.target("http://localhost:8080").path("v1/customer/createUser");

        customerUid = webTarget
                .queryParam("username", "LagosCustomer")
                .queryParam("cprNumber", "1243214321")
                .queryParam("firstName", "LagosCustomer")
                .queryParam("lastName", "DTUpay")
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);

        merchantUid = webTarget
                .queryParam("username", "LagosMerchant")
                .queryParam("cprNumber", "1234556677")
                .queryParam("firstName", "LagosMerchant")
                .queryParam("lastName", "DTUpay")
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);

        System.out.println(customerUid);
        System.out.println(merchantUid);
    }

    @AfterClass
    public static void tearDown () {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget;
        webTarget = client.target("http://localhost:8082/v1/manager/user/retireAccount?" +
                "accountID=" + customerUid);
        webTarget = client.target("http://localhost:8082/v1/manager/user/retireAccount?" +
                "accountID=" + merchantUid);
    }

    private void retireAccount(String accountId) {

    }


}
