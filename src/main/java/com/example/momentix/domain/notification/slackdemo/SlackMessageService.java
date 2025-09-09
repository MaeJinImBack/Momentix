package com.example.momentix.domain.notification.slackdemo;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackMessageService {

    private final SlackService slackService;

    public void sendSlack(SlackMessageDto slackMessageDto) {

        try {
            slackService.send(slackMessageDto.getRecipientSlackId(), slackMessageDto.getSlackMessage());

        } catch (Exception e) {
            throw new RuntimeException("슬랙 메시지 전송 실패", e);
        }
    }
}
