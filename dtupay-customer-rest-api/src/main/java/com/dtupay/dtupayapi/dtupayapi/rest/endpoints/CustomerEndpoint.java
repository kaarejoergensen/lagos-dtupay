package com.dtupay.dtupayapi.dtupayapi.rest.endpoints;


import clients.TokenClient;
import com.dtupay.dtupayapi.dtupayapi.rest.models.TokenBarcodePathPair;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import exceptions.ClientException;
import org.apache.commons.lang3.RandomStringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;


@Path("/v1/customer")
public class CustomerEndpoint {

    //Cunt

    private final int QR_SIZE = 300;
	//private BarcodeProvider barcodeProvider = new BarcodeProvider();

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("username") String username,
                            @QueryParam("userId") String userId,
                            @QueryParam("numberOfTokens") Integer numberOfTokens) {
        try {
            TokenClient tokenClient = new TokenClient("rabbitmq");
            return Response.ok(tokenClient.getTokens(username, userId, numberOfTokens)).build();
        } catch (IOException | TimeoutException | ClientException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private final String IMAGE_DIR = "/images/";
    private String saveQRToDisk(BitMatrix qrMatrix) throws IOException {
        String directory = System.getProperty("user.dir") + IMAGE_DIR;

        java.nio.file.Path directoryPath = FileSystems.getDefault().getPath(directory);
        if (Files.notExists(directoryPath)) Files.createDirectory(directoryPath);
        java.nio.file.Path path;
        String randomString;
        do {
            randomString = RandomStringUtils.randomAlphabetic(10);
            path = FileSystems.getDefault().getPath(directory + randomString + ".png");
        } while (Files.exists(path));

        MatrixToImageWriter.writeToPath(qrMatrix, "PNG", path);
        return "/v1/tokens/barcode/" + randomString + ".png";
    }

	@GET
    @Path("/{name}/{uid}/{numberOfRemainingTokens}")
	public Response requestTokens(@PathParam("name") String username,
                          @PathParam("uid") String userId,
                          @PathParam("numberOfRemainingTokens") int number) {

		try {
            ArrayList<String> tokenString = new ArrayList<>();//this.barcodeProvider.getTokens(username, userId, number);
            Set<TokenBarcodePathPair> finalTokens = new HashSet<>();
            for (String string : tokenString) {
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                try {
                    BitMatrix bitMatrix = qrCodeWriter.encode(string, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
                    finalTokens.add(new TokenBarcodePathPair(string, saveQRToDisk(bitMatrix)));
                } catch (WriterException e) {
                    //throw new QRException(e.getMessage(), e);
                }
            }

            return Response.ok("customer").build();
        }catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Caught exception").build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Caught exception").build();
        }

	}


    @POST
    public Response useToken(@HeaderParam(value = "Authorization") String token) {
        boolean success;
        try {
            success = true;//this.barcodeProvider.useToken(token);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        if (success)
            return Response.status(Response.Status.OK).build();
        else
            return Response.status(Response.Status.UNAUTHORIZED).build();
    }

	@GET
    @Path("/barcode/{fileName}")
    public Response getBarcode(@PathParam("fileName") String fileName) {
        String path = System.getProperty("user.dir") + IMAGE_DIR + fileName;
        Path filePath = (Path) FileSystems.getDefault().getPath(path);
        File temp = new File(String.valueOf(filePath));

        if(temp.exists()){
			return Response.status(Response.Status.OK).header("content-disposition",
					"attachment; filename=\"" + fileName + "\"").build();
		} else {
            return Response.status(Response.Status.BAD_REQUEST).entity("File not found").build();
        }
    }

}
