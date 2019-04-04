package com.osidocker.open.micro.lock;

import com.osidocker.open.micro.vo.CoreException;
import com.osidocker.open.micro.vo.ServiceExceptionEnum;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 9:56
 * @Copyright: © Caoyj
 */
public class GunsLockException extends CoreException {

    public GunsLockException(ServiceExceptionEnum serviceExceptionEnum) {
        super(serviceExceptionEnum);
    }

    public GunsLockException(Exception e){
        super(new ServiceExceptionEnum() {
            @Override
            public Integer getCode() {
                return 777;
            }

            @Override
            public String getMessage() {
                return e.getMessage();
            }
        });
    }
}
