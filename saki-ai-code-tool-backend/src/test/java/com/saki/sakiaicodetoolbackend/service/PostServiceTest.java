package com.saki.sakiaicodetoolbackend.service;

import com.saki.sakiaicodetoolbackend.common.CursorPage;
import com.saki.sakiaicodetoolbackend.model.dto.post.PostCursorQueryRequest;
import com.saki.sakiaicodetoolbackend.model.entity.Post;
import com.saki.sakiaicodetoolbackend.model.entity.PostCategory;
import com.saki.sakiaicodetoolbackend.model.entity.User;
import com.saki.sakiaicodetoolbackend.model.vo.PostVO;
import com.saki.sakiaicodetoolbackend.utils.CursorUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootTest
class PostServiceTest {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @Resource
    private PostCategoryService postCategoryService;

    private static final int TEST_PAGE_SIZE = 5;

    @Test
    void testGetPostCursorPage_FirstPage() {
        PostCursorQueryRequest request = new PostCursorQueryRequest();
        request.setPageSize(TEST_PAGE_SIZE);

        CursorPage<PostVO> result = postService.getPostCursorPage(request, null);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
        Assertions.assertNotNull(result.getHasMore());

        log.info("第一页查询结果: records.size={}, hasMore={}, nextCursor={}",
                result.getRecords().size(), result.getHasMore(), result.getNextCursor());

        if (!result.getRecords().isEmpty()) {
            PostVO firstPost = result.getRecords().get(0);
            Assertions.assertNotNull(firstPost.getId());
            Assertions.assertNotNull(firstPost.getTitle());
            Assertions.assertNotNull(firstPost.getUser());
            Assertions.assertNotNull(firstPost.getCategoryName());
            log.info("第一条帖子: id={}, title={}, categoryName={}, userName={}",
                    firstPost.getId(), firstPost.getTitle(), firstPost.getCategoryName(), firstPost.getUser().getUserName());
        }
    }

    @Test
    void testGetPostCursorPage_WithCursor() {
        PostCursorQueryRequest firstRequest = new PostCursorQueryRequest();
        firstRequest.setPageSize(TEST_PAGE_SIZE);

        CursorPage<PostVO> firstResult = postService.getPostCursorPage(firstRequest, null);

        if (firstResult.getHasMore() && firstResult.getNextCursor() != null) {
            PostCursorQueryRequest secondRequest = new PostCursorQueryRequest();
            secondRequest.setPageSize(TEST_PAGE_SIZE);
            secondRequest.setCursor(firstResult.getNextCursor());

            CursorPage<PostVO> secondResult = postService.getPostCursorPage(secondRequest, null);

            Assertions.assertNotNull(secondResult);
            Assertions.assertNotNull(secondResult.getRecords());

            log.info("第二页查询结果: records.size={}, hasMore={}",
                    secondResult.getRecords().size(), secondResult.getHasMore());

            if (!firstResult.getRecords().isEmpty() && !secondResult.getRecords().isEmpty()) {
                PostVO lastOfFirst = firstResult.getRecords().get(firstResult.getRecords().size() - 1);
                PostVO firstOfSecond = secondResult.getRecords().get(0);
                Assertions.assertNotEquals(lastOfFirst.getId(), firstOfSecond.getId(),
                        "第一页最后一条和第二页第一条不应该相同");
                log.info("验证分页正确: 第一页最后id={}, 第二页第一条id={}", lastOfFirst.getId(), firstOfSecond.getId());
            }
        } else {
            log.info("数据不足，无法测试第二页");
        }
    }

    @Test
    void testGetPostCursorPage_WithCategoryId() {
        List<PostCategory> categories = postCategoryService.list();
        if (categories.isEmpty()) {
            log.info("没有分类数据，跳过分类筛选测试");
            return;
        }

        Long categoryId = categories.get(0).getId();
        PostCursorQueryRequest request = new PostCursorQueryRequest();
        request.setPageSize(TEST_PAGE_SIZE);
        request.setCategoryId(categoryId);

        CursorPage<PostVO> result = postService.getPostCursorPage(request, null);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());

        for (PostVO post : result.getRecords()) {
            Assertions.assertEquals(categoryId, post.getCategoryId(),
                    "所有帖子应该属于指定分类");
        }

        log.info("按分类筛选结果: categoryId={}, records.size={}", categoryId, result.getRecords().size());
    }

    @Test
    void testGetPostCursorPage_WithKeyword() {
        PostCursorQueryRequest request = new PostCursorQueryRequest();
        request.setPageSize(TEST_PAGE_SIZE);
        request.setKeyword("测试");

        CursorPage<PostVO> result = postService.getPostCursorPage(request, null);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());

        log.info("按关键词搜索结果: keyword='测试', records.size={}", result.getRecords().size());
    }

    @Test
    void testGetPostCursorPage_PageSizeLimit() {
        PostCursorQueryRequest request = new PostCursorQueryRequest();
        request.setPageSize(100);

        Assertions.assertEquals(50, request.getEffectivePageSize(), "pageSize应该被限制为50");

        request.setPageSize(0);
        Assertions.assertEquals(1, request.getEffectivePageSize(), "pageSize最小应该为1");

        request.setPageSize(-1);
        Assertions.assertEquals(1, request.getEffectivePageSize(), "负数pageSize应该被限制为1");
    }

    @Test
    void testCursorUtils_EncodeDecode() {
        LocalDateTime createTime = LocalDateTime.of(2026, 3, 4, 12, 0, 0);
        Long id = 12345L;

        String encodedCursor = CursorUtils.encodeCursor(createTime, id);
        Assertions.assertNotNull(encodedCursor);
        log.info("编码后的游标: {}", encodedCursor);

        CursorUtils.CursorData decodedData = CursorUtils.decodeCursor(encodedCursor);
        Assertions.assertNotNull(decodedData);
        Assertions.assertEquals(id, decodedData.getId());
        Assertions.assertNotNull(decodedData.getParsedCreateTime());

        log.info("解码后的游标: createTime={}, id={}", decodedData.getParsedCreateTime(), decodedData.getId());
    }

    @Test
    void testCursorUtils_DecodeInvalidCursor() {
        CursorUtils.CursorData result = CursorUtils.decodeCursor("invalid_cursor");
        Assertions.assertNull(result, "无效游标应该返回null");
        log.info("无效游标解码结果: null (符合预期)");
    }

    @Test
    void testGetPostCursorPage_WithInvalidCursor() {
        PostCursorQueryRequest request = new PostCursorQueryRequest();
        request.setPageSize(TEST_PAGE_SIZE);
        request.setCursor("invalid_base64_cursor");

        CursorPage<PostVO> result = postService.getPostCursorPage(request, null);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());

        log.info("无效游标时返回第一页数据: records.size={}", result.getRecords().size());
    }

    @Test
    void testGetPostCursorPage_WithLoginUser() {
        List<User> users = userService.list();
        if (users.isEmpty()) {
            log.info("没有用户数据，跳过登录用户测试");
            return;
        }

        Long loginUserId = users.get(0).getId();
        PostCursorQueryRequest request = new PostCursorQueryRequest();
        request.setPageSize(TEST_PAGE_SIZE);

        CursorPage<PostVO> result = postService.getPostCursorPage(request, loginUserId);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());

        for (PostVO post : result.getRecords()) {
            Assertions.assertNotNull(post.getIsLiked(), "isLiked字段不应为null");
        }

        log.info("登录用户查询结果: loginUserId={}, records.size={}", loginUserId, result.getRecords().size());
    }

    @Test
    void testGetPostCursorPage_CheckPostVOFields() {
        PostCursorQueryRequest request = new PostCursorQueryRequest();
        request.setPageSize(1);

        CursorPage<PostVO> result = postService.getPostCursorPage(request, null);

        if (!result.getRecords().isEmpty()) {
            PostVO post = result.getRecords().get(0);

            Assertions.assertNotNull(post.getId(), "id不应为null");
            Assertions.assertNotNull(post.getUserId(), "userId不应为null");
            Assertions.assertNotNull(post.getCategoryId(), "categoryId不应为null");
            Assertions.assertNotNull(post.getTitle(), "title不应为null");
            Assertions.assertNotNull(post.getContent(), "content不应为null");
            Assertions.assertNotNull(post.getViewCount(), "viewCount不应为null");
            Assertions.assertNotNull(post.getLikeCount(), "likeCount不应为null");
            Assertions.assertNotNull(post.getCommentCount(), "commentCount不应为null");
            Assertions.assertNotNull(post.getIsTop(), "isTop不应为null");
            Assertions.assertNotNull(post.getCreateTime(), "createTime不应为null");
            Assertions.assertNotNull(post.getUser(), "user不应为null");
            Assertions.assertNotNull(post.getCategoryName(), "categoryName不应为null");
            Assertions.assertNotNull(post.getIsLiked(), "isLiked不应为null");

            log.info("PostVO字段验证通过: id={}, title={}, categoryName={}, userName={}",
                    post.getId(), post.getTitle(), post.getCategoryName(), post.getUser().getUserName());
        }
    }
}
