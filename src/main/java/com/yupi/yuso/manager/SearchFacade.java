package com.yupi.yuso.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuso.common.ErrorCode;
import com.yupi.yuso.datasource.*;
import com.yupi.yuso.exception.BusinessException;
import com.yupi.yuso.model.dto.search.SearchPromptRequest;
import com.yupi.yuso.model.dto.search.SearchRequest;
import com.yupi.yuso.model.dto.user.UserQueryRequest;
import com.yupi.yuso.model.entity.Picture;
import com.yupi.yuso.model.enums.SearchTypeEnum;
import com.yupi.yuso.model.vo.PostVO;
import com.yupi.yuso.model.vo.SearchVO;
import com.yupi.yuso.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 *
 * @author 15304 
 *  
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;


    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        //ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        String searchText = searchRequest.getSearchText();
        // 当前页号，页大小
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();
        // 搜索出所有数据
        if (searchTypeEnum == null) {
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                return userDataSource.doSearch(searchText, current, pageSize);
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                // 获取 HttpServletRequest 对象并存储到 ThreadLocal 中
                RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
                try {
                    // 在异步执行的线程中取出 HttpServletRequest 对象并使用
                    return  postDataSource.doSearch(searchText, current, pageSize);
                } finally {
                    // 清空 ThreadLocal 中存储的 HttpServletRequest 对象
                    RequestContextHolder.resetRequestAttributes();
                }
            });

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> pictureDataSource.doSearch(searchText, 1, 10));

            CompletableFuture.allOf(userTask, postTask, pictureTask).join();
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            searchVO.setTotal(page.getTotal());
            return searchVO;
        }
    }

    public List<String> getSearchPrompt(SearchPromptRequest searchPromptRequest) {
        String type = searchPromptRequest.getType();
        String keyword = searchPromptRequest.getSearchText();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        if (searchTypeEnum != null) {
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            // 搜索建议统一返回字符串，就不需要泛型了
            return dataSource.getSearchPrompt(keyword);
        } else
            //不在我们标签里的，就不用提供搜索建议服务
        {
            return Collections.emptyList();
        }
    }
}
