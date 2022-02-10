package com.foo.rpc.examples.spring.hypermutation;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class HypermutationApp {


    public static void main(String[] args) {
        SpringApplication.run(HypermutationApp.class, args);
    }

    @Bean
    public TProtocolFactory tProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }

    @Bean
    public ServletRegistrationBean dbBaseServlet(TProtocolFactory protocolFactory, HypermutationServiceImp service) {
        TServlet tServlet =  new TServlet(new HypermutationService.Processor<>(service), protocolFactory);
        return new ServletRegistrationBean(tServlet, "/hypermutation");
    }

}
