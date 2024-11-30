package com.physicaleducation.execption;

import lombok.Data;
import lombok.ToString;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Data
@ToString
public class PEPlusException extends RuntimeException{

    private String errMessage;

    public PEPlusException() {
        super();
    }

    public PEPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public static void cast(CommonError commonError){
        throw new PEPlusException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new PEPlusException(errMessage);
    }
}
