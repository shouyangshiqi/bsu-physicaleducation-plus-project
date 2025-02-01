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
public class CreateOrderResponse implements Serializable {
    private int code;
    private String message;
    private Data data;

    // Getters and Setters

    @AllArgsConstructor
    @lombok.Data
    public static class Data implements Serializable{
        private long orderId;

        public Data(Long id) {
            this.orderId = id;
        }

        // Getters and Setters
    }
}
