package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.TeamData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class SortingRankingTest {
    private SortingRanking sortingRanking;
    private HashMap<String, List<TeamData>> testData;

    @Test
    @DisplayName("第一筆資料測試是否有符合規範(由大到小)")
    void testRankingLeague() {
        sortingRanking = new SortingRanking();
        testData = new HashMap<>();

        // West區域
        List<TeamData> westTeams = new ArrayList<>();
        westTeams.add(TeamData.builder()
                .teamName("West1")
                .win(95L)
                .lose(67L)
                .winRate(0.586f)
                .build());
        westTeams.add(TeamData.builder()
                .teamName("West2")
                .win(90L)
                .lose(72L)
                .winRate(0.556f)
                .build());

        // Central區域
        List<TeamData> centralTeams = new ArrayList<>();
        centralTeams.add(TeamData.builder()
                .teamName("Central1")
                .win(100L)
                .lose(62L)
                .winRate(0.617f)
                .build());
        centralTeams.add(TeamData.builder()
                .teamName("Central2")
                .win(88L)
                .lose(74L)
                .winRate(0.543f)
                .build());

        // East區域
        List<TeamData> eastTeams = new ArrayList<>();
        eastTeams.add(TeamData.builder()
                .teamName("East1")
                .win(92L)
                .lose(70L)
                .winRate(0.568f)
                .build());
        eastTeams.add(TeamData.builder()
                .teamName("East2")
                .win(89L)
                .lose(73L)
                .winRate(0.549f)
                .build());

        testData.put("West", westTeams);
        testData.put("Central", centralTeams);
        testData.put("East", eastTeams);
        log.info("初始化資料 \n{}", testData);

        List<TeamData> result = sortingRanking.rankingLeague(testData);
        log.info("結果 {}", result);

        // List 的結果必須為從大到小
        for (int i = 0; i < result.size()-1; i++) {
            assertTrue(result.get(i).winRate > result.get(i+1).winRate);
        }
    }

    @Test
    @DisplayName("沒有資料是否會拋出例外")
    void noData() {
        HashMap<String, List<TeamData>> hashLeague = new HashMap<>();
        AssertionError error = assertThrows(AssertionError.class, () ->
                sortingRanking.rankingLeague(hashLeague));
        log.info("空 Map 獲得 error message: {}", error.getMessage());
        assertEquals("Need 6 teams for 本季季後賽，組別不夠打...", error.getMessage());
    }
}