package it.globalx.velocity.module.implementations.chatfiltering.check;

import it.globalx.velocity.module.implementations.chatfiltering.check.result.CheckResult;
import it.globalx.velocity.module.implementations.chatfiltering.check.result.type.CheckResultType;

public abstract class Check {

    public abstract CheckResult check(String message);

}
