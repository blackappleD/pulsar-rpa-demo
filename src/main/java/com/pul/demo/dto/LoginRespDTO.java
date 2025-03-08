package com.pul.demo.dto;

import lombok.Data;

@Data
public class LoginRespDTO {
    private String token;
    private String username;
}