package com.dyds.orders.utils.execption;

import lombok.Data;
import lombok.ToString;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Data
@ToString
public class DydsException extends RuntimeException{

    private String errMessage;

    public DydsException() {
        super();
    }

    public DydsException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public static void cast(CommonError commonError){
        throw new DydsException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new DydsException(errMessage);
    }
}
