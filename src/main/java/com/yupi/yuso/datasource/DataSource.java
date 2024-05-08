package com.yupi.yuso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 数据源接口（新接入的数据源必须实现）
 *
 * @author 15304
 * @param <T>
 * 
 */
public interface DataSource<T> {

    /**
     * 搜索
     *
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);

    /**
     * 搜索词提示
     * @param keyword
     * @return
     */
    List<String> getSearchPrompt(String keyword);
}
