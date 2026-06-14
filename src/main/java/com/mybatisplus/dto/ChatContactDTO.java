package com.mybatisplus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatContactDTO {
    private String userType;
    private Integer userId;
    private String displayName;
    private String extra;
}
