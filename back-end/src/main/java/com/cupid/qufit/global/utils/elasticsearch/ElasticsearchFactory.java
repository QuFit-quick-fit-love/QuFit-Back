package com.cupid.qufit.global.utils.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticsearchFactory {

    // TODO: AWS 서버 띄운 후 application.properties에 설정 필요.
    //  연동이 안되더라도 프로젝트 실행 가능
    @Value("${elasticsearch.url}")
    private String serverUrl;
    @Value("${elasticsearch.apikey}")
    private String apiKey;
    @Value("${elasticsearch.username}")
    private String username;
    @Value("${elasticsearch.password}")
    private String userpassword;
    @Value("${elasticsearch.fingerprint}")
    private String fingerprint;

    ElasticsearchClient getElasticsearchClient() {
        RestClient restClient = getRestClient();
        return new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }

    public RestClient getRestClient() {
        SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint);
        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, userpassword));
        return RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{new BasicHeader("Authorization", "ApiKey" + apiKey)})
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv))
                .build();
    }


}
