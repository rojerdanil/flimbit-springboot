package com.riseup.flimbit.request;


import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class MovieProfitRequest {
    private int movieId;
    private String totalReturnAmount; // Example: 200000000 for 20 crore
}
