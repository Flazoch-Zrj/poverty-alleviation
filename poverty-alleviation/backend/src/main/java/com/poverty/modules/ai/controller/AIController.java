package com.poverty.modules.ai.controller;

import com.poverty.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Api(tags = "AI 助手接口")
@RestController
@RequestMapping("/api/v1/ai")
public class AIController {

    @Value("${deepseek.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @ApiOperation("AI 对话")
    @PostMapping("/chat")
    public R<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return R.fail("请输入消息");
        }

        try {
            // 构建 DeepSeek API 请求
            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");

            List<Map<String, String>> messages = new ArrayList<>();
            // 系统提示词 - 扶贫专家
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一位专业的中国扶贫政策专家和乡村振兴顾问，精通国家扶贫政策、帮扶措施、产业发展、就业培训、教育医疗等领域的知识。" +
                    "你的任务是帮助扶贫干部、贫困户和志愿者解答关于扶贫政策、帮扶方法、补贴标准、产业选择等方面的问题。" +
                    "请用简洁、易懂的中文回答，适当使用例子和数据进行说明。回答要结合实际、客观准确、建议具体可行。" +
                    "如果涉及具体政策，请注明政策参考范围。如果不知道确切信息，请诚实说明。");
            messages.add(systemMsg);

            // 用户消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", message);
            messages.add(userMsg);

            body.put("messages", messages);
            body.put("temperature", 0.7);
            body.put("max_tokens", 2000);

            // 构建 HTTP 请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 调用 DeepSeek API
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.deepseek.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> delta = (Map<String, String>) choice.get("message");
                    result.put("reply", delta.get("content"));
                }
                return R.ok(result);
            }

            return R.fail("AI 服务暂未响应，请稍后重试");

        } catch (Exception e) {
            return R.fail("AI 服务调用失败: " + e.getMessage());
        }
    }
}
