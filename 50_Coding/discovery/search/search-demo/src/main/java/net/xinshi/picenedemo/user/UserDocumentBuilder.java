package net.xinshi.picenedemo.user;

import net.xinshi.discovery.search.client.services.DocumentBuilder;
import net.xinshi.discovery.search.client.services.SearchType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class UserDocumentBuilder implements DocumentBuilder {
    private List<User> users;

    public SearchType getSearchType() {
        return SearchTypes.USER;
    }

    public String getSearchTypeString() {
        return "USER";
    }

    public UserDocumentBuilder(List<User> users) {
        this.users = users;
    }

    public Collection<JSONObject> getDoc() throws Exception {

        int n = 0;
        List<JSONObject> docs = new ArrayList<JSONObject>();
        try {
            for (User user : this.users) {

                n++;

                JSONObject doc = new JSONObject();
                doc.put(UserSearchFields.ID.ID, user.getId());

                JSONArray purchases = new JSONArray();
                for (String purchase : user.getPurchases()) {
                    purchases.put(purchase);
                }
                doc.put(UserSearchFields.Keyword.PURCHASES, purchases);

                docs.add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docs;
    }

    public Collection<String> getKeys() throws Exception {
        Vector<String> keys = new Vector<String>();
        for (User user : this.users) {
            keys.add(user.getId());
        }
        return keys;
    }
}
