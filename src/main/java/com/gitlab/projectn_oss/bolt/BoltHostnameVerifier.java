package com.gitlab.projectn_oss.bolt;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;

public class BoltHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String host, SSLSession sslSession) {
        DefaultHostnameVerifier df = new DefaultHostnameVerifier();
        return df.verify(BoltConfig.BoltHostname, sslSession);
    }

}
