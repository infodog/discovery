package net.xinshi.discovery.search.mgt.mvc;

import com.sun.syndication.feed.atom.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 12/31/12
 * Time: 3:12 PM
 */
@Controller
@RequestMapping("/services/apps")
public class AppsController {

    @RequestMapping(value = "/local", method=RequestMethod.GET)
    public @ResponseBody Feed local(){
        System.out.println("hello, apps local");

        Feed feed = new Feed();

        feed.setFeedType("atom_1.0");
        feed.setTitle("localapps");
        feed.setId("http://localhost:8080/services/apps/local");
        final Generator generator = new Generator();
        feed.setGenerator(generator);
        List<Person> authors = new ArrayList<Person>();
        Person p = new Person();
        p.setName("Discovery");
        authors.add(p);
        feed.setAuthors(authors);

        List<Entry> entries = new ArrayList<Entry>();
        Entry entry = new Entry();
        entry.setTitle("search");
        entry.setAuthors(authors);
        entry.setId("http://localhost:8080/services/apps/local/search");
        Content content = new Content();
        content.setType("text/xml");
        content.setValue(generateContent("search"));
        List<Content> contents = new ArrayList<Content>();
        contents.add(content);
        entry.setContents(contents);

        entries.add(entry);


        Entry dentry = new Entry();
        dentry.setTitle("forMerchant");
        dentry.setAuthors(authors);
        dentry.setId("http://localhost:8080/services/apps/local/forMerchant");
        Content dcontent = new Content();
        dcontent.setType("text/xml");
        dcontent.setValue(generateContent("forMerchant"));
        List<Content> dcontents = new ArrayList<Content>();
        dcontents.add(dcontent);
        dentry.setContents(dcontents);
        entries.add(dentry);

        entry = new Entry();
        entry.setTitle("userStat");
        entry.setAuthors(authors);
        entry.setId("http://localhost:8080/services/apps/local/userStat");
        content = new Content();
        content.setType("text/xml");
        content.setValue(generateContent("userStat"));
        contents = new ArrayList<Content>();
        contents.add(content);
        entry.setContents(contents);
        entries.add(entry);
        feed.setEntries(entries);
        return feed;
    }

    private String generateContent(String name) {
        String s = "" +
                "      <dict>\n" +
                "        <key name=\"author\">Ben</key>\n" +
                "        <key name=\"check_for_updates\">1</key>\n" +
                "        <key name=\"configured\">1</key>\n" +
                "        <key name=\"description\">" + name +" App </key>\n" +
                "        <key name=\"disabled\">0</key>\n" +
                "        <key name=\"eai:acl\">\n" +
                "          <dict>\n" +
                "            <key name=\"app\">system</key>\n" +
                "            <key name=\"can_change_perms\">1</key>\n" +
                "            <key name=\"can_list\">1</key>\n" +
                "            <key name=\"can_share_app\">1</key>\n" +
                "            <key name=\"can_share_global\">1</key>\n" +
                "            <key name=\"can_share_user\">0</key>\n" +
                "            <key name=\"can_write\">1</key>\n" +
                "            <key name=\"modifiable\">1</key>\n" +
                "            <key name=\"owner\">nobody</key>\n" +
                "            <key name=\"perms\">\n" +
                "              <dict>\n" +
                "                <key name=\"read\">\n" +
                "                  <list>\n" +
                "                    <item>*</item>\n" +
                "                  </list>\n" +
                "                </key>\n" +
                "                <key name=\"write\">\n" +
                "                  <list>\n" +
                "                    <item>admin</item>\n" +
                "                    <item>power</item>\n" +
                "                  </list>\n" +
                "                </key>\n" +
                "              </dict>\n" +
                "            </key>\n" +
                "            <key name=\"removable\">0</key>\n" +
                "            <key name=\"sharing\">app</key>\n" +
                "          </dict>\n" +
                "        </key>\n" +
                "        <key name=\"label\">" + name+ "</key>\n" +
                "        <key name=\"manageable\">1</key>\n" +
                "        <key name=\"state_change_requires_restart\">0</key>\n" +
                "        <key name=\"version\">5.0</key>\n" +
                "        <key name=\"visible\">1</key>\n" +
                "      </dict>";
        return s;
    }


    @RequestMapping(value="/atom", method=RequestMethod.GET)
    public @ResponseBody Feed writeFeed() {
        System.out.println("hello, atom");
        Feed feed = new Feed();
        feed.setFeedType("atom_1.0");
        feed.setTitle("My Atom feed");
        return feed;
    }
}
