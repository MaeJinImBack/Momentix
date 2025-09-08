package com.example.momentix.domain.notification.slackdemo;


import lombok.Getter;

@Getter
public class SlackMessageDto {

    private String recipientSlackId;

    private String slackMessage;
}
