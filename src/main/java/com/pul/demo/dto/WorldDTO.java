package com.pul.demo.dto;

import com.pul.demo.enums.WorldLevel;
import lombok.Data;

@Data
public class WorldDTO {
    private Long id;
    private Long worldId;
    private WorldLevel level;
    private String name;
}