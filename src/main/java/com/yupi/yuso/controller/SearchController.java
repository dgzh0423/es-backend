package com.yupi.yuso.controller;

import com.yupi.yuso.common.BaseResponse;
import com.yupi.yuso.common.ResultUtils;
import com.yupi.yuso.manager.SearchFacade;
import com.yupi.yuso.model.dto.search.SearchPromptRequest;
import com.yupi.yuso.model.dto.search.SearchRequest;
import com.yupi.yuso.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 聚合查询接口
 *
 * @author 15304 
 *  
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }

    @GetMapping("/getSearchPrompt")
    public BaseResponse<List<String>> getSearchPrompt(SearchPromptRequest searchPromptRequest){
        List<String> searchPrompt = searchFacade.getSearchPrompt(searchPromptRequest);
        return ResultUtils.success(searchPrompt);
    }

}
