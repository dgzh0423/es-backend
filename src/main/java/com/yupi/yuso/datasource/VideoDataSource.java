package com.yupi.yuso.datasource;

// [加入编程导航](https://www.code-nav.cn/) 入门捷径+交流答疑+项目实战+求职指导，帮你自学编程不走弯路

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 视频数据源
 *
 * @author 15304
 *
 */
@Component
public class VideoDataSource implements DataSource<Object> {

    @Override
    public Page<Object> doSearch(String searchText, long pageNum, long pageSize) {
        return null;
    }

    @Override
    public List<String> getSearchPrompt(String keyword) {
        return Collections.emptyList();
    }
}
