package com.saki.sakiaicodetoolbackend.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.saki.sakiaicodetoolbackend.annotation.AuthCheck;
import com.saki.sakiaicodetoolbackend.common.BaseResponse;
import com.saki.sakiaicodetoolbackend.common.ResultUtils;
import com.saki.sakiaicodetoolbackend.constant.UserConstant;
import com.saki.sakiaicodetoolbackend.exception.BusinessException;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.model.dto.postcategory.PostCategoryAddRequest;
import com.saki.sakiaicodetoolbackend.model.dto.postcategory.PostCategoryDeleteRequest;
import com.saki.sakiaicodetoolbackend.model.dto.postcategory.PostCategoryQueryRequest;
import com.saki.sakiaicodetoolbackend.model.dto.postcategory.PostCategoryUpdateRequest;
import com.saki.sakiaicodetoolbackend.model.entity.PostCategory;
import com.saki.sakiaicodetoolbackend.model.vo.PostCategoryVO;
import com.saki.sakiaicodetoolbackend.service.PostCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子分类控制器
 * <p>
 * 提供帖子分类的增删改查接口
 *
 * @author Neal Caffrey
 * @version 1.0
 * @since 2026-03-04
 */
@RestController
@RequestMapping("/postCategory")
public class PostCategoryController {

    @Resource
    private PostCategoryService postCategoryService;

    /**
     * 添加帖子分类
     * <p>
     * 仅管理员可调用，创建新的帖子分类
     *
     * @param postCategoryAddRequest 添加请求参数
     * @return 新创建的分类ID
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addPostCategory(@RequestBody PostCategoryAddRequest postCategoryAddRequest) {
        ThrowUtils.throwIf(postCategoryAddRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");

        String name = postCategoryAddRequest.getName();
        ThrowUtils.throwIf(name == null || name.trim().isEmpty(), ErrorCode.PARAMS_ERROR, "分类名称不能为空");

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("name", name.trim())
                .eq("is_delete", 0);
        long count = postCategoryService.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "分类名称已存在");

        PostCategory postCategory = new PostCategory();
        postCategory.setName(name.trim());
        postCategory.setSortOrder(postCategoryAddRequest.getSortOrder() != null ? postCategoryAddRequest.getSortOrder() : 0);

        boolean result = postCategoryService.save(postCategory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加分类失败");

        return ResultUtils.success(postCategory.getId());
    }

    /**
     * 删除帖子分类
     * <p>
     * 仅管理员可调用，逻辑删除指定分类
     *
     * @param postCategoryDeleteRequest 删除请求参数
     * @return 删除是否成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deletePostCategory(@RequestBody PostCategoryDeleteRequest postCategoryDeleteRequest) {
        ThrowUtils.throwIf(postCategoryDeleteRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");

        Long id = postCategoryDeleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "分类ID无效");

        PostCategory postCategory = postCategoryService.getById(id);
        ThrowUtils.throwIf(postCategory == null, ErrorCode.NOT_FOUND_ERROR, "分类不存在");

        boolean result = postCategoryService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除分类失败");

        return ResultUtils.success(true);
    }

    /**
     * 更新帖子分类
     * <p>
     * 仅管理员可调用，更新指定分类信息
     *
     * @param postCategoryUpdateRequest 更新请求参数
     * @return 更新是否成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePostCategory(@RequestBody PostCategoryUpdateRequest postCategoryUpdateRequest) {
        ThrowUtils.throwIf(postCategoryUpdateRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");

        Long id = postCategoryUpdateRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "分类ID无效");

        PostCategory existCategory = postCategoryService.getById(id);
        ThrowUtils.throwIf(existCategory == null, ErrorCode.NOT_FOUND_ERROR, "分类不存在");

        String name = postCategoryUpdateRequest.getName();
        if (name != null && !name.trim().isEmpty()) {
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq("name", name.trim())
                    .eq("is_delete", 0)
                    .ne("id", id);
            long count = postCategoryService.count(queryWrapper);
            ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "分类名称已存在");
        }

        PostCategory postCategory = new PostCategory();
        postCategory.setId(id);
        postCategory.setName(name != null ? name.trim() : null);
        postCategory.setSortOrder(postCategoryUpdateRequest.getSortOrder());

        boolean result = postCategoryService.updateById(postCategory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新分类失败");

        return ResultUtils.success(true);
    }

    /**
     * 根据ID获取帖子分类
     * <p>
     * 所有用户均可访问，获取指定分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public BaseResponse<PostCategoryVO> getPostCategory(@PathVariable Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "分类ID无效");

        PostCategory postCategory = postCategoryService.getById(id);
        ThrowUtils.throwIf(postCategory == null, ErrorCode.NOT_FOUND_ERROR, "分类不存在");

        PostCategoryVO vo = new PostCategoryVO();
        vo.setId(postCategory.getId());
        vo.setName(postCategory.getName());
        vo.setSortOrder(postCategory.getSortOrder());

        return ResultUtils.success(vo);
    }

    /**
     * 获取帖子分类列表
     * <p>
     * 所有用户均可访问，返回所有未删除的分类，按排序值升序排列
     *
     * @return 分类列表
     */
    @GetMapping("/list")
    public BaseResponse<List<PostCategoryVO>> listPostCategory() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("is_delete", 0)
                .orderBy("sort_order", true)
                .orderBy("id", true);
        List<PostCategory> categories = postCategoryService.list(queryWrapper);
        List<PostCategoryVO> categoryVOList = categories.stream().map(category -> {
            PostCategoryVO vo = new PostCategoryVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setSortOrder(category.getSortOrder());
            return vo;
        }).collect(Collectors.toList());
        return ResultUtils.success(categoryVOList);
    }

    /**
     * 分页获取帖子分类列表
     * <p>
     * 仅管理员可访问，支持分页查询和条件筛选
     *
     * @param postCategoryQueryRequest 查询请求参数
     * @return 分页结果
     */
    @PostMapping("/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<PostCategoryVO>> listPostCategoryByPage(@RequestBody PostCategoryQueryRequest postCategoryQueryRequest) {
        ThrowUtils.throwIf(postCategoryQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");

        int pageNum = postCategoryQueryRequest.getPageNum();
        int pageSize = postCategoryQueryRequest.getPageSize();
        Long id = postCategoryQueryRequest.getId();
        String name = postCategoryQueryRequest.getName();
        String sortField = postCategoryQueryRequest.getSortField();
        String sortOrder = postCategoryQueryRequest.getSortOrder();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("is_delete", 0);

        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like("name", name.trim());
        }

        if (sortField != null && !sortField.trim().isEmpty()) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            queryWrapper.orderBy("sort_order", true);
            queryWrapper.orderBy("id", true);
        }

        Page<PostCategory> postCategoryPage = postCategoryService.page(Page.of(pageNum, pageSize), queryWrapper);

        Page<PostCategoryVO> voPage = new Page<>();
        voPage.setPageNumber(postCategoryPage.getPageNumber());
        voPage.setPageSize(postCategoryPage.getPageSize());
        voPage.setTotalRow(postCategoryPage.getTotalRow());
        voPage.setRecords(postCategoryPage.getRecords().stream().map(category -> {
            PostCategoryVO vo = new PostCategoryVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setSortOrder(category.getSortOrder());
            return vo;
        }).collect(Collectors.toList()));

        return ResultUtils.success(voPage);
    }
}
