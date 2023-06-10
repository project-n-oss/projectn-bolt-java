package ai.granica.awssdk.services.s3;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;

public class GranicaHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String host, SSLSession sslSession) {
        DefaultHostnameVerifier df = new DefaultHostnameVerifier();
        return df.verify(GranicaConfig.BoltHostname, sslSession);
    }

}
