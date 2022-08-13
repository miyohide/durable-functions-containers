// Azure-Samples/aci-java-create-container-groupsにあるUtils class
// を参考に実装
// see. https://github.com/Azure-Samples/aci-java-create-container-groups
package com.github.miyohide;

import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.*;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpLoggingPolicy;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.AzureResourceManager;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeoutException;


public class Utils {
    private static final ClientLogger LOGGER = new ClientLogger(Utils.class);

    public static String sendGetRequest(String urlString) {
        HttpRequest request = new HttpRequest(HttpMethod.GET, urlString);
        Mono<Response<String>> response =
                stringResponse(HTTP_PIPELINE.send(request)
                        .flatMap(response1 -> {
                            int code = response1.getStatusCode();
                            if (code == 200 || code == 400 || code == 404) {
                                return Mono.just(response1);
                            } else {
                                return Mono.error(new HttpResponseException(response1));
                            }
                        })
                        .retryWhen(Retry
                                .fixedDelay(5, Duration.ofSeconds(30))
                                .filter(t -> {
                                    boolean retry = false;
                                    if (t instanceof TimeoutException) {
                                        retry = true;
                                    } else if (t instanceof HttpResponseException
                                            && ((HttpResponseException) t).getResponse().getStatusCode() == 503
                                    ) {
                                        retry = true;
                                    }

                                    if (retry) {
                                        LOGGER.info("retry GET request to {}", urlString);
                                    }
                                    return retry;
                                })
                        )
                );
        Response<String> ret = response.block();
        return ret == null ? null : ret.getValue();
    }

    public static Mono<Response<String>> stringResponse(Mono<HttpResponse> responseMono) {
        return responseMono.flatMap(response -> response.getBodyAsString()
                .map(str -> new SimpleResponse<>(response.getRequest(), response.getStatusCode(), response.getHeaders(), str)));
    }

    public static final HttpPipeline HTTP_PIPELINE = new HttpPipelineBuilder()
            .policies(
                    new HttpLoggingPolicy(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC)),
                    new RetryPolicy("Retry-After", ChronoUnit.SECONDS)
            ).build();

    public static String randomResourceName(AzureResourceManager azure, String prefix, int maxLen) {
        return azure.resourceGroups().manager().internalContext().randomResourceName(prefix, maxLen);
    }
}
