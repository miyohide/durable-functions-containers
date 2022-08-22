package com.github.miyohide;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.durabletask.DurableTaskClient;
import com.microsoft.durabletask.azurefunctions.DurableClientContext;
import com.microsoft.durabletask.azurefunctions.DurableClientInput;

import java.util.Optional;

public class SimpleBatchApp {
    @FunctionName("StartHelloCities")
    public HttpResponseMessage startHelloCities(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}) HttpRequestMessage<Optional<String>> req,
            @DurableClientInput(name = "durableContext") DurableClientContext durableContext,
            final ExecutionContext context
            ) {
        DurableTaskClient client = durableContext.getClient();
        String instanceId = client.scheduleNewOrchestrationInstance("HelloCities");
        return durableContext.createCheckStatusResponse(req, instanceId);
    }
}
