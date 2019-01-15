package barcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import exceptions.QRException;
import models.TokenBarcodePair;
import persistence.Datastore;
import persistence.MemoryDataStore;
import tokens.TokenProvider;

import java.util.HashSet;
import java.util.Set;

public class BarcodeProvider {
    private static final int QR_SIZE = 300;

    private TokenProvider tokenProvider;
    private Datastore datastore;

    public BarcodeProvider() {
        this.tokenProvider = new TokenProvider();
        this.datastore = new MemoryDataStore(tokenProvider);
    }

    public BarcodeProvider(TokenProvider tokenProvider, Datastore datastore) {
        this.tokenProvider = tokenProvider;
        this.datastore = datastore;
    }

    public Set<TokenBarcodePair> getTokens(String userName, String userId, int numberOfTokens) throws QRException {
        if (numberOfTokens < 1 || numberOfTokens > 5)
            throw new IllegalArgumentException("The number of tokens must be between 1 and 5");
        int numberOfUnusedTokens = this.datastore.getNumberOfUnusedTokens(userName);
        if (numberOfUnusedTokens > 1)
            throw new IllegalArgumentException("Number of unused tokens is more than 1");
        Set<TokenBarcodePair> tokens = new HashSet<>();
        int count = datastore.getTotalNumberOfTokensIssued();
        for (int i = 0; i < numberOfTokens; i++) {
            String token = this.tokenProvider.issueToken(userName, userId, count++);
            BitMatrix barcode = this.generateQRCode(token);
            tokens.add(new TokenBarcodePair(token, barcode));
        }
        datastore.setTotalNumberOfTokensIssued(count);
        this.datastore.addTokens(tokens.size(), userName);
        return tokens;
    }

    public boolean useToken(String tokenString) {
        boolean tokenValid = this.tokenProvider.checkToken(tokenString) && !this.datastore.isTokenUsed(tokenString);
        if (tokenValid)
            this.datastore.useToken(tokenString);
        return tokenValid;
    }

    private BitMatrix generateQRCode(String tokenString) throws QRException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            return qrCodeWriter.encode(tokenString, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
            //ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            //MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            //return pngOutputStream.toByteArray();
        } catch (WriterException e) {
            throw new QRException(e.getMessage(), e);
        }

    }
}
