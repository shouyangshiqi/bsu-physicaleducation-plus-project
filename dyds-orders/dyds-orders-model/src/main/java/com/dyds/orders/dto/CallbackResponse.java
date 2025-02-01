package com.dyds.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Data
@AllArgsConstructor
public class CallbackResponse {
    private int code;
    private String message;
    private String data;
}
