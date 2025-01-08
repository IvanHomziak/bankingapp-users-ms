package com.ihomziak.bankingapp.api.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BankingAppApiUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingAppApiUsersApplication.class, args);
    }

}
