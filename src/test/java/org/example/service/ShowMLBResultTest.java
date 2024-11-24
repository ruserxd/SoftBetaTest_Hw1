package org.example.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShowMLBResultTest {
    private final ShowMLBResult showMLBResult = new ShowMLBResult();
    private static final String FILE_PATH_BASIC = "src/main/resources/mlbTeamsJsonRecord/record_2023.json";

    @Test
    @DisplayName("測試正常輸入是否會產生問題")
    void testBasicOut() {
        showMLBResult.ShowMLBResult(FILE_PATH_BASIC);
    }

    @Test
    @DisplayName("測試錯誤的檔案路徑")
    void testErrorOut() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> showMLBResult.ShowMLBResult("/error"));

        // 驗證錯誤訊息
        String expectedMessage = "無法讀取檔案";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}