package com.hfh.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
@Schema(description = "登录请求")
public class LoginDTO {

    @NotBlank(message = "账号不能为空")
    @Schema(description = "用户名或邮箱", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
