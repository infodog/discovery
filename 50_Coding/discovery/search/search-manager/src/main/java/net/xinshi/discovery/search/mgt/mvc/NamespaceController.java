package net.xinshi.discovery.search.mgt.mvc;

import com.sun.syndication.feed.atom.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/4/13
 * Time: 10:24 AM
 */
@Controller
@RequestMapping("servicesNS")
public class NamespaceController {
    public String insightHome = "";

    public NamespaceController() {
        this.insightHome = System.getProperty("insight.home");
        System.out.println("Insight.Home : " + this.insightHome);
    }

    @RequestMapping(value = "{owner}/{app}/admin/conf-{fileName}", method = RequestMethod.GET)
    public String adminConfig(@PathVariable String owner, @PathVariable String app, @PathVariable String fileName, Model model) {
        System.out.println("config file: " + fileName);
        File file = new File(this.insightHome + "/etc/system/default/" + fileName + ".conf");

        HierarchicalINIConfiguration iniConfObj = null;
        try {
            iniConfObj = new HierarchicalINIConfiguration(file);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        model.addAttribute("content", iniConfObj);

        return "atomView";
    }

    @RequestMapping(value = "{owner}/{app}/properties/{fileName}", method = RequestMethod.GET)
    public String properties(@PathVariable String owner, @PathVariable String app, @PathVariable String fileName, Model model) {
        System.out.println("properties file: " + fileName);
        File file = new File(this.insightHome + "/etc/apps/" + app + "/default/" + fileName + ".conf");
        try {

            HierarchicalINIConfiguration iniConfObj = null;
            try {
                iniConfObj = new HierarchicalINIConfiguration(file);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }

            model.addAttribute("content", iniConfObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "atomView";
    }


    ///mgt/servicesNS/admin/user-prefs/data/user-prefs/general
    @RequestMapping(value = "{owner}/{app}/data/{fileName}/{section}", method = RequestMethod.GET)
    public String local(@PathVariable String owner, @PathVariable String app, @PathVariable String fileName, @PathVariable String section, Model model) {
        System.out.println("properties file: " + fileName);
        File file = new File(this.insightHome + "/etc/apps/users/" + owner +"/" + app + "/local/" + fileName + ".conf");
        try {

            HierarchicalINIConfiguration iniConfObj = null;
            try {
                iniConfObj = new HierarchicalINIConfiguration(file);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
            model.addAttribute("content", iniConfObj.getSection(section));
            model.addAttribute("id", section);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "atomView";
    }


    @RequestMapping(value = "{owner}/{app}/data/ui/views", method = RequestMethod.GET)
    public
    @ResponseBody
    Feed getViews(@PathVariable String owner, @PathVariable String app) {
        System.out.println("hell, namespace, views");
        System.out.println(owner + app);
        Feed feed = new Feed();

        feed.setFeedType("atom_1.0");
        feed.setTitle("views");
        feed.setId("http://localhost:8080/servicesNS/admin/search/data/ui/views");
        final Generator generator = new Generator();
        feed.setGenerator(generator);
        List<Person> authors = new ArrayList<Person>();
        Person p = new Person();
        p.setName("Discovery");
        authors.add(p);
        feed.setAuthors(authors);

        List<Entry> entries = new ArrayList<Entry>();

        File file = new File(this.insightHome + "/etc/apps/" + app +"/default/data/ui/views");

        if (file != null && file.listFiles().length > 0) {
            for (File item : file.listFiles()) {
                if (item.isFile() && !item.isHidden()) {
                    String view = null;
                    try {
                        view = FileUtils.readFileToString(item.getAbsoluteFile(), "utf-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String name = item.getName().substring(0, item.getName().length() - 4);
                    Entry entry = new Entry();
                    entry.setTitle(name);
                    entry.setAuthors(authors);
                    entry.setId("http://localhost:8080/servicesNS/admin/search/data/ui/views/" + name);
                    Content content = new Content();
                    content.setType("text/xml");
                    content.setValue(generateContent(view, name));
                    List<Content> contents = new ArrayList<Content>();
                    contents.add(content);
                    entry.setContents(contents);

                    entries.add(entry);
                }
            }
        }


        feed.setEntries(entries);

        return feed;
    }


    private String generateContent(String view, String name) {
        String digest = null;
        try {
            digest = DigestUtils.md5Hex(name);
        } catch (Exception e) {
            e.printStackTrace();
            digest = name;
        }
        String s = "<dict>\n" +
                "        <key name=\"eai:acl\">\n" +
                "          <dict>\n" +
                "            <key name=\"app\">search</key>\n" +
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
                "                  </list>\n" +
                "                </key>\n" +
                "              </dict>\n" +
                "            </key>\n" +
                "            <key name=\"removable\">0</key>\n" +
                "            <key name=\"sharing\">system</key>\n" +
                "          </dict>\n" +
                "        </key>\n" +
                "        <key name=\"eai:appName\">search</key>\n" +
                "        <key name=\"eai:data\"><![CDATA[" + view + "]]></key>\n" +
                "<key name=\"eai:digest\">" + digest + "</key>" +
                "        <key name=\"eai:userName\">admin</key>\n" +
                "      </dict>\n";

        return s;
    }


    @RequestMapping(value = "{owner}/{app}/data/ui/manager", method = RequestMethod.GET)
    public
    @ResponseBody
    Feed uiManager(@PathVariable String owner, @PathVariable String app) {
        Feed feed = new Feed();

        feed.setFeedType("atom_1.0");
        feed.setTitle("manager");
        feed.setId("http://localhost:8080/servicesNS/admin/search/data/ui/manager");
        final Generator generator = new Generator();
        feed.setGenerator(generator);
        List<Person> authors = new ArrayList<Person>();
        Person p = new Person();
        p.setName("Discovery");
        authors.add(p);
        feed.setAuthors(authors);

        List<Entry> entries = new ArrayList<Entry>();

        File file = new File(this.insightHome + "/etc/apps/" + app +"/default/data/ui/manager");

        if (file != null && file.listFiles().length > 0) {
            for (File item : file.listFiles()) {
                if (item.isFile() && !item.isHidden()) {
                    String view = null;
                    try {
                        view = FileUtils.readFileToString(item.getAbsoluteFile(), "utf-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String name = item.getName().substring(0, item.getName().length() - 4);
                    Entry entry = new Entry();
                    entry.setTitle(name);
                    entry.setAuthors(authors);
                    entry.setId("http://localhost:8080/servicesNS/admin/search/data/ui/manager/" + name);
                    Content content = new Content();
                    content.setType("text/xml");
                    content.setValue(generateUImanagerContent(view));
                    List<Content> contents = new ArrayList<Content>();
                    contents.add(content);
                    entry.setContents(contents);

                    entries.add(entry);
                }
            }
        }


        feed.setEntries(entries);


        return feed;
    }

    private String generateUImanagerContent(String view) {

        String s = "<dict>\n" +
                "        <key name=\"eai:acl\">\n" +
                "          <dict>\n" +
                "            <key name=\"app\">search</key>\n" +
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
                "                  </list>\n" +
                "                </key>\n" +
                "              </dict>\n" +
                "            </key>\n" +
                "            <key name=\"removable\">0</key>\n" +
                "            <key name=\"sharing\">global</key>\n" +
                "          </dict>\n" +
                "        </key>\n" +
                "        <key name=\"eai:appName\">search</key>\n" +
                "        <key name=\"eai:data\"><![CDATA[" + view + "]]></key>\n" +
                "        <key name=\"eai:userName\">admin</key>\n" +
                "      </dict>\n";
        return s;
    }


    @RequestMapping(value = "{owner}/{app}/data/ui/nav/default", method = RequestMethod.GET)
    public
    @ResponseBody
    Feed navigator(@PathVariable String owner, @PathVariable String app) {
        Feed feed = new Feed();

        feed.setFeedType("atom_1.0");
        feed.setTitle("nav");
        feed.setId("http://localhost:8080/servicesNS/admin/search/data/ui/nav");
        final Generator generator = new Generator();
        feed.setGenerator(generator);
        List<Person> authors = new ArrayList<Person>();
        Person p = new Person();
        p.setName("Discovery");
        authors.add(p);
        feed.setAuthors(authors);

        List<Entry> entries = new ArrayList<Entry>();

        File file = new File(this.insightHome + "/etc/apps/" + app + "/default/data/ui/nav");

        if (file != null && file.listFiles().length > 0) {
            for (File item : file.listFiles()) {
                if (item.isFile() && !item.isHidden()) {
                    String view = null;
                    try {
                        view = FileUtils.readFileToString(item.getAbsoluteFile(), "utf-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String name = item.getName().substring(0, item.getName().length() - 4);
                    Entry entry = new Entry();
                    entry.setTitle(name);
                    entry.setAuthors(authors);
                    entry.setId("http://localhost:8080/servicesNS/admin/search/data/ui/nav/" + name);
                    Content content = new Content();
                    content.setType("text/xml");
                    content.setValue(generateNavContent(view));
                    List<Content> contents = new ArrayList<Content>();
                    contents.add(content);
                    entry.setContents(contents);

                    entries.add(entry);
                }
            }
        }


        feed.setEntries(entries);

        return feed;
    }


    @RequestMapping(value = "{owner}/{app}/search/parser", method = RequestMethod.GET, produces = "application/xml; charset=utf-8")
    public
    @ResponseBody
    String searchParser(@PathVariable String owner, @PathVariable String app, @RequestParam String q) {
        String args = q;
        if (args.startsWith("search")) {
            args = args.substring(6);
        }

        String result = "<response>\n" +
                "  <dict>\n" +
                "    <key name=\"remoteSearch\">" + q + "</key>\n" +
                "    <key name=\"remoteTimeOrdered\">1</key>\n" +
                "    <key name=\"eventsTimeOrdered\">1</key>\n" +
                "    <key name=\"eventsStreaming\">1</key>\n" +
                "    <key name=\"reportsSearch\"></key>\n" +
                "    <key name=\"canSummarize\">0</key>\n" +
                "  </dict>\n" +
                "  <list>\n" +
                "    <item>\n" +
                "      <dict>\n" +
                "        <key name=\"command\">search</key>\n" +
                "        <key name=\"rawargs\">" + args + "</key>\n" +
                "        <key name=\"pipeline\">streaming</key>\n" +
                "        <key name=\"args\">\n" +
                "          <dict>\n" +
                "            <key name=\"search\">\n" +
                "              <list>\n" +
                "                <item>" + args + "</item>\n" +
                "              </list>\n" +
                "            </key>\n" +
                "          </dict>\n" +
                "        </key>\n" +
                "        <key name=\"isGenerating\">1</key>\n" +
                "        <key name=\"streamType\">SP_STREAM</key>\n" +
                "      </dict>\n" +
                "    </item>\n" +
                "  </list>\n" +
                "</response>";
        return result;
    }


    private String generateNavContent(String view) {

        String s = "<dict>\n" +
                "        <key name=\"eai:acl\">\n" +
                "          <dict>\n" +
                "            <key name=\"app\">search</key>\n" +
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
                "                  </list>\n" +
                "                </key>\n" +
                "              </dict>\n" +
                "            </key>\n" +
                "            <key name=\"removable\">0</key>\n" +
                "            <key name=\"sharing\">system</key>\n" +
                "          </dict>\n" +
                "        </key>\n" +
                "        <key name=\"eai:appName\">search</key>\n" +
                "        <key name=\"eai:data\"><![CDATA[" + view + "]]></key>\n" +
                "        <key name=\"eai:userName\">admin</key>\n" +
                "      </dict>\n";
        return s;
    }
}
