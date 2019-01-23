package com.dtupay.dtupayapi.customer.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
@author Kåre
 */

@Data
@AllArgsConstructor
public class TokenBarcodePathPair {
    private String token;
    private String barcodePath;
}
