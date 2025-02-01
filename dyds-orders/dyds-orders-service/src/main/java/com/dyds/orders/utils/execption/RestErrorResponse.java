package com.dyds.orders.utils.execption;

import lombok.Data;

import java.io.Serializable;

/**
 * @author khran
 * @version 1.0
 * @description 返回的异常信息
 */

@Data
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

}
