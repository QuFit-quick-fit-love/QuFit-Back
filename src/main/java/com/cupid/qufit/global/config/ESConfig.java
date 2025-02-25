//package com.cupid.qufit.global.config;
//
//import co.elastic.clients.transport.TransportUtils;
//import javax.net.ssl.SSLContext;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
//
////TODO: Elastic Search 살리기: Config 살리기
////@Configuration
//public class ESConfig extends ElasticsearchConfiguration {
//
//    @Value("${elasticsearch.url}")
//    private String host;
//
//    @Value("${elasticsearch.username}")
//    private String username;
//
//    @Value("${elasticsearch.password}")
//    private String password;
//
//    @Value("${elasticsearch.fingerprint}")
//    private String fingerprint;
//
//
//    @NotNull
//    @Override
//    public ClientConfiguration clientConfiguration() {
//        SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint);
//        return ClientConfiguration.builder()
//                                  .connectedTo(host)
//                                  .usingSsl(sslContext, (hostname, session) -> true) // ssl 사용
//                                  .withBasicAuth(username, password)
//                                  .build();
//    }
//}
