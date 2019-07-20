package com.ccnu.bbs.exception;

import com.ccnu.bbs.enums.ResultEnum;
import lombok.Data;

@Data
public class BBSException extends RuntimeException {

    private Integer code;

    public BBSException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());

        this.code = resultEnum.getCode();
    }

    public BBSException(Integer code, String message){
        super(message);
        this.code = code;
    }
}
