package net.xinshi.discovery.search.mgt.mvc;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Generator;
import com.sun.syndication.feed.atom.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/4/13
 * Time: 11:14 AM
 */
@Controller
public class MessageController {

    @RequestMapping(value = "services/messages", method = RequestMethod.GET)
    public @ResponseBody Feed messages() {
        System.out.println("hello, messages");

        Feed feed = new Feed();

        feed.setFeedType("atom_1.0");
        feed.setTitle("messages");
        feed.setId("http://localhost:8080/services/messages");
        final Generator generator = new Generator();
        feed.setGenerator(generator);
        List<Person> authors = new ArrayList<Person>();
        Person p = new Person();
        p.setName("Discovery");
        authors.add(p);
        feed.setAuthors(authors);

        List<Entry> entries = new ArrayList<Entry>();
        feed.setEntries(entries);

        return feed;
    }
}
