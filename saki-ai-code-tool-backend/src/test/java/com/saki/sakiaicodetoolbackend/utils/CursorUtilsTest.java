package com.saki.sakiaicodetoolbackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@Slf4j
class CursorUtilsTest {

    @Test
    void testEncodeCursor() {
        LocalDateTime createTime = LocalDateTime.of(2026, 3, 4, 15, 30, 45);
        Long id = 100L;

        String cursor = CursorUtils.encodeCursor(createTime, id);

        Assertions.assertNotNull(cursor);
        Assertions.assertFalse(cursor.isEmpty());
        log.info("编码游标: createTime={}, id={}, cursor={}", createTime, id, cursor);
    }

    @Test
    void testDecodeCursor() {
        LocalDateTime createTime = LocalDateTime.of(2026, 3, 4, 15, 30, 45);
        Long id = 100L;

        String cursor = CursorUtils.encodeCursor(createTime, id);
        CursorUtils.CursorData cursorData = CursorUtils.decodeCursor(cursor);

        Assertions.assertNotNull(cursorData);
        Assertions.assertEquals(id, cursorData.getId());
        Assertions.assertEquals("2026-03-04 15:30:45", cursorData.getCreateTime());

        LocalDateTime parsedTime = cursorData.getParsedCreateTime();
        Assertions.assertNotNull(parsedTime);
        Assertions.assertEquals(2026, parsedTime.getYear());
        Assertions.assertEquals(3, parsedTime.getMonthValue());
        Assertions.assertEquals(4, parsedTime.getDayOfMonth());
        Assertions.assertEquals(15, parsedTime.getHour());
        Assertions.assertEquals(30, parsedTime.getMinute());
        Assertions.assertEquals(45, parsedTime.getSecond());

        log.info("解码游标: cursor={}, createTime={}, id={}", cursor, cursorData.getCreateTime(), cursorData.getId());
    }

    @Test
    void testEncodeCursor_NullValues() {
        String cursor1 = CursorUtils.encodeCursor(null, 100L);
        Assertions.assertNull(cursor1);

        String cursor2 = CursorUtils.encodeCursor(LocalDateTime.now(), null);
        Assertions.assertNull(cursor2);

        String cursor3 = CursorUtils.encodeCursor(null, null);
        Assertions.assertNull(cursor3);

        log.info("空值编码测试通过");
    }

    @Test
    void testDecodeCursor_NullOrEmpty() {
        CursorUtils.CursorData result1 = CursorUtils.decodeCursor(null);
        Assertions.assertNull(result1);

        CursorUtils.CursorData result2 = CursorUtils.decodeCursor("");
        Assertions.assertNull(result2);

        CursorUtils.CursorData result3 = CursorUtils.decodeCursor("   ");
        Assertions.assertNull(result3);

        log.info("空值解码测试通过");
    }

    @Test
    void testDecodeCursor_InvalidBase64() {
        CursorUtils.CursorData result = CursorUtils.decodeCursor("这不是有效的Base64字符串!!!");
        Assertions.assertNull(result);

        log.info("无效Base64解码测试通过");
    }

    @Test
    void testDecodeCursor_InvalidJson() {
        String invalidJson = "eyJ0ZXN0IjogIm5vdCBhIHZhbGlkIGN1cnNvciJ9";
        CursorUtils.CursorData result = CursorUtils.decodeCursor(invalidJson);
        Assertions.assertNull(result);

        log.info("无效JSON解码测试通过");
    }

    @Test
    void testEncodeDecodeConsistency() {
        for (int i = 0; i < 10; i++) {
            LocalDateTime createTime = LocalDateTime.now().minusDays(i);
            Long id = (long) (1000 + i);

            String cursor = CursorUtils.encodeCursor(createTime, id);
            CursorUtils.CursorData decoded = CursorUtils.decodeCursor(cursor);

            Assertions.assertNotNull(decoded);
            Assertions.assertEquals(id, decoded.getId());

            LocalDateTime parsedTime = decoded.getParsedCreateTime();
            Assertions.assertNotNull(parsedTime);
            Assertions.assertEquals(createTime.getYear(), parsedTime.getYear());
            Assertions.assertEquals(createTime.getMonthValue(), parsedTime.getMonthValue());
            Assertions.assertEquals(createTime.getDayOfMonth(), parsedTime.getDayOfMonth());
            Assertions.assertEquals(createTime.getHour(), parsedTime.getHour());
            Assertions.assertEquals(createTime.getMinute(), parsedTime.getMinute());
            Assertions.assertEquals(createTime.getSecond(), parsedTime.getSecond());
        }

        log.info("编解码一致性测试通过");
    }

    @Test
    void testCursorFormat() {
        LocalDateTime createTime = LocalDateTime.of(2026, 12, 31, 23, 59, 59);
        Long id = 999999L;

        String cursor = CursorUtils.encodeCursor(createTime, id);

        Assertions.assertTrue(cursor.length() > 0);
        Assertions.assertFalse(cursor.contains(" "), "游标不应包含空格");
        Assertions.assertFalse(cursor.contains("\n"), "游标不应包含换行符");

        log.info("游标格式测试通过: cursor={}", cursor);
    }
}
