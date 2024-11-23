package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.TeamData;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class SortingRankingTest {
    SortingRanking sortingRanking = new SortingRanking();

    @Test
    void noData() {
        HashMap<String, List<TeamData>> hashLeague = new HashMap<>();
        AssertionError error = assertThrows(AssertionError.class, () ->
                sortingRanking.rankingLeague(hashLeague));
        log.info("空 Map 獲得 error message: {}", error.getMessage());
        assertEquals("Need 6 teams for 本季季後賽，組別不夠打...", error.getMessage());
    }
}