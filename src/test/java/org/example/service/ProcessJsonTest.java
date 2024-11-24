package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.TeamData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ProcessJsonTest {
    private SortingRanking sortingRanking;

    // 預計要測試的資料路徑
    private static final String FILE_PATH_BASIC = "src/main/resources/mlbTeamsJsonRecord/record_2023.json";
    private static final String FILE_PATH_ASSERT = "src/main/resources/mlbTeamsJsonRecord/record_2023_forTryCatchFinally.json";
    private static final String FILE_PATH_ONLYNATIONAL = "src/main/resources/ForTest/record_2023_OnlyNational.json";

    @BeforeEach
    void setUp() {
        sortingRanking = new SortingRanking();
        System.out.println("-------------------------");
    }

    @Test
    @DisplayName("測試 Only National")
    void testOnlyNational() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> sortingRanking.processJson(FILE_PATH_ONLYNATIONAL));
        assertEquals("League not found in JSON: American League", error.getMessage());
    }


    @Test
    @DisplayName("測試是否拋出沒有對應聯盟的例外")
    void testNoExpectLeague() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> sortingRanking.processJson(FILE_PATH_ASSERT));
        assertEquals("League not found in JSON: National League", error.getMessage());
    }

    @Test
    @DisplayName("測試檔案不存在的情況")
    void testFileNotFound() {
        String nonExistentPath = "non_existent_file.json";

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                sortingRanking.processJson(nonExistentPath)
        );

        assertTrue(exception.getMessage().contains("無法讀取檔案"));
    }

    @Test
    @DisplayName("測試 2023 數據並驗證排名結果")
    void testProcessJsonWithReal2023Data() {
        HashMap<String, List<TeamData>> result = sortingRanking.processJson(FILE_PATH_BASIC);

        // 測試資料內容
        assertEquals(2, result.size());
        assertTrue(result.containsKey("American League"));
        assertTrue(result.containsKey("National League"));

        // 取得美國聯盟排名
        List<TeamData> americanLeague = result.get("American League");
        assertEquals(6, americanLeague.size());

        // 驗證美國聯盟前三名應該是各區第一名
        validateDivisionWinners(americanLeague,
                "Baltimore Orioles",  // East
                "Minnesota Twins",    // Central
                "Houston Astros"      // West
        );

        // 驗證美國聯盟的外卡隊伍 (第4-6名)
        validateWildCardTeams(americanLeague,
                "Tampa Bay Rays",
                "Texas Rangers",
                "Toronto Blue Jays"
        );

        // 取得國家聯盟排名
        List<TeamData> nationalLeague = result.get("National League");
        assertEquals(6, nationalLeague.size());

        // 驗證國家聯盟前三名應該是各區第一名
        validateDivisionWinners(nationalLeague,
                "Atlanta Braves",     // East
                "Milwaukee Brewers",  // Central
                "Los Angeles Dodgers" // West
        );

        // 驗證國家聯盟的外卡隊伍 (第4-6名)
        validateWildCardTeams(nationalLeague,
                "Philadelphia Phillies",
                "Arizona Diamondbacks",
                "Miami Marlins"
        );

        // 輸出結果以供檢查
        log.info("American League 排名:");
        americanLeague.forEach(team ->
                log.info("{}: {}", team.getTeamName(), team.getWinRate()));

        log.info("\nNational League 排名:");
        nationalLeague.forEach(team ->
                log.info("{}: {}", team.getTeamName(), team.getWinRate()));
    }

    private void validateDivisionWinners(List<TeamData> teams, String... expectedWinners) {
        // 檢查前三名是否包含所有分區冠軍
        List<String> firstThreeTeams = teams.subList(0, 3)
                .stream()
                .map(TeamData::getTeamName)
                .toList();

        for (String winner : expectedWinners) {
            assertTrue(firstThreeTeams.contains(winner),
                    "Division winner " + winner + " should be in top 3");
        }
    }

    private void validateWildCardTeams(List<TeamData> teams, String... expectedWildCards) {
        // 檢查4-6名是否為預期的外卡隊伍
        List<String> wildCardTeams = teams.subList(3, 6)
                .stream()
                .map(TeamData::getTeamName)
                .toList();

        for (String wildCard : expectedWildCards) {
            assertTrue(wildCardTeams.contains(wildCard),
                    "Wild card team " + wildCard + " should be in positions 4-6");
        }
    }
}