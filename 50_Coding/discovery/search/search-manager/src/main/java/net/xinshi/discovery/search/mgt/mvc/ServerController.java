package net.xinshi.discovery.search.mgt.mvc;

import com.sun.syndication.feed.atom.Feed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/4/13
 * Time: 11:19 AM
 */
@Controller
@RequestMapping("services/server")
public class ServerController {

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public @ResponseBody Feed info() {
        System.out.println("hello, server info");
        Feed feed = new Feed("atom_1.0");

        return feed;
    }
}
