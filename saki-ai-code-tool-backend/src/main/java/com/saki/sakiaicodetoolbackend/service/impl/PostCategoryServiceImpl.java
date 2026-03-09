package com.saki.sakiaicodetoolbackend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.saki.sakiaicodetoolbackend.model.entity.PostCategory;
import com.saki.sakiaicodetoolbackend.mapper.PostCategoryMapper;
import com.saki.sakiaicodetoolbackend.service.PostCategoryService;
import org.springframework.stereotype.Service;

/**
 * 帖子分类 服务层实现。
 *
 * @author Neal Caffrey
 */
@Service
public class PostCategoryServiceImpl extends ServiceImpl<PostCategoryMapper, PostCategory>  implements PostCategoryService{

}
