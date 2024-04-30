package io.reflectoring.resilience4j.ratelimit;

import java.time.Duration;
import java.util.function.Supplier;

public class Main {

    public static void main(String[] args) {

        Supplier<String> logic = () -> {
            CrptApi.Document doc =  CrptApi.WorkWithJson.create("src/main/resources/document.json");
            String result = CrptApi.HttpRequest.post("https://ismp.crpt.ru/api/v3/lk/documents/create", CrptApi.WorkWithJson.convertToJson(doc));
            return result;
        };

        CrptApi crptApi = new CrptApi(Duration.ofSeconds(1), 1).
                setTimeDuration(Duration.ofMillis(250)).
                addLogic(logic);

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
            crptApi.retryAndRateLimit();
        }

    }
}
