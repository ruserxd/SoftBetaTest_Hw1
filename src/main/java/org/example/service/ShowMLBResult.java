package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.TeamData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ShowMLBResult {
    // 聯盟
    private static final String[] leagues = {"American League", "National League"};

    public void ShowMLBResult(String filePath) {
        try {
            SortingRanking sortingRanking = new SortingRanking();

            // 使用傳入的 filePath 參數
            HashMap<String, List<TeamData>> wantData = sortingRanking.processJson(filePath);
            for (Map.Entry<String, List<TeamData>> entry : wantData.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }

            showAmericanLeague(wantData.get(leagues[0]));
            showNationalLeague(wantData.get(leagues[1]));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            log.info("Ending");
        }
    }

    public static void showAmericanLeague(List<TeamData> americanLeagueRanking) {
        System.out.println("(AMERICAN LEAGUE)\n");

        // 定義輸出格式
        String format = "%25s %-2d %-6s%-17s\n";
        String continueFormat = "%33s %-2d %-6s%-17s\n";

        System.out.printf(format, americanLeagueRanking.get(5).teamName, 6, "-----", "");
        System.out.printf(format, americanLeagueRanking.get(2).teamName, 3, "-----", "? -----");
        System.out.printf(continueFormat, americanLeagueRanking.get(1).teamName, 2, "-----", "?");
        System.out.printf(format, americanLeagueRanking.get(4).teamName, 5, "-----", "");
        System.out.printf(format, americanLeagueRanking.get(3).teamName, 4, "-----", "? -----");
        System.out.printf(continueFormat, americanLeagueRanking.get(0).teamName, 1, "-----", "? ----- ?");

        // 總冠軍
        System.out.printf("%55s\n", "---- ?");
    }

    public static void showNationalLeague(List<TeamData> nationalLeagueRanking) {
        // 定義輸出格式
        String format = "%25s %-2d %-6s%-17s\n";
        String continueFormat = "%33s %-2d %-6s%-17s\n";

        System.out.printf(format, nationalLeagueRanking.get(5).teamName, 6, "-----", "? ----- ? ----- ?");
        System.out.printf(format, nationalLeagueRanking.get(2).teamName, 3, "-----", "");
        System.out.printf(continueFormat, nationalLeagueRanking.get(1).teamName, 2, "-----", "");
        System.out.printf(format, nationalLeagueRanking.get(4).teamName, 5, "----- ?", " ----- ?");
        System.out.printf(format, nationalLeagueRanking.get(3).teamName, 4, "-----", "");
        System.out.printf(continueFormat, nationalLeagueRanking.get(0).teamName, 1, "-----", "");

        System.out.println("(NATIONAL LEAGUE)\n");
    }

}
