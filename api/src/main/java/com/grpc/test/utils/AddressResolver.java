package com.grpc.test.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AddressResolver {

    public static final String LOCAL_ADDRESS = resolveLocalAddress();

    private AddressResolver() {
        // Nope
    }

    private static String resolveLocalAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName() + " " + localHost.getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown host (.́_.̀ )";
        }
    }
}
