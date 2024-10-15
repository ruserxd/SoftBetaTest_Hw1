package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.TeamData;

import java.util.*;

// 將資料 hashing 轉為該聯盟的排名 (每個區域的 No1) + 剩餘，以 ArrayList 作為排名順序
@Slf4j
public class SortingRanking {
    // 聯盟
    private static final String[] leagues = {"American League", "National League"};

    // 將從 Json 獲得的資料，做排序處理，並回傳 key : 聯盟, value : 排序好的隊伍，好以供應接下來的外卡、種子輸出
    public HashMap<String, ArrayList<TeamData>> processJson(String filePath) {
        // 先抓取資料
        GetDataFromJSON getDataFromJSON = new GetDataFromJSON();
        getDataFromJSON.getDataFromJsonFile(filePath);

        // 拿取 GetDataFromJson 獲取到的對應資料
        HashMap<String, List<TeamData>> hashAmericanLeague = getDataFromJSON.getHashAmericanLeague();
        HashMap<String, List<TeamData>> hashNationalLeague = getDataFromJSON.getHashNationalLeague();

        ArrayList<TeamData> americanLeague = rankingLeague(hashAmericanLeague);
        log.info("成功獲取排序好的資料" + " american\n" + americanLeague);

        ArrayList<TeamData> nationalLeague = rankingLeague(hashNationalLeague);
        log.info("成功獲取排序好的資料" + " national\n" + nationalLeague);

        HashMap<String, ArrayList<TeamData>> mlbLeagueRanking = new HashMap<>();
        mlbLeagueRanking.put(leagues[0], americanLeague);
        mlbLeagueRanking.put(leagues[1], nationalLeague);

        return mlbLeagueRanking;
    }

    // 會先找每個區域的第一名出來, 把它放到對應的 Winner, 其餘放到 loser, 接著在按規則做排序
    private ArrayList<TeamData> rankingLeague(HashMap<String, List<TeamData>> hashLeague) {
        // 第一名的隊伍
        ArrayList<TeamData> regionWinner = new ArrayList<>();

        // 除了第一名的隊伍
        ArrayList<TeamData> regionLoser = new ArrayList<>();

        // 將每個區域資料抓下，獲得第一名與其餘
        for (Map.Entry<String, List<TeamData>> entry : hashLeague.entrySet())
        {
            // 利用 winRate 進行排序
            entry.getValue().sort((a, b) -> Double.compare(b.winRate, a.winRate));
            List<TeamData> regionRank = entry.getValue();
            regionWinner.add(regionRank.get(0));

            for (int i = 1; i < regionRank.size(); i++)
                regionLoser.add(regionRank.get(i));
        }

        // 利用 winRate 進行排序
        regionWinner.sort((a, b) -> Double.compare(b.winRate, a.winRate));

        // 我們最後會得到的結果
        ArrayList<TeamData> sortingLeague = new ArrayList<>(regionWinner);

        // 利用 winRate 進行排序
        regionLoser.sort((a, b) -> Double.compare(b.winRate, a.winRate));
        for (int i = 0; i < 3 && i < regionLoser.size(); i++)
            sortingLeague.add(regionLoser.get(i));

        assert sortingLeague.size() == 6: "Need 6 teams for 本季季後賽，組別不夠打...";
        return sortingLeague;
    }
}
