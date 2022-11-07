package com.gitlab.projectn_oss.bolt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;


public class BoltConfig {

    static String ReadOrderEndpoints[] = { "main_read_endpoints", "main_write_endpoints", "failover_read_endpoints", "failover_write_endpoints" };
    static String WriteOrderEndpoints[] = { "main_write_endpoints", "failover_write_endpoints" };
    static List<String> HttpReadMethodTypes = Arrays.asList( "GET", "HEAD" ); // S3 operations get converted to one of the standard HTTP request methods https://docs.aws.amazon.com/apigateway/latest/developerguide/integrating-api-with-aws-services-s3.html
    
    public static String Region = System.getenv("AWS_REGION") == null
        ? EC2MetadataUtils.getEC2InstanceRegion(): System.getenv("AWS_REGION");
    public static String ZoneId = System.getenv("AWS_ZONE_ID") == null
        ? EC2MetadataUtils.getAvailabilityZone(): System.getenv("AWS_ZONE_ID");
    public static String CustomDomain = System.getenv("BOLT_CUSTOM_DOMAIN");
    public static String AuthBucket = System.getenv("BOLT_AUTH_BUCKET");
    static String BoltHostname = String.format("bolt.%s.%s", Region, CustomDomain);
    static String QuicksilverUrl = String.format("https://quicksilver.%s.%s/services/bolt%s", Region, CustomDomain, ZoneId == null ? "": String.format("?az=%s", ZoneId));
    //TODO: inplace of calling getBoltEndpoints, call refresh endpoints to avoid calling quicksilver everytime
    static Map<String, List<String>> BoltEndpoints = getBoltEndpoints("");
    public static Map<String, List<String>> getBoltEndpoints(String errIp){
        if (QuicksilverUrl == null){
            return null;
        }
        // BoltEndpoints
        String requestUrl = errIp.length() > 0 ? 
                String.format("%s&err=%s",QuicksilverUrl, errIp) : QuicksilverUrl;
        try{
            URL url = new URL(requestUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            return parse(content.toString());        
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    private static Map<String, List<String>> parse(String responseBody) throws JsonMappingException, JsonProcessingException {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(responseBody, new TypeReference<HashMap<String, List<String>>>() {});
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //TODO: Cleanup
        return map;
    }

    public static URI selectBoltEndpoints(String httpRequestMethod){
        if (BoltEndpoints == null){
            BoltEndpoints = getBoltEndpoints("");
        }
        String[] preferredOrder = HttpReadMethodTypes.contains(httpRequestMethod) ? ReadOrderEndpoints : WriteOrderEndpoints;

        // String[] endPointsKey;
        for (String endPointsKey : preferredOrder){

            if (BoltEndpoints.containsKey(endPointsKey) && BoltEndpoints.get(endPointsKey).size()>0){
                //TODO: select a rendom index instate of 0
                String selectedEndpoint = BoltEndpoints.get(endPointsKey).get(0);
                try {
                    return new URI(String.format("https://%s",selectedEndpoint));
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
