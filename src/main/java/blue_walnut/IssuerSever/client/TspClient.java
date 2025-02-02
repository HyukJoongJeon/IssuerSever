package blue_walnut.IssuerSever.client;

import blue_walnut.IssuerSever.exception.ErrorCode;
import blue_walnut.IssuerSever.exception.RetryableException;
import blue_walnut.IssuerSever.exception.TokenException;
import blue_walnut.IssuerSever.model.TokenReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Slf4j
@Component("TspClient")
@RequiredArgsConstructor
public class TspClient {
    private final RestClient restClient = RestClient.create("http://localhost:8085/tsp");
    private final ObjectMapper objectMapper;

    @Retryable(value = { RetryableException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 1.5))
    public Boolean verifyToken(TokenReq req) {
        return exchange("/verifyToken", req);
    }

    private Boolean exchange(String path, Object requestBody) {
        try {
            return restClient.patch().uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(Boolean.class)
                    .getBody();
        } catch (HttpClientErrorException e) {
            // 4xx 에러는 클라이언트 측의 오류로 재시도할 필요가 없지만 429 Too Many Requests일 경우 재시도
            if (e.getStatusCode().is4xxClientError()) {
                if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                    throw new RetryableException(ErrorCode.TOO_MANY_REQUESTS);
                }
                throw new TokenException(ErrorCode.SERVER_ERROR);
            }
            // 5xx 서버 오류는 재시도 필요
            if (e.getStatusCode().is5xxServerError()) {
                throw new RetryableException(ErrorCode.SERVER_500_ERR);
            }
        } catch (ResourceAccessException e) {
            // 네트워크 오류(타임아웃)도 재시도해야 하므로 RetryableException 던짐
            throw new RetryableException(ErrorCode.NETWORK_TIMEOUT);
        } catch (RestClientResponseException rcre) {
            throw new TokenException(ErrorCode.fromErrCode(MapUtils.getString(extractErrorMessage(rcre), "code")));
        }

        throw new TokenException(ErrorCode.UNKNOWN_ERROR);
    }

    private Map<String, Object> extractErrorMessage(RestClientResponseException rcre) {
        try {
            return objectMapper.readValue(rcre.getResponseBodyAsString(), Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error processing response body", e);
            return null;
        }
    }
}

