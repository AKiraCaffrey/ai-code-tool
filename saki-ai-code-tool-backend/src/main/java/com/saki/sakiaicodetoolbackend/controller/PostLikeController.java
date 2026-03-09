package com.saki.sakiaicodetoolbackend.controller;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.saki.sakiaicodetoolbackend.model.entity.PostLike;
import com.saki.sakiaicodetoolbackend.service.PostLikeService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 帖子点赞 控制层。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@RestController
@RequestMapping("/postLike")
public class PostLikeController {

    @Resource
    private PostLikeService postLikeService;

}
