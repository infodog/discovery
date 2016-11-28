package net.xinshi.discovery.search.mgt.mvc;

import org.apache.solr.services.ConfigSaver;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 8/17/12
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */


@Controller
public class DomainController {

    private ConfigSaver configSaver = new ConfigSaver();
    private ObjectMapper objectMapper = new ObjectMapper();


    @RequestMapping(value = "/domains", method= RequestMethod.GET)
    @ResponseBody
    public String domains() {
        System.out.println("get domains");
        return this.configSaver.get();
    }

    @RequestMapping(value = "/domains", method = RequestMethod.POST)
    @ResponseBody
    public String createDomain(@RequestBody String domain) {
        System.out.println("create a domain");
        System.out.println(domain);
        try {
            JsonNode item = objectMapper.readTree(domain);
            String id = item.get("id").getTextValue();
            System.out.println("id : " + id);
            this.configSaver.save(id,domain);
            return domain;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    @RequestMapping(value = "/domains/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public String updateDomain(@RequestBody String domain, @PathVariable String id) {
        System.out.println("update a domain");
        System.out.println(id);
        System.out.println("domain = " + domain);
        this.configSaver.save(id,domain);
        return domain;
    }

    @RequestMapping(value = "/domains/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String destroyDomain(@PathVariable String id) {
        System.out.println("destroy a domain");
        System.out.println("id = " + id);
        this.configSaver.delete(id);
        return id;
    }
}
