package dev.aziz.echobot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VkBotProperties {

    @Value("${vk.bot.token}")
    private String token;

    @Value("${vk.bot.group-id}")
    private String groupId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
