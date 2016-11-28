package net.xinshi.discovery.search.mgt.mvc;

import net.xinshi.discovery.search.mgt.auth.SessionMgt;
import net.xinshi.discovery.search.mgt.util.CipherUtil;
import net.xinshi.discovery.search.mgt.util.Md5Service;
import net.xinshi.discovery.search.mgt.util.MgtUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 12/28/12
 * Time: 5:22 PM
 */
@Controller
@RequestMapping("services/auth")
public class LoginController {
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMddHHmm");

    @RequestMapping(value = "/login", method= RequestMethod.POST)
    public @ResponseBody String login(@RequestBody String creds) {
        Map<String, List<String>> params = null;
        try {
            params = MgtUtils.parsePostBody(creds);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String username = null;
        String password = null;
        try {
            username = params.get("username").get(0);
            password = params.get("password").get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("login...");
        System.out.println(username);

        if ("admin".equals(username) && "iloveinsight".equals(password)) {
            String sessionKey = UUID.randomUUID().toString();

            Map<String, String> values = new HashMap<String, String>();
            values.put("user", username);
            SessionMgt.sessions.put("Dinsight " + sessionKey, values);

            return "<response>\n" +
                    "  <sessionKey>" + sessionKey + "</sessionKey>\n" +
                    "</response>";
        }

        return "<response>\n" +
                "  <sessionKey>error</sessionKey>\n" +
                "</response>";
    }

    @RequestMapping(value = "/saasLogin", method= RequestMethod.POST)
    public @ResponseBody String saasLogin(@RequestBody String creds) throws Exception{
        Map<String, List<String>> params = null;
        try {
            params = MgtUtils.parsePostBody(creds);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String username = null;
        String password = null;
        try {
            username = params.get("username").get(0).trim();
            password = params.get("password").get(0).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("saas login...");
        System.out.println(username);

        String message = "infoscape_insight_secret_" + username;

        try {
            String digest = DigestUtils.md5Hex(message);

            if (digest != null && digest.equals(password)) {
                String sessionKey = UUID.randomUUID().toString();

                //String user = username;

                Map<String, String> values = new HashMap<String, String>();
                values.put("user", username);
                //values.put("mid", mid);

                SessionMgt.sessions.put("Dinsight " + sessionKey, values);
                System.out.println(username + " saas login successfully");
                return "<response>\n" +
                        "  <sessionKey>" + sessionKey + "</sessionKey>\n" +
                        "</response>";
            } else {
                System.out.println(digest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        String message = Md5Service.decString(password, "infoscape");
//        String[] ms = message.split(":");
//        String mid = null;
//        String time = null;
//        if(ms != null && ms.length == 2) {
//            mid = ms[0];
//            time = ms[1];
//        } else {
//
//            return "<response>\n" +
//                    "  <sessionKey>error</sessionKey>\n" +
//                    "</response>";
//        }
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.MINUTE, -30);
//        String begin = dayFormat.format(c.getTime());
//        c = Calendar.getInstance();
//        c.add(Calendar.MINUTE, 30);
//        String end = dayFormat.format(c.getTime());
//
//
//        if(mid != null && mid.equals(username.trim()) && time.compareTo(begin) >=0  && time.compareTo(end) <= 0) {
//            String sessionKey = UUID.randomUUID().toString();
//
//            Map<String, String> values = new HashMap<String, String>();
//            values.put("user", username);
//            SessionMgt.sessions.put("Dinsight " + sessionKey, values);
//            System.out.println(username + " saas login successfully");
//
//            return "<response>\n" +
//                    "  <sessionKey>" + sessionKey + "</sessionKey>\n" +
//                    "</response>";
//        }

        throw new Exception("您是黑客，请自首！");
//        return "<response>\n" +
//                "  <sessionKey>error</sessionKey>\n" +
//                "</response>";
    }
}
