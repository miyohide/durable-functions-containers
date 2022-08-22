package com.github.miyohide;

import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.durabletask.azurefunctions.DurableActivityTrigger;

public class SimpleBatchAppActivity {
    @FunctionName("SayHello")
    public String sayHello(@DurableActivityTrigger(name = "name") String name) {
        return String.format("Hello %s!", name);
    }
}
