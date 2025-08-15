package com.qiuzhitech.onlineshopping_07.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {
    @Bean
    public RestHighLevelClient EsClient() {
        return new RestHighLevelClient(
                //default port number for es search
                RestClient.builder(new HttpHost("localhost", 9200,"http" ))
        );
    }
}
