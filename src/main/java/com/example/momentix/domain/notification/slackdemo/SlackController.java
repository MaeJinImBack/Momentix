package com.example.momentix.domain.notification.slackdemo;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SlackController {

    private final SlackMessageService slackMessageService;


    //유틸 확인용
    @PostMapping("/send")
    public SlackMessageDto sendSlack(@RequestBody SlackMessageDto slackMessageDto) {

        slackMessageService.sendSlack(slackMessageDto);

        return slackMessageDto;
    }
}
