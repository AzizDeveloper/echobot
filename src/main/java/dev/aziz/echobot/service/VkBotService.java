package dev.aziz.echobot.service;

import dev.aziz.echobot.config.VkBotProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;

@Service
public class VkBotService {

    private final VkBotProperties props;
    private final String API_VERSION = "5.199";
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://api.vk.com/method";

    public VkBotService(VkBotProperties props) {
        this.props = props;
    }

    public void run() throws InterruptedException {
        Map<String, Object> serverData = getLongPollServer();

        String server = (String) serverData.get("server");
        String key = (String) serverData.get("key");
        String ts = (String) serverData.get("ts");

        while (true) {
            String url = String.format("%s?act=a_check&key=%s&ts=%s&wait=25", server, key, ts);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("updates")) continue;

            ts = (String) response.get("ts");

            var updates = (Iterable<Map<String, Object>>) response.get("updates");
            for (Map<String, Object> update : updates) {
                if ("message_new".equals(update.get("type"))) {
                    Map<String, Object> object = (Map<String, Object>) update.get("object");
                    Map<String, Object> message = (Map<String, Object>) object.get("message");

                    Integer userId = (Integer) message.get("from_id");
                    String text = (String) message.get("text");

                    sendMessage(userId, "Вы сказали: " + text);
                }
            }

            Thread.sleep(500);
        }
    }

    private Map<String, Object> getLongPollServer() {
        String url = String.format(
                BASE_URL + "/groups.getLongPollServer?group_id=%s&access_token=%s&v=%s",
                props.getGroupId(), props.getToken(), API_VERSION
        );

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map body = response.getBody();

        if (body == null || body.get("response") == null) {
            System.out.println("Response body is null");
            return null;
        }

        return (Map<String, Object>) body.get("response");
    }

    private void sendMessage(int userId, String message) {
        int randomId = new Random().nextInt(Integer.MAX_VALUE);
        String url = BASE_URL + "/messages.send";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("user_id", String.valueOf(userId));
        params.add("message", message);
        params.add("access_token", props.getToken());
        params.add("v", API_VERSION);
        params.add("random_id", String.valueOf(randomId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        restTemplate.postForEntity(url, request, String.class);
    }

}