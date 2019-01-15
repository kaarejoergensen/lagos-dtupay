package com.dtupay.dtupayapi.dtupayapi.rest.utils;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class QRMapper {
    public static final String IMAGE_DIR = "/images/";

    public static String saveQRToDisk(BitMatrix qrMatrix) throws IOException {
        String directory = System.getProperty("user.dir") + IMAGE_DIR;

        Path directoryPath = FileSystems.getDefault().getPath(directory);
        if (Files.notExists(directoryPath)) Files.createDirectory(directoryPath);
        Path path;
        String randomString;
        do {
            randomString = RandomStringUtils.randomAlphabetic(10);
            path = FileSystems.getDefault().getPath(directory + randomString + ".png");
        } while (Files.exists(path));

        MatrixToImageWriter.writeToPath(qrMatrix, "PNG", path);
        return "/v1/tokens/barcode/" + randomString + ".png";
    }
}
