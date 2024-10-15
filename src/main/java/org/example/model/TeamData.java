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
