package com.dtupay.dtupayapi.dtupayapi.rest.endpoints;


import barcode.BarcodeProvider;
import com.dtupay.dtupayapi.dtupayapi.rest.models.TokenBarcodePathPair;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import exceptions.QRException;
import models.TokenBarcodePair;
import org.apache.commons.lang3.RandomStringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;


@Path("/v1/customer")
public class CustomerEndpoint {

	private BarcodeProvider barcodeProvider = new BarcodeProvider();

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
            Set<TokenBarcodePair> tokens = this.barcodeProvider.getTokens(username, userId, number);
            Set<TokenBarcodePathPair> finalTokens = new HashSet<>();
            for (TokenBarcodePair token : tokens) {
                finalTokens.add(new TokenBarcodePathPair(token.getToken(), saveQRToDisk(token.getBarcode())));
            }
        }catch (QRException | IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		return Response.ok("customer").build();
	}


    @POST
    public Response useToken(@HeaderParam(value = "Authorization") String token) {
        boolean success;
        try {
            success = this.barcodeProvider.useToken(token);
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
