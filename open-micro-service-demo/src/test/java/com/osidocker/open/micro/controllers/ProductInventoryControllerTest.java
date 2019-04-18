package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.base.BaseJunit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductInventoryControllerTest extends BaseJunit {

//    @InjectMocks ProductInventoryController controller;

//    @Mock
//    private IProductInventoryService productInventoryService;
//    @Mock
//    private IRequestAsyncProcessService requestAsyncProcessService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetProductInventory() throws Exception {
//        doNothing().when(requestAsyncProcessService).process(any());
//        when(productInventoryService.getProductInventoryCache(anyLong())).thenReturn(null);
//        when(productInventoryService.findInventory(anyLong())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/product/get/110"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("productId").value(110))
                .andExpect(jsonPath("inventoryCnt").value(-1L));
    }

    @Test
    public void testUpdateProductInventory() {
    }
}