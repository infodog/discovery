package net.xinshi.discovery.search.mgt.mvc;

import com.google.common.base.Charsets;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 3/26/13
 * Time: 2:59 PM
 */
@Controller
@RequestMapping("servicesNS")
public class ViewStatesController {
    private String insightHome = "";
    private HierarchicalINIConfiguration iniConfObj = null;

    public ViewStatesController() {
        this.insightHome = System.getProperty("insight.home");
        System.out.println("Insight.Home : " + this.insightHome);

        String fileName = "viewstates";
        String path = this.insightHome + "/etc/apps/search/default/" + fileName + ".conf";
        System.out.println(path);
        File file = new File(path);
        if (file.exists()) {
            try {

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
                        Iterator it1 = sObj.getKeys();
                        while (it1.hasNext()) {
                            Object key = it1.next();
                            System.out.println(key + " : " + sObj.getString(key.toString()));
                        }
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

    @RequestMapping(value = "{owner}/{app}/data/ui/viewstates/{vsid}", method = RequestMethod.POST)
    public String createViewStates(@PathVariable String owner, @PathVariable String app, @PathVariable String vsid, @RequestBody String body, Model model) {
        System.out.println(vsid);
        System.out.println("ViewStates : " + body);
        return "atomView";
    }

    @RequestMapping(value = "{owner}/{app}/data/ui/viewstates/{vsid}", method = RequestMethod.GET)
    public String getViewStates(@PathVariable String owner, @PathVariable String app, @PathVariable String vsid, Model model) {
        System.out.println("I need ViewStates !!!!!");
        System.out.println(vsid);
        model.addAttribute("title", "viewstates");
        model.addAttribute("id", "/mgt/servicesNS/" + owner + "/" + app + "/ui/viewstates/" + vsid);

        if (this.iniConfObj.getSection(vsid) != null) {
            model.addAttribute("content", this.iniConfObj.getSection(vsid));
        } else {
            model.addAttribute("content", new HashMap());
        }

        return "atomView";
    }


}
