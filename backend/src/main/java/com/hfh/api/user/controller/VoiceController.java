package com.hfh.api.user.controller;

import com.hfh.api.common.BaseController;
import com.hfh.api.common.Result;
import com.hfh.api.service.AiBookkeepingService;
import com.hfh.api.service.impl.VoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户端-语音识别控制器
 */
@Slf4j
@Tag(name = "用户端-语音识别")
@RestController
@RequestMapping("/api/voice")
public class VoiceController extends BaseController {

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private AiBookkeepingService aiBookkeepingService;

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

    @Operation(summary = "语音智能记账", description = "上传音频文件，自动识别语音内容并分析记账需求，自动完成记账")
    @PostMapping("/bookkeep")
    public Result<Map<String, String>> voiceBookkeep(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.fail("音频文件不能为空");
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.fail("音频文件不能超过10MB");
            }

            // 1. 语音转文字
            byte[] audioData = file.getBytes();
            String text = voiceService.recognize(audioData);
            log.info("语音识别结果: {}", text);

            if (text == null || text.isBlank()) {
                return Result.fail("语音识别结果为空，请重新录制");
            }

            // 2. 将识别文字传入AI记账分析，自动调用记账接口
            Long userId = getCurrentUserId();
            String aiResult = aiBookkeepingService.analyzeAndRecord(text, userId);

            return Result.ok(Map.of(
                    "text", text,
                    "result", aiResult
            ));
        } catch (Exception e) {
            return Result.fail("语音记账失败: " + e.getMessage());
        }
    }

    @Operation(summary = "文字智能记账", description = "传入记账需求文字，AI自动分析并完成记账")
    @PostMapping("/bookkeep/text")
    public Result<Map<String, String>> textBookkeep(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            if (text == null || text.isBlank()) {
                return Result.fail("记账需求不能为空");
            }

            Long userId = getCurrentUserId();
            String aiResult = aiBookkeepingService.analyzeAndRecord(text, userId);

            return Result.ok(Map.of(
                    "text", text,
                    "result", aiResult
            ));
        } catch (Exception e) {
            return Result.fail("文字记账失败: " + e.getMessage());
        }
    }
}
