package com.saki.sakiaicodetoolbackend.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.saki.sakiaicodetoolbackend.common.CursorPage;
import com.saki.sakiaicodetoolbackend.exception.ErrorCode;
import com.saki.sakiaicodetoolbackend.exception.ThrowUtils;
import com.saki.sakiaicodetoolbackend.mapper.PostMapper;
import com.saki.sakiaicodetoolbackend.constant.UserConstant;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostCreateRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostCursorQueryRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostQueryRequest;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostUpdateRequest;
import com.saki.sakiaicodetoolbackend.model.entity.Post;
import com.saki.sakiaicodetoolbackend.model.entity.PostCategory;
import com.saki.sakiaicodetoolbackend.model.entity.PostLike;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.model.vo.PostDetailVO;
import com.saki.sakiaicodetoolbackend.model.vo.PostVO;
import com.saki.sakiaicodetoolbackend.model.vo.UserVO;
import com.saki.sakiaicodetoolbackend.service.PostCategoryService;
import com.saki.sakiaicodetoolbackend.service.PostLikeService;
import com.saki.sakiaicodetoolbackend.service.PostService;
import com.saki.sakiaicodetoolbackend.service.UserService;
import com.saki.sakiaicodetoolbackend.service.CommentService;
import com.saki.sakiaicodetoolbackend.utils.CursorUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 帖子 服务层实现。
 *
 * @author Neal Caffrey
 * @since 2026-3-4
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&[a-zA-Z]+;|&#\\d+;");
    private static final int SUMMARY_LENGTH = 200;
    private static final String SORT_TYPE_HOT = "hot";

    @Resource
    private UserService userService;

    @Resource
    private PostCategoryService postCategoryService;

    @Resource
    private PostLikeService postLikeService;

    @Resource
    private CommentService commentService;

    @Override
    public CursorPage<PostVO> getPostCursorPage(PostCursorQueryRequest request, Long loginUserId) {
        int pageSize = request.getEffectivePageSize();
        String cursor = request.getCursor();
        String sortType = request.getSortType();

        CursorUtils.CursorData cursorData = CursorUtils.decodeCursor(cursor);
        LocalDateTime cursorCreateTime = null;
        Long cursorId = null;
        Long cursorHotScore = null;
        if (cursorData != null) {
            cursorCreateTime = cursorData.getParsedCreateTime();
            cursorId = cursorData.getId();
            cursorHotScore = cursorData.getHotScore();
        }

        boolean isHotSort = SORT_TYPE_HOT.equals(sortType);
        QueryWrapper queryWrapper = buildQueryWrapper(request, cursorCreateTime, cursorId, cursorHotScore, isHotSort);

        List<Post> posts = this.list(queryWrapper.limit(pageSize + 1));

        boolean hasMore = posts.size() > pageSize;
        if (hasMore) {
            posts = posts.subList(0, pageSize);
        }

        List<PostVO> postVOList = convertToPostVOList(posts, loginUserId);

        String nextCursor = null;
        if (hasMore && !posts.isEmpty()) {
            Post lastPost = posts.get(posts.size() - 1);
            if (isHotSort) {
                long hotScore = calculateHotScore(lastPost);
                nextCursor = CursorUtils.encodeCursor(lastPost.getCreateTime(), lastPost.getId(), hotScore);
            } else {
                nextCursor = CursorUtils.encodeCursor(lastPost.getCreateTime(), lastPost.getId());
            }
        }

        CursorPage<PostVO> cursorPage = new CursorPage<>();
        cursorPage.setRecords(postVOList);
        cursorPage.setNextCursor(nextCursor);
        cursorPage.setHasMore(hasMore);

        return cursorPage;
    }

    private QueryWrapper buildQueryWrapper(PostCursorQueryRequest request, LocalDateTime cursorCreateTime, Long cursorId, Long cursorHotScore, boolean isHotSort) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("is_delete", 0);

        if (request.getCategoryId() != null) {
            queryWrapper.eq("category_id", request.getCategoryId());
        }

        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            queryWrapper.like("title", request.getKeyword().trim());
        }

        if (cursorCreateTime != null && cursorId != null) {
            if (isHotSort && cursorHotScore != null) {
                queryWrapper.and("(like_count * 2 + comment_count * 2 + view_count < " + cursorHotScore + 
                        " OR (like_count * 2 + comment_count * 2 + view_count = " + cursorHotScore + 
                        " AND (create_time < '" + cursorCreateTime + "' OR (create_time = '" + cursorCreateTime + "' AND id < " + cursorId + "))))");
            } else {
                queryWrapper.and("(create_time < '" + cursorCreateTime + "' OR (create_time = '" + cursorCreateTime + "' AND id < " + cursorId + "))");
            }
        }

        queryWrapper.orderBy("is_top", false);
        if (isHotSort) {
            queryWrapper.orderBy("(like_count * 2 + comment_count * 2 + view_count)", false);
        }
        queryWrapper.orderBy("create_time", false);
        queryWrapper.orderBy("id", false);

        return queryWrapper;
    }

    private long calculateHotScore(Post post) {
        int likeCount = post.getLikeCount() != null ? post.getLikeCount() : 0;
        int commentCount = post.getCommentCount() != null ? post.getCommentCount() : 0;
        int viewCount = post.getViewCount() != null ? post.getViewCount() : 0;
        return (long) likeCount * 2 + (long) commentCount * 2 + viewCount;
    }

    private List<PostVO> convertToPostVOList(List<Post> posts, Long loginUserId) {
        if (posts.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> userIds = posts.stream().map(Post::getUserId).collect(Collectors.toSet());
        Set<Long> categoryIds = posts.stream().map(Post::getCategoryId).collect(Collectors.toSet());
        Set<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toSet());

        Map<Long, UserVO> userVOMap = getUserVOMap(userIds);
        Map<Long, String> categoryNameMap = getCategoryNameMap(categoryIds);
        Set<Long> likedPostIds = getLikedPostIds(postIds, loginUserId);

        return posts.stream().map(post -> {
            PostVO postVO = new PostVO();
            postVO.setId(post.getId());
            postVO.setUserId(post.getUserId());
            postVO.setCategoryId(post.getCategoryId());
            postVO.setCategoryName(categoryNameMap.getOrDefault(post.getCategoryId(), "未分类"));
            postVO.setTitle(post.getTitle());
            postVO.setFirstImageUrl(post.getFirstImageUrl());
            postVO.setContent(generateSummary(post.getContent()));
            postVO.setViewCount(post.getViewCount());
            postVO.setLikeCount(post.getLikeCount());
            postVO.setCommentCount(post.getCommentCount());
            postVO.setIsTop(post.getIsTop());
            postVO.setCreateTime(post.getCreateTime());
            postVO.setUser(userVOMap.get(post.getUserId()));
            postVO.setIsLiked(likedPostIds.contains(post.getId()));
            return postVO;
        }).collect(Collectors.toList());
    }

    private Map<Long, UserVO> getUserVOMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return new HashMap<>();
        }
        List<User> users = userService.listByIds(userIds);
        List<UserVO> userVOList = userService.getUserVOList(users);
        return userVOList.stream().collect(Collectors.toMap(UserVO::getId, vo -> vo, (v1, v2) -> v1));
    }

    private Map<Long, String> getCategoryNameMap(Set<Long> categoryIds) {
        if (categoryIds.isEmpty()) {
            return new HashMap<>();
        }
        List<PostCategory> categories = postCategoryService.listByIds(categoryIds);
        return categories.stream().collect(Collectors.toMap(PostCategory::getId, PostCategory::getName, (v1, v2) -> v1));
    }

    private Set<Long> getLikedPostIds(Set<Long> postIds, Long loginUserId) {
        if (loginUserId == null || postIds.isEmpty()) {
            return new HashSet<>();
        }
        List<PostLike> postLikes = postLikeService.list(QueryWrapper.create()
                .in("post_id", postIds)
                .eq("user_id", loginUserId)
                .eq("is_delete", 0));
        return postLikes.stream().map(PostLike::getPostId).collect(Collectors.toSet());
    }

    @Override
    public Long createPost(PostCreateRequest request, Long loginUserId) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        String title = request.getTitle();
        String content = request.getContent();
        Long categoryId = request.getCategoryId();

        ThrowUtils.throwIf(title == null || title.trim().isEmpty(), ErrorCode.PARAMS_ERROR, "标题不能为空");
        ThrowUtils.throwIf(title.length() > 512, ErrorCode.PARAMS_ERROR, "标题长度不能超过512个字符");
        ThrowUtils.throwIf(content == null || content.trim().isEmpty(), ErrorCode.PARAMS_ERROR, "内容不能为空");
        ThrowUtils.throwIf(categoryId == null, ErrorCode.PARAMS_ERROR, "分类不能为空");

        PostCategory category = postCategoryService.getById(categoryId);
        ThrowUtils.throwIf(category == null, ErrorCode.PARAMS_ERROR, "分类不存在");

        String firstImageUrl = extractFirstImageUrl(content);

        Post post = new Post();
        post.setUserId(loginUserId);
        post.setCategoryId(categoryId);
        post.setTitle(title.trim());
        post.setContent(content);
        post.setFirstImageUrl(firstImageUrl);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setIsTop(0);

        boolean result = this.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建帖子失败");

        return post.getId();
    }

    private String extractFirstImageUrl(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String generateSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        String text = HTML_TAG_PATTERN.matcher(content).replaceAll("");
        text = HTML_ENTITY_PATTERN.matcher(text).replaceAll("");
        text = text.replaceAll("\\s+", " ").trim();
        if (text.length() <= SUMMARY_LENGTH) {
            return text;
        }
        return text.substring(0, SUMMARY_LENGTH) + "...";
    }

    @Override
    public PostDetailVO getPostDetail(Long postId, Long loginUserId) {
        ThrowUtils.throwIf(postId == null || postId <= 0, ErrorCode.PARAMS_ERROR, "帖子ID无效");

        Post post = this.getById(postId);
        ThrowUtils.throwIf(post == null || post.getIsDelete() == 1, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");

        post.setViewCount(post.getViewCount() + 1);
        this.updateById(post);

        PostDetailVO postDetailVO = new PostDetailVO();
        postDetailVO.setId(post.getId());
        postDetailVO.setUserId(post.getUserId());
        postDetailVO.setCategoryId(post.getCategoryId());
        postDetailVO.setTitle(post.getTitle());
        postDetailVO.setContent(post.getContent());
        postDetailVO.setFirstImageUrl(post.getFirstImageUrl());
        postDetailVO.setViewCount(post.getViewCount());
        postDetailVO.setLikeCount(post.getLikeCount());
        postDetailVO.setCommentCount(post.getCommentCount());
        postDetailVO.setIsTop(post.getIsTop());
        postDetailVO.setCreateTime(post.getCreateTime());

        User user = userService.getById(post.getUserId());
        if (user != null) {
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUserName(user.getUserName());
            userVO.setUserAvatar(user.getUserAvatar());
            userVO.setUserProfile(user.getUserProfile());
            postDetailVO.setUser(userVO);
        }

        PostCategory category = postCategoryService.getById(post.getCategoryId());
        if (category != null) {
            postDetailVO.setCategoryName(category.getName());
        }

        boolean isLiked = false;
        if (loginUserId != null) {
            isLiked = postLikeService.isLiked(postId, loginUserId);
        }
        postDetailVO.setIsLiked(isLiked);

        return postDetailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likePost(Long postId, Long loginUserId) {
        ThrowUtils.throwIf(postId == null || postId <= 0, ErrorCode.PARAMS_ERROR, "帖子ID无效");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Post post = this.getById(postId);
        ThrowUtils.throwIf(post == null || post.getIsDelete() == 1, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");

        PostLike existLike = postLikeService.getOne(QueryWrapper.create()
                .eq("post_id", postId)
                .eq("user_id", loginUserId));

        postLikeService.like(postId, loginUserId);

        if (existLike == null || existLike.getIsDelete() == 1) {
            post.setLikeCount(post.getLikeCount() + 1);
            this.updateById(post);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikePost(Long postId, Long loginUserId) {
        ThrowUtils.throwIf(postId == null || postId <= 0, ErrorCode.PARAMS_ERROR, "帖子ID无效");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Post post = this.getById(postId);
        ThrowUtils.throwIf(post == null || post.getIsDelete() == 1, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");

        boolean wasLiked = postLikeService.isLiked(postId, loginUserId);
        postLikeService.unlike(postId, loginUserId);

        if (wasLiked) {
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            this.updateById(post);
        }
    }

    @Override
    public boolean updatePost(PostUpdateRequest request, Long loginUserId) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Long postId = request.getId();
        ThrowUtils.throwIf(postId == null || postId <= 0, ErrorCode.PARAMS_ERROR, "帖子ID无效");

        Post post = this.getById(postId);
        ThrowUtils.throwIf(post == null || post.getIsDelete() == 1, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");

        User loginUser = userService.getById(loginUserId);
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isOwner = post.getUserId().equals(loginUserId);
        ThrowUtils.throwIf(!isAdmin && !isOwner, ErrorCode.NO_AUTH_ERROR, "无权编辑该帖子");

        String title = request.getTitle();
        String content = request.getContent();
        Long categoryId = request.getCategoryId();

        ThrowUtils.throwIf(title == null || title.trim().isEmpty(), ErrorCode.PARAMS_ERROR, "标题不能为空");
        ThrowUtils.throwIf(title.length() > 512, ErrorCode.PARAMS_ERROR, "标题长度不能超过512个字符");
        ThrowUtils.throwIf(content == null || content.trim().isEmpty(), ErrorCode.PARAMS_ERROR, "内容不能为空");
        ThrowUtils.throwIf(categoryId == null, ErrorCode.PARAMS_ERROR, "分类不能为空");

        PostCategory category = postCategoryService.getById(categoryId);
        ThrowUtils.throwIf(category == null, ErrorCode.PARAMS_ERROR, "分类不存在");

        String firstImageUrl = extractFirstImageUrl(content);

        post.setTitle(title.trim());
        post.setContent(content);
        post.setCategoryId(categoryId);
        post.setFirstImageUrl(firstImageUrl);

        return this.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePost(Long postId, Long loginUserId) {
        ThrowUtils.throwIf(postId == null || postId <= 0, ErrorCode.PARAMS_ERROR, "帖子ID无效");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        Post post = this.getById(postId);
        ThrowUtils.throwIf(post == null || post.getIsDelete() == 1, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");

        User loginUser = userService.getById(loginUserId);
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isOwner = post.getUserId().equals(loginUserId);
        ThrowUtils.throwIf(!isAdmin && !isOwner, ErrorCode.NO_AUTH_ERROR, "无权删除该帖子");

        post.setIsDelete(1);
        boolean result = this.updateById(post);

        postLikeService.remove(QueryWrapper.create().eq("post_id", postId));
        commentService.remove(QueryWrapper.create().eq("post_id", postId));

        return result;
    }

    @Override
    public Page<PostVO> listPostByPage(PostQueryRequest request, Long loginUserId) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        User loginUser = userService.getById(loginUserId);
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        ThrowUtils.throwIf(!isAdmin, ErrorCode.NO_AUTH_ERROR, "无权访问，仅管理员可操作");

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("is_delete", 0);

        if (request.getId() != null) {
            queryWrapper.eq("id", request.getId());
        }
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            queryWrapper.like("title", request.getTitle().trim());
        }
        if (request.getUserId() != null) {
            queryWrapper.eq("user_id", request.getUserId());
        }
        if (request.getCategoryId() != null) {
            queryWrapper.eq("category_id", request.getCategoryId());
        }
        if (request.getIsTop() != null) {
            queryWrapper.eq("is_top", request.getIsTop());
        }

        queryWrapper.orderBy("create_time", false);

        Page<Post> postPage = this.page(new Page<>(request.getPageNum(), request.getPageSize()), queryWrapper);

        List<PostVO> postVOList = convertToPostVOList(postPage.getRecords(), loginUserId);

        Page<PostVO> resultPage = new Page<>();
        resultPage.setRecords(postVOList);
        resultPage.setPageNumber(postPage.getPageNumber());
        resultPage.setPageSize(postPage.getPageSize());
        resultPage.setTotalRow(postPage.getTotalRow());

        return resultPage;
    }

    @Override
    public boolean setPostTop(Long postId, Integer isTop, Long loginUserId) {
        ThrowUtils.throwIf(postId == null || postId <= 0, ErrorCode.PARAMS_ERROR, "帖子ID无效");
        ThrowUtils.throwIf(isTop == null || (isTop != 0 && isTop != 1), ErrorCode.PARAMS_ERROR, "置顶状态无效");
        ThrowUtils.throwIf(loginUserId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        User loginUser = userService.getById(loginUserId);
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        ThrowUtils.throwIf(!isAdmin, ErrorCode.NO_AUTH_ERROR, "无权操作，仅管理员可置顶帖子");

        Post post = this.getById(postId);
        ThrowUtils.throwIf(post == null || post.getIsDelete() == 1, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");

        post.setIsTop(isTop);
        return this.updateById(post);
    }
}
