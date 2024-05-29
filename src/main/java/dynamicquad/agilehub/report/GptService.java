package dynamicquad.agilehub.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GptService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    public String summarizeIssue(String issueContent) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 메시지 형식에 맞게 요청 본문 구성
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "Summarize the following issue in Korean: " + issueContent);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(message));
        requestBody.put("max_tokens", 150);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();

        // 응답 처리 수정
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        Map<String, Object> choice = choices.get(0);
        Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");
        String summary = (String) messageObj.get("content");

        return summary.trim();
    }

    public String summarizeIssues(List<String> contents) {
        // 여러 이슈를 한 번에 요약하기 위해 내용을 하나의 문자열로 결합
        String combinedContent = String.join("\n", contents);

        // GPT 서비스 호출
        String combinedSummary = summarizeIssue(combinedContent);

        // 각 이슈에 대한 요약을 추출하여 반환
        String[] summaries = combinedSummary.split("\n");
        StringBuilder result = new StringBuilder();
        for (String summary : summaries) {
            result.append("Summary: ").append(summary).append("\n");
        }
        return result.toString();
    }
}
