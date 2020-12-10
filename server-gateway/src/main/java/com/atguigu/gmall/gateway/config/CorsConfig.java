package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
@Configuration
public class CorsConfig {

    //  制作跨域：
    //  <bean class="org.springframework.web.cors.reactive.CorsWebFilter" ></bean>
    @Bean
    public CorsWebFilter corsWebFilter(){
        //  创建对象CorsConfiguration
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //  设置跨域的相关参数
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);
        //  返回当前对象
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        //  配置哪些需要过滤的路径。
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        //  CorsConfigurationSource 它是一个接口
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
