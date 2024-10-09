---
title: H01 季後賽

---

# H01 季後賽

## 本筆記網站
怕格式跑掉
https://hackmd.io/@ruserxd/BkLL8QB0R

## 本次目標
自行製作一個 mlb 季後賽表
首先，透過 json 檔案讀取例行賽的對戰紀錄表 -> 資料
接著，對資料進行處理，
有兩個聯盟 (American, National)，每個聯盟有三個區域 (West, Central, East)
需要從每個聯盟挑出 2 個種子, 4 個外卡
每個區域 (Region) 都會有一個分區冠軍，從三個裡面選出兩個當作種子，剩下的一個為外卡
接著再從 3 個區域不是冠軍依照勝率 (高) 取出 3 對當做外卡

同分修改:
此次針對的數據為 2023 mlb 例行賽
因為有同勝場敗場的隊伍
我選擇修改數據 sry
原始兩隊數據為 wins:90, losses:72
```json
{
    "team": "Houston Astros",
    "wins": 91,
    "losses": 71
},
{
    "team": "Texas Rangers",
    "wins": 89,
    "losses": 73
}
```
## Preventive Programming

### 1. SLF4J-Log4j
這部分算是個人習慣，平常使用 Spring Boot 框架，都會有 SLF4J 在裡頭
結果自己要導入的時候好麻煩，有些版本 1.多得，看文章說有出現安全性漏洞
不建議用，以下是後來找到的版本
```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>2.0.16</version>
</dependency>
```
還要設置對應的 log4j.properties 才能有希望呈現的時間顏色...(預設紅色)
這時才理解到 Spring Boot 的好 (當然還有其他的好)

```java
// 獲得該區域的資料
private void getRegionsData(JSONObject jsonLeague, String region, HashMap<String, List<TeamData>> hashLeague) {
    ..省略
    for (Object obj : teams) {
        ..大量程式碼省略
        hashLeague.get(region).add(teamData);

        log.info("成功存入" + teamData);
    }        
}
```

Example:
![截圖 2024-09-29 晚上7.36.50](https://hackmd.io/_uploads/SyGl3hI00.png)

### 2. assert
為了避免出現資料的隊伍不足，可能今年一堆隊伍退出聯盟，因為內部沒錢
出現不符合一個聯盟 6 隊打季後賽的情況
設計了一段 assert 在 SortingRanking.java 上
`assert sortingLeague.size() == 6: "Need 6 teams for 本季季後賽，組別不夠打...";`

`for (int i = 0; i < 3 && i < regionLoser.size(); i++)`

**(此測試請使用src/main/resources/mlbTeamsJsonRecord/record_2023_forAssert.json)**

```java
// 會先找每個區域的第一名出來, 把它放到對應的 Winner, 其餘放到 loser, 接著在按規則做排序
private ArrayList<TeamData> rankingLeague(HashMap<String, List<TeamData>> hashLeague) {
    ..大量程式碼省略
    // 利用 winRate 進行排序
    regionLoser.sort((a, b) -> Double.compare(b.winRate, a.winRate));
    for (int i = 0; i < 3 && i < regionLoser.size(); i++)
        sortingLeague.add(regionLoser.get(i));

    assert sortingLeague.size() == 6: "Need 6 teams for 本季季後賽，組別不夠打...";
    return sortingLeague;
}
```

Example:
![截圖 2024-09-29 晚上7.38.34](https://hackmd.io/_uploads/r1JUnnIRC.png)

### 3. try-cathch-finally
當我們在讀取 Json 檔案時，會希望 Json 是一個固定格式的檔案
當沒讀取到希望的聯盟名稱時會 throw 一段 error message
並結束時獲得一個訊息

**(此測試請使用src/main/resources/mlbTeamsJsonRecord/record_2023_forTryCatchFinally.json)**
```java
// 聯盟
private static final String[] leagues = {"American League", "National League"};

// 獲取 Json 的資料
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
            ... 程式碼省略
        }
    } catch (IOException | ParseException e) {
        log.error(e);
    } finally {
        log.info("抓取 Json 結束");
    }
}
```

Example:
![截圖 2024-09-29 晚上7.39.52](https://hackmd.io/_uploads/rkzs22IAA.png)


### 4. 三層式架構
我將整個架構分為三個步驟 (物件) 操作
- 步驟:
    讀取 Json -> 對資料處理 -> 依照資料輸出結果
- 對應類別 Class:
    GetDataFromJson -> ShowMLBResult -> SortingRanking
    
這樣當哪個步驟出錯時，可以比較有效率的找出是哪裡的問題並修正，同時也較好閱讀程式碼

## 其餘部分

### model
對於 mlb 數據的設計
會有對應的隊伍名稱、勝場、敗場、勝率
運用一些 lombok 簡化我們需要寫的程式碼

```java
package org.example.model;

import lombok.Builder;
import lombok.Data;

// lombok 簡化程式碼
@Builder
@Data
public class TeamData {
    public String teamName;
    public Long win;
    public Long lose;
    public Float winRate;
}
```

### 使用此專案
打開 Main 類別 -> run code
若想測試 Assert 請記得打上 -ea
路徑可根據想測試的項目做使用
- 1 Assert 
  (src/main/resources/mlbTeamsJsonRecord/record_2023_forAssert.json)
- 2 try-catch-finally 
  (src/main/resources/mlbTeamsJsonRecord/record_2023_forTryCatchFinally.json)
- 3 Orignal version 
  (src/main/resources/mlbTeamsJsonRecord/record_2023.json)
  
人性化設置 -> 透過輸入選擇即可
```java
public static void main(String[] args) {
    // 初始化可使用路徑
    initial();
    Scanner scanner = new Scanner(System.in);
    ShowMLBResult showMLBResult = new ShowMLBResult();

    System.out.println("\n請問要執行哪個測試檔案");
    System.out.println("1. Assert");
    System.out.println("2. Try-Catch");
    System.out.println("3. Original Version");
    System.out.print("請輸入選項 (1-3): ");

    String input = scanner.nextLine();

    switch (input) {
        case "1", "2", "3" -> {
            int index = Integer.parseInt(input) - 1;
            System.out.println("\n執行: " + paths.get(index));
            showMLBResult.ShowMLBResult(paths.get(index));
        }
        default -> System.out.println("無效的輸入");
    }
}
```

