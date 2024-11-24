package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GetDataFromJSONTest {
    private GetDataFromJSON getDataFromJSON;
    private static String FILE_PATH_ERRORJSON = "src/main/resources/ForTest/Error.json";
    @BeforeEach
    void setUp() {
        getDataFromJSON = new GetDataFromJSON();
    }

    @Test
    @DisplayName("測試JSON解析錯誤的ParseException")
    void testInvalidJsonFormat() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> getDataFromJSON.getDataFromJsonFile(FILE_PATH_ERRORJSON));

        assertTrue(exception.getMessage().contains("JSON解析錯誤"));
    }
}