package com.example.momentix.domain.notification.slackdemo;


import com.example.momentix.domain.common.exception.event.EventErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.momentix.domain.common.exception.event.EventErrorCode.NO_SLACK;

@Service
@RequiredArgsConstructor
public class SlackMessageService {

    private final SlackService slackService;

    public void sendSlack(SlackMessageDto slackMessageDto) {

        try {
            slackService.send(slackMessageDto.getRecipientSlackId(), slackMessageDto.getSlackMessage());

        } catch (Exception e) {
            throw new EventErrorException(NO_SLACK);
        }
    }
}
