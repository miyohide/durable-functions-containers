package com.github.miyohide;

import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.durabletask.OrchestrationRunner;
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger;

public class SimpleBatchAppOrchestrator {
    @FunctionName("HelloCities")
    public String helloCitiesOrchestrator(@DurableOrchestrationTrigger(name = "runtimeState") String runtimeState) {
        return OrchestrationRunner.loadAndRun(runtimeState, ctx -> {
            String result = "";
            result += ctx.callActivity("SayHello", "Tokyo", String.class);
            result += ctx.callActivity("SayHello", "London", String.class);
            result += ctx.callActivity("SayHello", "Seattle", String.class);
            return result;
        });
    }
}
