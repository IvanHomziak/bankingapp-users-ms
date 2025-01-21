package com.ihomziak.bankingapp.api.users.data;

import com.ihomziak.bankingapp.api.users.ui.model.AlbumResponseModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "albums-ws")
public interface AlbumsServiceClient {

    @GetMapping("/users/{userID}/albums")
    @Retry(name = "albums-ws")
    @CircuitBreaker(name = "albums-ws", fallbackMethod = "getAlbumsFallback")
    List<AlbumResponseModel> getAlbums(@PathVariable String userID);

    default List<AlbumResponseModel> getAlbumsFallback(String userID, Throwable throwable) {
        System.out.println("Param = " + userID);
        System.out.println("Exception: " + throwable);
        return new ArrayList<>();
    }
}
