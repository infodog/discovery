package net.xinshi.discovery.search.mgt.mvc;

import com.google.common.base.Charsets;
import com.sun.syndication.feed.atom.Entry;
import net.xinshi.discovery.search.mgt.bean.SavedSearch;
import net.xinshi.discovery.search.mgt.util.MgtUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.solr.util.MapUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/23/13
 * Time: 3:11 PM
 */
@Controller
@RequestMapping("servicesNS")
public class SavedSearchController {
    private Map<String, SavedSearch> savedMap = new HashMap<String, SavedSearch>();


    public SavedSearchController() {
        String fileName = "savedsearches";
        String insightHome = System.getProperty("insight.home");
        String path = insightHome + "/etc/apps/search/default/" + fileName + ".conf";
        System.out.println(path);
        File file = new File(path);
        if (file.exists()) {
            try {

                HierarchicalINIConfiguration iniConfObj = null;
                try {
                    iniConfObj = new HierarchicalINIConfiguration();
                    System.out.println(Charsets.UTF_8.toString());
                    iniConfObj.load(new FileInputStream(file), Charsets.UTF_8.toString());

                    Set setOfSections = iniConfObj.getSections();
                    Iterator sectionNames = setOfSections.iterator();

                    while (sectionNames.hasNext()) {
                        String sectionName = sectionNames.next().toString();
                        System.out.println("[" + sectionName + "]");

                        SubnodeConfiguration sObj = iniConfObj.getSection(sectionName);

                        SavedSearch job = this.getSavedSearchFromProp(sectionName, sObj);
                        this.savedMap.put(sectionName , job);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(path + " does not exist!");
        }
    }

    private SavedSearch getSavedSearchFromProp(String sectionName, SubnodeConfiguration sObj) {
        SavedSearch job = new SavedSearch();
        Iterator it1 = sObj.getKeys();

        Map<String, List<String>> params = new HashMap<String, List<String>>();
        if (sObj != null) {
            while (it1.hasNext()) {
                Object key = it1.next();
                System.out.println(key + " : " + sObj.getString(key.toString()));
                List<String> values = new ArrayList<String>();
                values.add(sObj.getString(key.toString()));
                params.put(key.toString(), values);
            }
            MgtUtils.fillValues(job, params);
        }

        return job;
    }

    @RequestMapping(value = "{owner}/{app}/saved/searches/_new", method = RequestMethod.GET)
    public String newSearch(@PathVariable String owner, @PathVariable String app, Model model) {
        System.out.println("saved searches new !");

        SavedSearch savedSearch = new SavedSearch();
        model.addAttribute("title", "savedsearch");
        model.addAttribute("id", "/servicesNS/admin/search/saved/searches");
        model.addAttribute("content", savedSearch);

        return "atomView";
    }

    @RequestMapping(value = "{owner}/{app}/saved/searches/{name}", method = RequestMethod.GET)
    public String getSearch(@PathVariable String owner, @PathVariable String app, @PathVariable String name, Model model) {
        System.out.println("get a saved search! " + name);

        SavedSearch savedSearch = this.savedMap.get(name);
        model.addAttribute("title", name);
        model.addAttribute("id", "/servicesNS/admin/search/saved/searches/" + name);
        model.addAttribute("content", savedSearch);

        return "atomView";
    }

    @RequestMapping(value = "{owner}/{app}/saved/searches", method = RequestMethod.POST)
    public String createSearch(@PathVariable String owner, @PathVariable String app, @RequestBody String body,Model model) {
        System.out.println("saved searches create !");

        try {
            Map<String, List<String>> params = MgtUtils.parsePostBody(body);
            System.out.println("Create Saved Search");
            System.out.println(params.size());
            SavedSearch saved = new SavedSearch();
            MgtUtils.fillValues(saved, params);
            String name = params.get("name").get(0);
            this.savedMap.put(name, saved);

            model.addAttribute("title", name);
            model.addAttribute("id", "http://localhost:8080/mgt/servicesNS/admin/search/saved/searches/" + name);
            model.addAttribute("content", saved);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return "atomView";
    }

    @RequestMapping(value = "{owner}/{app}/saved/searches", method = RequestMethod.GET)
    public String getSearches(@PathVariable String owner, @PathVariable String app,Model model) {
        System.out.println("get saved searches!");

        model.addAttribute("title", "savedSearches");
        model.addAttribute("id", "/servicesNS/admin/search/saved/searches");
        model.addAttribute("content", this.savedMap);

        return "atomView";
    }
}
