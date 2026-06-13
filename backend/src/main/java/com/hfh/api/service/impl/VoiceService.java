package com.hfh.api.service.impl;

import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechRecognizer;
import com.alibaba.nls.client.protocol.asr.SpeechRecognizerListener;
import com.alibaba.nls.client.protocol.asr.SpeechRecognizerResponse;
import com.alibaba.nls.client.AccessToken;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云智能语音交互 - 一句话识别服务
 * 基于 nls-sdk-recognizer
 */
@Slf4j
@Service
public class VoiceService {

    @Value("${aliyun.nls.app-key}")
    private String appKey;

    @Value("${aliyun.nls.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.nls.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.nls.gateway-url:wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1}")
    private String gatewayUrl;

    private NlsClient nlsClient;
    private volatile String cachedToken;
    private volatile long tokenExpireTime;

    /**
     * 获取NLS访问Token（带缓存，提前5分钟刷新）
     * 使用SDK自带的AccessToken类
     */
    private synchronized String getToken() throws Exception {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return cachedToken;
        }

        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        accessToken.apply();
        cachedToken = accessToken.getToken();
        tokenExpireTime = accessToken.getExpireTime() - 300 * 1000L;
        log.info("NLS Token刷新成功，有效期至: {}", tokenExpireTime);
        return cachedToken;
    }

    /**
     * 获取NlsClient实例（全局单例）
     */
    private synchronized NlsClient getNlsClient() throws Exception {
        if (nlsClient == null) {
            String token = getToken();
            nlsClient = new NlsClient(gatewayUrl, token);
        }
        return nlsClient;
    }

    @PreDestroy
    public void destroy() {
        if (nlsClient != null) {
            nlsClient.shutdown();
        }
    }

    /**
     * 一句话语音识别（适用于60秒以内的短音频）
     *
     * @param audioData 音频二进制数据（PCM格式，16bit单声道16kHz）
     * @return 识别出的文字
     */
    public String recognize(byte[] audioData) throws Exception {
        String token = getToken();
        NlsClient client = getNlsClient();

        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder resultBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();

        SpeechRecognizerListener listener = new SpeechRecognizerListener() {
            @Override
            public void onRecognitionResultChanged(SpeechRecognizerResponse response) {
                log.debug("识别中间结果: {}", response.getRecognizedText());
            }

            @Override
            public void onRecognitionCompleted(SpeechRecognizerResponse response) {
                String text = response.getRecognizedText();
                log.info("识别最终结果: {}", text);
                resultBuilder.append(text);
                latch.countDown();
            }

            @Override
            public void onStarted(SpeechRecognizerResponse response) {
                log.debug("识别开始, task_id: {}", response.getTaskId());
            }

            @Override
            public void onFail(SpeechRecognizerResponse response) {
                log.error("识别失败, task_id: {}, status: {}, status_text: {}",
                        response.getTaskId(), response.getStatus(), response.getStatusText());
                errorBuilder.append(response.getStatusText());
                latch.countDown();
            }
        };

        SpeechRecognizer recognizer = null;
        try {
            recognizer = new SpeechRecognizer(client, listener);
            recognizer.setAppKey(appKey);
            recognizer.setFormat(InputFormatEnum.PCM);
            recognizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            recognizer.setEnableIntermediateResult(false);
            recognizer.setEnablePunctuation(true);
            recognizer.setEnableITN(true);
            recognizer.addCustomedParam("enable_voice_detection", true);

            recognizer.start();

            // 分片发送音频数据，每片3200字节
            int offset = 0;
            int chunkSize = 3200;
            while (offset < audioData.length) {
                int len = Math.min(chunkSize, audioData.length - offset);
                byte[] chunk = new byte[len];
                System.arraycopy(audioData, offset, chunk, 0, len);
                recognizer.send(chunk, len);
                offset += len;
                // 模拟实时音频流，根据数据量计算sleep时长
                int deltaSleep = getSleepDelta(len, 16000);
                Thread.sleep(deltaSleep);
            }

            recognizer.stop();

            if (!latch.await(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("语音识别超时");
            }

            if (errorBuilder.length() > 0) {
                throw new RuntimeException("语音识别失败: " + errorBuilder);
            }

            return resultBuilder.toString();
        } finally {
            if (recognizer != null) {
                recognizer.close();
            }
        }
    }

    /**
     * 根据二进制数据大小计算对应的同等语音长度（ms）
     * 公式来源：官方Demo SpeechRecognizerDemo
     */
    private static int getSleepDelta(int dataSize, int sampleRate) {
        // 16位采样，单通道
        return (dataSize * 10 * 8000) / (160 * sampleRate);
    }
}
