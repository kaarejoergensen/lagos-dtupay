package com.dtupay.dtupayapi.dtupayapi.rest.endpoints;


import barcode.BarcodeProvider;
import com.dtupay.dtupayapi.dtupayapi.rest.models.TokenBarcodePathPair;
import com.dtupay.dtupayapi.dtupayapi.rest.utils.QRMapper;
import exceptions.QRException;
import io.jsonwebtoken.io.IOException;
import models.TokenBarcodePair;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.file.FileSystems;
import java.util.HashSet;
import java.util.Set;


@Path("/v1/customer")
public class CustomerEndpoint {

	private BarcodeProvider barcodeProvider = new BarcodeProvider();

	@GET
    @Path("/{name}/{uid}/{numberOfRemainingTokens}")
	public Response requestTokens(@PathParam("name") String username,
                          @PathParam("uid") String userId,
                          @PathParam("numberOfRemainingTokens") int number) {
        try {
            Set<TokenBarcodePair> tokens = this.barcodeProvider.getTokens(username, userId, number);
            Set<TokenBarcodePathPair> finalTokens = new HashSet<>();
            for (TokenBarcodePair token : tokens) {
                finalTokens.add(new TokenBarcodePathPair(token.getToken(), QRMapper.saveQRToDisk(token.getBarcode())));
            }
        }catch (QRException | IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok("customer").build();
	}


    @POST
    public Response useToken(String token) {
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
        String path = System.getProperty("user.dir") + QRMapper.IMAGE_DIR + fileName;
        Path filePath = (Path) FileSystems.getDefault().getPath(path);
        File temp = new File(String.valueOf(filePath));

        if(temp.exists()){
            try {
                return Response.status(Response.Status.OK).header("content-disposition",
                        "attachment; filename=\"" + fileName + "\"").build();
            } catch (IOException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("File not found").build();
        }
    }

}
