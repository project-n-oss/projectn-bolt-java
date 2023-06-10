package ai.granica.awssdk.services.s3;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;


public class GranicaConfig {

    static String ReadOrderEndpoints[] = { "main_read_endpoints", "main_write_endpoints", "failover_read_endpoints", "failover_write_endpoints" };
    static String WriteOrderEndpoints[] = { "main_write_endpoints", "failover_write_endpoints" };
    static List<String> HttpReadMethodTypes = Arrays.asList( "GET", "HEAD" ); // S3 operations get converted to one of the standard HTTP request methods https://docs.aws.amazon.com/apigateway/latest/developerguide/integrating-api-with-aws-services-s3.html
    
    public static String Region = System.getenv("AWS_REGION") == null ? EC2MetadataUtils.getEC2InstanceRegion(): System.getenv("AWS_REGION");
    public static String ZoneId = System.getenv("AWS_ZONE_ID") == null ? EC2MetadataUtils.getAvailabilityZone(): System.getenv("AWS_ZONE_ID");
    public static String CustomDomain =  System.getenv("GRANICA_CUSTOM_DOMAIN") != null ? System.getenv("GRANICA_CUSTOM_DOMAIN") : System.getenv("BOLT_CUSTOM_DOMAIN");
    public static String AuthBucket = System.getenv("BOLT_AUTH_BUCKET");
    public static String UserAgentPrefix = System.getenv("USER_AGENT_PREFIX") == null? "projectn/": String.format("%s/",System.getenv("USER_AGENT_PREFIX"));
    static String BoltHostname = String.format("bolt.%s.%s", Region, CustomDomain);
    static String QuicksilverUrl = String.format("https://quicksilver.%s.%s/services/bolt%s", Region, CustomDomain, ZoneId == null ? "": String.format("?az=%s", ZoneId));
    static LocalDateTime RefreshTime = LocalDateTime.now().plusSeconds(120);
    static Map<String, List<String>> BoltEndpoints = null;// getBoltEndpoints("");
    static Random rand = new Random();

    private static Map<String, List<String>> getBoltEndpoints(String errIp){
        if (QuicksilverUrl == null || Region == null){
            return null;
        }

        String requestUrl = errIp.length() > 0 ? 
                String.format("%s&err=%s",QuicksilverUrl, errIp) : QuicksilverUrl;
        
        Map<String, List<String>> out = parse(executeGetRequest(requestUrl));
        return out;
    }

    public static String executeGetRequest(String get_url) {
        // Retries 3 times by default
        CloseableHttpClient httpClient = HttpClients.custom().build();

        final HttpGet httpGet = new HttpGet(get_url);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return new BasicResponseHandler().handleResponse(response);            
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, List<String>> parse(String responseBody){
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(responseBody, new TypeReference<HashMap<String, List<String>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void refreshBoltEndpoint(String errIp){
        BoltEndpoints = getBoltEndpoints(errIp);
        RefreshTime = LocalDateTime.now().plusSeconds(60+ rand.nextInt(120));
    }

    public static URI selectBoltEndpoints(String httpRequestMethod){
        if (RefreshTime.isBefore(LocalDateTime.now()) || BoltEndpoints == null){
            refreshBoltEndpoint("");
        }
        String[] preferredOrder = HttpReadMethodTypes.contains(httpRequestMethod) ? ReadOrderEndpoints : WriteOrderEndpoints;

        // String[] endPointsKey;
        for (String endPointsKey : preferredOrder){

            if (BoltEndpoints.containsKey(endPointsKey) && BoltEndpoints.get(endPointsKey).size()>0){
                String selectedEndpoint = BoltEndpoints.get(endPointsKey).get(rand.nextInt(BoltEndpoints.get(endPointsKey).size()));
                try {
                    return new URI(String.format("https://%s",selectedEndpoint));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
