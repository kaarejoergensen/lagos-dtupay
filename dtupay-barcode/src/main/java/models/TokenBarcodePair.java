package models;

import com.google.zxing.common.BitMatrix;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenBarcodePair {
    private String token;
    private BitMatrix barcode;
}
