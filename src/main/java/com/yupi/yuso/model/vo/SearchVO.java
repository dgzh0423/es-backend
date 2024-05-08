package com.yupi.yuso.model.vo;

import com.yupi.yuso.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索
 *
 * @author 15304 
 *  
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userList;

    private List<PostVO> postList;

    private List<Picture> pictureList;

    private List<?> dataList;

    private long total;

    private static final long serialVersionUID = 1L;

}
