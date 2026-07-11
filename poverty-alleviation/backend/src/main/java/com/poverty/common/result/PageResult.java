package com.poverty.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回结果
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> records;
    private long total;
    private long pages;
    private long current;
    private long size;

    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        return result;
    }

    public static <T> PageResult<T> of(IPage<T> iPage) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(iPage.getRecords());
        result.setTotal(iPage.getTotal());
        result.setPages(iPage.getPages());
        result.setCurrent(iPage.getCurrent());
        result.setSize(iPage.getSize());
        return result;
    }
}
