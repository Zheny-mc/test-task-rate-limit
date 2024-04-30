package io.reflectoring.resilience4j.ratelimit;

import io.reflectoring.resilience4j.ratelimit.model.Document;
import io.reflectoring.resilience4j.ratelimit.services._CrptApi;
import io.reflectoring.resilience4j.ratelimit.services.HttpRequest;

import java.time.Duration;
import java.util.function.Supplier;


public class _Main {

    public static void main(String[] args) {

        Supplier<String> logic = () -> {
            Document doc = Document.create("src/main/resources/document.json");
            String result = HttpRequest.post("https://ismp.crpt.ru/api/v3/lk/documents/create", doc.convertToJson());
            return result;
        };

        _CrptApi crptApi = new _CrptApi(Duration.ofSeconds(1), 1).
                setTimeDuration(Duration.ofMillis(250)).
                addLogic(logic);

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
            crptApi.retryAndRateLimit();
        }

    }



}
