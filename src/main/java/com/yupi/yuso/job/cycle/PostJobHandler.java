package com.yupi.yuso.job.cycle;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yupi.yuso.esdao.PostEsDao;
import com.yupi.yuso.mapper.PostMapper;
import com.yupi.yuso.model.dto.post.PostEsDTO;
import com.yupi.yuso.model.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步帖子到 es的JobHandler
 * @author 15304
 */
@Component
@Slf4j
public class PostJobHandler {

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostEsDao postEsDao;

    private static final long FIVE_MINUTES = 5 * 60 * 1000L;


    /**
     * 定时调度在xxl-admin中设置
     */
    @XxlJob("postJobHandler")
    public void run() throws Exception {
        // 使用 XxlJobHelper.log()才能通知到admin中
        XxlJobHelper.log("IncSyncPostToEs is running !");

        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - FIVE_MINUTES);
        List<Post> postList = postMapper.listPostWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(postList)) {
            log.info("no inc post");
            return;
        }
        List<PostEsDTO> postEsDTOList = postList.stream()
                .map(PostEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("IncSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("IncSyncPostToEs end, total {}", total);
    }

}
