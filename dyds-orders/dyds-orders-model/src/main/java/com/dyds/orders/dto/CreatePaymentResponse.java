package com.dyds.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Data
public class CreatePaymentResponse implements Serializable {
    private int code;
    private String message;
    //二维码
    private String qrcode;
    private Data data;

    // Getters and Setters

    @AllArgsConstructor
    @lombok.Data
    public static class Data implements Serializable{
        private long paymentId;

        public Data(Long id) {
            this.paymentId = id;
        }

    }
}
