package com.physicaleducation.model;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.ToString;

/**
 * @author khean
 * @version 1.0
 */

@Data
@ToString
public class PageParams {

    @ApiParam("当前页码")
    // 当前页码
    private Long pageNo = 1L;
    @ApiParam("每页显示数")
    // 每页显示数
    private Long pageSize = 1L;

    public PageParams() {
    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
