package org.example;

import org.example.service.ShowMLBResult;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 主要負責呼叫寫好的 Class
 */
public class Main {

    public static ArrayList<String> paths = new ArrayList<>();

    public static void initial() {
        paths.add("src/main/resources/mlbTeamsJsonRecord/record_2023_forAssert.json");
        paths.add("src/main/resources/mlbTeamsJsonRecord/record_2023_forTryCatchFinally.json");
        paths.add("src/main/resources/mlbTeamsJsonRecord/record_2023.json");
    }

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

        int input = scanner.nextInt();

        while (1 > input || 3 < input) {
            System.out.println("請輸入在範圍內 (1-3):");
            input = scanner.nextInt();
        }

        switch (input) {
            case 1, 2, 3 -> {
                int index = input - 1;
                System.out.println("\n執行: " + paths.get(index));
                showMLBResult.ShowMLBResult(paths.get(index));
            }
            default -> System.out.println("無效的輸入");
        }
    }
}