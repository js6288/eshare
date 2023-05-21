package com.shu.eshare.exception;

import com.shu.eshare.common.BaseResponse;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author ljs
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException:"+ e.getMessage(),e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse ExceptionHandler(Exception e){
        log.error("Exception:"+e.getMessage(),e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"系统繁忙请稍后重试","系统繁忙请稍后重试");
    }
}
