package com.hfh.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token响应")
public class TokenVO {

    @Schema(description = "访问令牌（Access Token，短期有效）", example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
    private String token;

    @Schema(description = "Access Token有效期（秒）", example = "7200")
    private Long expiresIn;

    @Schema(description = "刷新令牌（Refresh Token，长期有效）", example = "yyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy")
    private String refreshToken;

    @Schema(description = "Refresh Token有效期（秒）", example = "2592000")
    private Long refreshExpiresIn;

    public TokenVO(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
