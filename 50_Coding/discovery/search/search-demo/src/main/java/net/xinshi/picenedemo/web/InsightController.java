package net.xinshi.picenedemo.web;

import net.xinshi.picenedemo.util.CipherUtil;
import net.xinshi.picenedemo.util.Md5Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class InsightController {
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMddHHmm");

	@RequestMapping("/insight/demo")
	public String demo( Model model) {

//        String userName = "saasAccount_80013";
        String userName = "saasAccount_340024";

//        String message = userName + ":" + dayFormat.format(new Date());
//
//        String password = Md5Service.encString(message, "infoscape");

        String message = "infoscape_insight_secret_" + userName;

        String password = null;
        try {
            password = CipherUtil.digestString(message, "MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

//        String url = "http://localhost:9000/en-US/account/saasLogin?username=" + userName + "&password=" + password + "&return_to=/en-US/app/search/chart_table/?q=search insighttype%3Aview front_type%3Aview |field session,pageName";
        String url = "http://10.10.10.87:9000/en-US/account/saasLogin?username=" + userName + "&password=" + password + "&return_to=/en-US/app/search/chart_table/?q=search insighttype%3Aview front_type%3Aview |field session,pageName";
        model.addAttribute("url", url);
        return "insight";
    }
}
