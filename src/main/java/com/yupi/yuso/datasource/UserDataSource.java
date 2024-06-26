package com.yupi.yuso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuso.model.dto.user.UserQueryRequest;
import com.yupi.yuso.model.vo.UserVO;
import com.yupi.yuso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 用户服务实现
 *
 * @author 15304 
 *  
 */
@Service
@Slf4j
public class UserDataSource implements DataSource<UserVO> {

    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);
        return userService.listUserVOByPage(userQueryRequest);
    }

    @Override
    public List<String> getSearchPrompt(String keyword) {
        return Collections.emptyList();
    }
}
