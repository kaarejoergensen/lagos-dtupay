package com.dtupay.dtupayapi.customer.application;

import clients.BankClient;
import clients.TokenClient;
import com.dtupay.dtupayapi.customer.models.TokenBarcodePathPair;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import exceptions.ClientException;
import models.Transaction;
import org.apache.commons.lang3.RandomStringUtils;

import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomerUtils {

    /*

    Help class for the customer endpoint for more easy testing of the functinality of the method itself
     */

    private final String IMAGE_DIR = "/images/";
    private final int QR_SIZE = 300;
    private TokenClient tokenClient;
    private BankClient bankClient;


    public CustomerUtils(TokenClient tokenClient, BankClient bankClient){
        this.tokenClient = tokenClient;
        this.bankClient = bankClient;
    }


    public  Set<TokenBarcodePathPair> requestTokens(String username,
                                                    String userId,
                                                    int tokenCount) throws WriterException, IOException, ClientException{
        try {
            if (this.bankClient.getAccount(userId) == null) {
                throw new IllegalArgumentException("User could not be found.");
            }
        } catch (ClientException e) {
            throw new IllegalArgumentException("User could not be found.");
        }
        Set<String> s = tokenClient.getTokens(username, userId, tokenCount);
        Set<TokenBarcodePathPair> finalTokens = new HashSet<>();

        for (String string : s) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = qrCodeWriter.encode(string, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
                finalTokens.add(new TokenBarcodePathPair(string, saveQRToDisk(bitMatrix)));
            } catch (WriterException e) {
                throw new WriterException();
            } catch (IOException e) {
                throw new IOException();
            }
        }
        return finalTokens;
    }

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
        return "/v1/customer/barcode/" + randomString + ".png";
    }

    public Response getBarcodeImage(String filename){
        String path = System.getProperty("user.dir") + IMAGE_DIR + filename;
        Path filePath = (Path) FileSystems.getDefault().getPath(path);
        File temp = new File(String.valueOf(filePath));
        if(temp.exists()){
            return Response.status(Response.Status.OK).header("content-disposition",
                    "attachment; filename=\"" + filename + "\"").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("File not found").build();
        }
    }

    public List<Transaction> getTransactions(String userId, String fromDate, String toDate) throws ParseException, ClientException{
        try {
            List<Transaction> trans = bankClient.getAccount(userId).getTransactions();

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

            Date raw_fromdate = df.parse(fromDate);
            Calendar parsed_fromDate = new GregorianCalendar();
            parsed_fromDate.setTime(raw_fromdate);

            Date raw_todate = df.parse(toDate);
            Calendar parsed_toDate = new GregorianCalendar();
            parsed_toDate.setTime(raw_todate);

            for(Transaction t : trans){
                long mill = t.getTime().getMillisecond();
                if(mill < parsed_toDate.getTimeInMillis() || mill > parsed_toDate.getTimeInMillis()){
                    trans.remove(t);
                }
            }
            return trans;
        } catch (ClientException e) {
            throw e;
        } catch (ParseException e) {
            throw new ParseException("Could not parse date", 0);
        }
    }

}



















