/*
 *=======================================================================================
 *
 *
 *
 *
 *
 *
 *
 *
 *======================================================================================
 */
package org.osidocker.open.web.controller;

import org.osidocker.open.service.BookService;
import org.osidocker.open.web.executor.OneLevelAsyncContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * @类功能说明：
 * @类创建者： 曹杨杰
 * @类创建日期： 创建于：21:18 2017/5/7
 * @类修改说明：
 * @公司说明： 深圳原形信息技术有限公司
 * @类修改者： 曹杨杰
 * @类修改时间： 修改于：21:18 2017/5/7
 * @创建版本： V1.0.0
 */
@Controller
public class BookController {

    @Autowired
    protected OneLevelAsyncContext oneLevelAsyncContext;
    private BookService bookService;

    public void getBook(HttpServletRequest request,final Long skuId,final Integer cat1,final Integer cat2) throws Exception {
        oneLevelAsyncContext.submitFuture(request,()->bookService.getBook(skuId,cat1,cat2));
    }
}