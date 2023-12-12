package com.nha.abdm.wrapper.hrp.discoveryLinking.services;

import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;

import java.net.URISyntaxException;

public interface DiscoverLinkingService {
    void onDiscoverCall(DiscoverResponse data) throws URISyntaxException;

    void onInitCall(InitResponse data) throws URISyntaxException;

    void onConfirmCall(ConfirmResponse data) throws URISyntaxException;
}
