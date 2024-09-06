package it.globalx.velocity.module.implementations.chatfiltering.check.result;

import it.globalx.velocity.module.implementations.chatfiltering.check.result.type.CheckResultType;

public record CheckResult(CheckResultType checkResultType, String newMessage) {

}
