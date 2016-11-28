package org.apache.solr.services;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 8/25/12
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */

public class ConfigSaver {
    //    public static final String DEFAULT_CONFIGS = "configs";
    public static final String DISCOVERY_WORK = "DiscoveryWork";
    public static final String SEARCH_CONFIG = "SearchConfig";
    public static final String DEFAULT_USER_NAME = "saas";
    public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

    public boolean save(String id, String config) {
        File file = new File(this.getFileName(id));

        BufferedWriter fw = null;
    try {

            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            fw.write(config);
            fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            this.generateSolrConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String get() {
        File file = new File(getPath());
        StringWriter sw = new StringWriter();

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode domains = mapper.createArrayNode();

        if (file != null && file.listFiles().length > 0) {
            for (File item : file.listFiles()) {
                if (item.isFile() && !item.isHidden()) {
                    System.out.println(item.getAbsolutePath());
                    JsonNode jn = null;
                    try {
                        jn = mapper.readTree(item);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    domains.add(jn);
                }

            }
        }
        try {
            mapper.writeValue(sw, domains);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sw.toString();

    }

    public boolean delete(String id) {
        File file = new File(this.getFileName(id));

        boolean flag = file.delete();
        try {
            this.generateSolrConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return flag;
    }

    private static String getUserName() {
        String name = "";
//
//        try {
//            final Authentication authentication = SecurityContextHolder.
//                    getContext().getAuthentication();
//            System.out.println(authentication.getName());
//            name = authentication.getName();
//        } catch (Exception e) {
//            //e.printStackTrace();
//            System.out.println("cannot get user name!");
//        }

        name = (name == null || name.equals("")) ? DEFAULT_USER_NAME : name;

        return name;
    }

    private String getFileName(String id) {
        return this.getPath() + File.separator + id;
    }

    public String getPath() {

        String home = "";
        String prop = "discovery.home";
        home = System.getProperty(prop);


        if (home == null) {
            home = "discovery" + File.separator + ConfigSaver.SEARCH_CONFIG;
        } else {
            StringBuilder path = new StringBuilder();
            path.append(home);
            if (!home.endsWith(File.separator)) {
                path.append(File.separator);
            }
            path.append(DISCOVERY_WORK);
            path.append(File.separator);
            path.append(SEARCH_CONFIG);
            path.append(File.separator);
            path.append(getUserName());
            home = path.toString();
        }

        System.out.println("Search Config Path: " + home);

        File file = new File(home);
        if (!file.exists()) {
            file.mkdirs();
        }

        return home;
    }

    private String getSolrPath() {
        StringBuilder path = new StringBuilder();
        path.append(this.getPath());
        path.append(File.separator);
        path.append("solr");

        File file = new File(path.toString());

        if (!file.exists()) {
            file.mkdirs();
        }

        return path.toString();
    }


    private void generateSolrConfig() throws IOException {
        Configuration cfg = new Configuration();

        cfg.setDefaultEncoding(CHARSET_UTF_8.toString());
        cfg.setOutputEncoding(CHARSET_UTF_8.toString());

        cfg.setObjectWrapper(new DefaultObjectWrapper());
        URL url = this.getClass().getClassLoader().getResource("solr.ftl");

        File tf = new File(url.getFile());

        System.out.println(tf.getParent());

        cfg.setDirectoryForTemplateLoading(tf.getParentFile());

        ObjectMapper mapper = new ObjectMapper();
        Map model = null;
        //FileWriter fw = null;
        FileOutputStream fos = null;
        OutputStreamWriter os = null;
        Template temp = null;

        File file = new File(getPath());
        List<String> domains = new ArrayList<String>();

        if (file != null && file.listFiles().length > 0) {
            for (File item : file.listFiles()) {
                if (item.isFile() && !item.isHidden()) {
                    System.out.println(item.getAbsolutePath());

                    try {
                        model = mapper.readValue(item, Map.class);
                        String name = (String) model.get("name");

                        if (name == null || "".equals(name)) {
                            continue;
                        }

                        domains.add(name);
                        String corePath = this.getSolrPath() + File.separator + name + File.separator + "conf";
                        File core = new File(corePath);
                        if (!core.exists()) {
                            core.mkdirs();
                        }
                        try {
                            fos = new FileOutputStream(corePath + File.separator + "solrconfig.xml");
                            os = new OutputStreamWriter(fos, CHARSET_UTF_8);
                            temp = cfg.getTemplate(File.separator + "core" + File.separator + "solrconfig.ftl");
                            temp.process(model, os);
                        } finally {
                            os.close();
                            fos.close();
                        }

                        try {
                            fos = new FileOutputStream(corePath + File.separator + "schema.xml");
                            os = new OutputStreamWriter(fos, CHARSET_UTF_8);
                            Template schema = cfg.getTemplate(File.separator + "core" + File.separator + "schema.ftl");
                            schema.process(model, os);
                        } finally {
                            os.close();
                            fos.close();
                        }

                        try {
                            fos = new FileOutputStream(corePath + File.separator + "protwords.txt");
                            os = new OutputStreamWriter(fos, CHARSET_UTF_8);
                            temp = cfg.getTemplate(File.separator + "core" + File.separator + "protwords.ftl");
                            temp.process(model, os);
                        } finally {
                            os.close();
                            fos.close();
                        }

                        try {
                            fos = new FileOutputStream(corePath + File.separator + "spellings.txt");
                            os = new OutputStreamWriter(fos, CHARSET_UTF_8);
                            temp = cfg.getTemplate(File.separator + "core" + File.separator + "spellings.ftl");
                            temp.process(model, os);
                        } finally {
                            os.close();
                            fos.close();
                        }

                        try {
                            fos = new FileOutputStream(corePath + File.separator + "stopwords.txt");
                            os = new OutputStreamWriter(fos, CHARSET_UTF_8);
                            temp = cfg.getTemplate(File.separator + "core" + File.separator + "stopwords.ftl");
                            temp.process(model, os);
                        } finally {
                            os.close();
                            fos.close();
                        }


                        try {
                            fos = new FileOutputStream(corePath + File.separator + "synonyms.txt");
                            os = new OutputStreamWriter(fos, CHARSET_UTF_8);
                            temp = cfg.getTemplate(File.separator + "core" + File.separator + "synonyms.ftl");
                            temp.process(model, os);
                        } finally {
                            os.close();
                            fos.close();
                        }

//                        try {
//                            fos = new FileOutputStream(corePath + File.separator + "elevate.xml");
//                            os = new OutputStreamWriter(fos, CHARSET_UTF_8);
//                            temp = cfg.getTemplate(File.separator + "core" + File.separator + "elevate.ftl");
//                            temp.process(model, os);
//                        } finally {
//                            os.close();
//                            fos.close();
//                        }

                        try {
                            fos = new FileOutputStream(corePath + File.separator + "data-config.xml");
                            os = new OutputStreamWriter(fos, CHARSET_UTF_8);
                            temp = cfg.getTemplate(File.separator + "core" + File.separator + "data-config.ftl");
                            temp.process(model, os);
                        } finally {
                            os.close();
                            fos.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            model = new HashMap<String, List<String>>();
            model.put("domains", domains);
            fos = new FileOutputStream(this.getSolrPath() + File.separator + "solr.xml");
            os = new OutputStreamWriter(fos, CHARSET_UTF_8);
            temp = cfg.getTemplate("solr.ftl");
            try {
                temp.process(model, os);
            } catch (TemplateException e) {
                e.printStackTrace();
            } finally {
                os.close();
                fos.close();
            }
        }

    }


    public String getConfigTemplate(String name) {
        StringBuilder config = new StringBuilder();
        config.append("{\"name\":\"");
        config.append(name);
        config.append("\",\"id\":\"");
        config.append(name);
        config.append("\",\"fields\":[]}");

        return config.toString();
    }
}
