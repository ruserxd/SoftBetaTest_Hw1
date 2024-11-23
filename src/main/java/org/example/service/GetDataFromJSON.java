package org.example.service;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.example.model.TeamData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// 負責將 Json 資料轉成 HashMap
@Getter
@Log4j
public class GetDataFromJSON {
    // final 意味著以下資料不能進行新增

    // 聯盟
    private static final String[] leagues = {"American League", "National League"};
    // 區域
    private static final String[] regions = {"West", "Central", "East"};

    // Team 內的資料
    private static final String[] teamOBJData = {"team", "wins", "losses"};

    // 獲取兩個聯盟的資料
    // 分兩個聯盟的 map ，因為兩者的關聯性對於此次作業並不大
    // Key: 西,中,東
    // Value: 各隊的 TeamData
    private final HashMap<String, List<TeamData>> hashAmericanLeague = initialMap();
    private final HashMap<String, List<TeamData>> hashNationalLeague = initialMap();

    void getDataFromJsonFile(String filePath) {
        try {
            Object ob = new JSONParser().parse(new FileReader(filePath));
            JSONObject js = (JSONObject) ob;

            for (String league : leagues) {

                // 這個聯盟的 Json 物件
                JSONObject leagueJson = (JSONObject) js.get(league);

                // 如果沒找到對應的 league
                if (leagueJson == null) {
                    throw new IllegalArgumentException("League not found in JSON: " + league);
                }

                for (String region : regions) {
                    if (league.equals(leagues[0]))
                        getRegionsData(leagueJson, region, hashAmericanLeague);
                    else if (league.equals(leagues[1]))
                        getRegionsData(leagueJson, region, hashNationalLeague);
                }
            }
        } catch (IOException | ParseException e) {
            log.error("getData 發生 : " + e);
        } finally {
            log.info("抓取 Json 結束");
        }
    }

    // 獲得該區域的資料
    private void getRegionsData(JSONObject jsonLeague, String region, HashMap<String, List<TeamData>> hashLeague) {
        // 這個聯盟的 Json 數組
        JSONArray teams = (JSONArray) jsonLeague.get(region);
        for (Object obj : teams) {
            //轉換 Object -> JSONObject
            JSONObject team = (JSONObject) obj;

            // builder 簡潔創建 teamData
            TeamData teamData = TeamData.builder()
                    .teamName(team.get(teamOBJData[0]).toString())
                    .win((Long) team.get(teamOBJData[1]))
                    .lose((Long) team.get(teamOBJData[2]))
                    .build();
            teamData.setWinRate((float) teamData.getWin() / ((float) teamData.getWin() + (float) teamData.getLose()));
            hashLeague.get(region).add(teamData);

            log.info("成功存入" + teamData);
        }
    }

    // 初始化 HashMap
    private static HashMap<String, List<TeamData>> initialMap() {
        HashMap<String, List<TeamData>> hashLeague = new HashMap<>();
        for (String region : regions)
            hashLeague.put(region, new ArrayList<>());

        return hashLeague;
    }
}
