package io.reflectoring.resilience4j.ratelimit;

import com.google.gson.Gson;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

public class CrptApi {
    @Getter @Setter
    private int requestLimit;
    @Getter @Setter
    private Duration timeRefreshPeriod;
    @Getter
    private Duration timeDuration;

    @Getter @Setter
    private int countMaxAttempts = 2;
    @Getter @Setter
    private Duration timeWaitDuration;

    private Supplier<String> rateLimitedSupplier;
    private Supplier<String> retryingSupplier;

    public CrptApi(Duration timeRefreshPeriod, int requestLimit) {
        this.timeRefreshPeriod = timeRefreshPeriod;
        this.requestLimit = requestLimit;
        this.timeWaitDuration = timeRefreshPeriod;
    }

    public CrptApi setTimeDuration(Duration timeDuration) {
        this.timeDuration = timeDuration;
        return this;
    }

    public CrptApi addLogic(Supplier<String> mainLogic) {
        configuration(mainLogic);
        return this;
    }

    public void configuration(Supplier<String> logic) {
        RateLimiterConfig config = RateLimiterConfig.custom().
                limitForPeriod(requestLimit).
                limitRefreshPeriod(timeRefreshPeriod).
                timeoutDuration(timeDuration).build();
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        RateLimiter rateLimiter = registry.rateLimiter("Send document");

        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(countMaxAttempts).
                waitDuration(timeWaitDuration).build();
        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);
        Retry retry = retryRegistry.retry("Send document", retryConfig);
        retry.getEventPublisher().onRetry(System.out::println);
        retry.getEventPublisher().onSuccess(System.out::println);

        rateLimitedSupplier = RateLimiter.decorateSupplier(rateLimiter, logic);
        retryingSupplier = Retry.decorateSupplier(retry, rateLimitedSupplier);
    }

    public void retryAndRateLimit() {
        System.out.println(retryingSupplier.get());
        System.out.println();
    }

    // -----------------------------------------------------------------------
    public static class HttpRequest {
        public static String post(String url, String jsonBody) {
            String result = null;
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpClient httpclient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpclient.execute(httpPost))
            {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    // ----------------------------Json--------------------------------
    public static class WorkWithJson {
        public static Document create(String path) {
//        path = "src/main/resources/document.json";
            try (FileReader reader = new FileReader(path)) {
                Gson gson = new Gson();
                Document document = gson.fromJson(reader, Document.class);
                return document;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        public static String convertToJson(Document doc) {
            Gson gson = new Gson();
            return gson.toJson(doc);
        }
    }
    // --------------------------model------------------------------

    @Data
    public class Description {
        private String participantInn;
    }

    @Data
    public class Product {
        private String certificate_document;
        private String certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private String production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;
    }

    @Data
    public class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private List<Product> products;
        private String reg_date;
        private String reg_number;
    }

}
