package application.services;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import com.google.gson.Gson;


public class AuthService {

    public class HashMethods {
        public static final  String MD5 = "md5";
    }

    private Map<Long, String> mapIdSecret;

    public AuthService() {
        this.mapIdSecret = new ConcurrentHashMap<Long, String>();
        this.mapIdSecret.put(0L, "SuperSecretString");
    }


    public String getJsonWebToken(Long userId, Long secretId, String hashMethod) {

        /*
        Gson gson = new Gson();
        String json = gson.toJson(myObject);
        */

        final String header = "{\"alg\":\"" + hashMethod + "\",\"typ\": \"JWT\"}";

        Map<String, String> payloadMap = new HashMap<String, String>();
        payloadMap.put("userId", userId.toString());

        return "";
    }
}
