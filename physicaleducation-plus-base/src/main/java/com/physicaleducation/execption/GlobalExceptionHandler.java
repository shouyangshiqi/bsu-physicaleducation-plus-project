package com.physicaleducation.execption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author khran
 * @version 1.0
 * @description  异常处理捕获
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(PEPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(PEPlusException e){
        log.error("【系统异常】{}", e.getErrMessage());
        return new RestErrorResponse(e.getErrMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e){
        log.error("【系统异常】{}", e.getMessage(), e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }
}
