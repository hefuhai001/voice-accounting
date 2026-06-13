package com.hfh.api.user.controller;

import com.hfh.api.common.Result;
import com.hfh.api.service.impl.VoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户端-语音识别控制器
 */
@Tag(name = "用户端-语音识别")
@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @Autowired
    private VoiceService voiceService;

    @Operation(summary = "语音转文字", description = "上传WAV音频文件（16bit单声道16kHz），返回识别文字")
    @PostMapping("/recognize")
    public Result<Map<String, String>> recognize(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.fail("音频文件不能为空");
            }

            // 限制文件大小：10MB
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.fail("音频文件不能超过10MB");
            }

            byte[] audioData = file.getBytes();
            String text = voiceService.recognize(audioData);

            return Result.ok(Map.of("text", text));
        } catch (Exception e) {
            return Result.fail("语音识别失败: " + e.getMessage());
        }
    }
}
