/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service.exceptions;

import com.osidocker.open.micro.model.ValidateInfo;
import com.osidocker.open.micro.vo.CoreException;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 18:05 2018/8/31
 * @修改说明：
 * @修改日期： 18:05 2018/8/31
 * @版本号： V1.0.0
 */
public class PythonDataException extends CoreException {
    public static PythonDataException BANK_FLOW_RECORD_EXCEPTION = new PythonDataException(-1,"获取用户的银行流水记录为空!");
    public static PythonDataException BANK_INTERRUPT_FILE_EXCEPTION = new PythonDataException(-2,"生成PDF文件被中断!");
    public static PythonDataException BANK_GENERATE_FILE_EXCEPTION = new PythonDataException(-3,"生成PDF文件执行异常!");
    public static PythonDataException BANK_GENERATE_TIME_OUT_EXCEPTION = new PythonDataException(-4,"生成PDF文件执行超时异常!");
    public static PythonDataException XUEXIN_RECORD_EXCEPTION = new PythonDataException(-5,"获取学信网信息记录失败!");

    private ValidateInfo validateInfo;

    public PythonDataException(int code,Object message){
        this.setCode(code);
        this.setData(message);
    }

    public PythonDataException init(ValidateInfo validateInfo){
        this.validateInfo = validateInfo;
        return this;
    }

    public ValidateInfo getValidateInfo() {
        return validateInfo;
    }
}
