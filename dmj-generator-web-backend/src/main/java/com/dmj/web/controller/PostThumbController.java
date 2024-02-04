package com.dmj.web.controller;

import com.dmj.web.common.BaseResponse;
import com.dmj.web.common.ErrorCode;
import com.dmj.web.common.ResultUtils;
import com.dmj.web.exception.BusinessException;
import com.dmj.web.model.dto.postthumb.PostThumbAddRequest;
import com.dmj.web.model.entity.User;
import com.dmj.web.service.PostThumbService;
import com.dmj.web.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子点赞接口
 *
 * @author  dmj
 */
@RestController
@RequestMapping("/post_thumb")
@Slf4j
@CrossOrigin
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
            HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }

}
