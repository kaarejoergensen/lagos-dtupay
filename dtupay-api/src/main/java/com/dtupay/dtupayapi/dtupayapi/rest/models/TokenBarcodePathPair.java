package com.dtupay.dtupayapi.dtupayapi.rest.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenBarcodePathPair {
    private String token;
    private String barcodePath;
}
