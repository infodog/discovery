package net.xinshi.picenedemo.product;

import net.xinshi.discovery.search.client.services.DocumentBuilder;
import net.xinshi.discovery.search.client.services.SearchType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ProductDocumentBuilder implements DocumentBuilder {
    private List<Product> products;

    public SearchType getSearchType() {
        return SearchTypes.PRODUCT;
    }

    public String getSearchTypeString() {
        return "PRODUCT";
    }

    public ProductDocumentBuilder(List<Product> products) {
        this.products = products;
    }

    public List<JSONObject> getDoc() throws Exception {

        int n = 0;
        List<JSONObject> docs = new ArrayList<JSONObject>();

        try {
            for (Product product : this.products) {
                n++;
                JSONObject doc = new JSONObject();
                product.setId(product.getId().replace(":",""));
                doc.put(ProductSearchFields.ID.ID, product.getId());
                doc.put(ProductSearchFields.Text.NAME, product.getTitle());
                doc.put(ProductSearchFields.HightLight.HNAME, product.getTitle());
                doc.put(ProductSearchFields.SpellCheck.SCNAME, product.getTitle());
                doc.put(ProductSearchFields.Keyword.PRICE, n);
                doc.put(ProductSearchFields.Keyword.MERCHANTID, "m_" + (n + 1 + new Random().nextInt()));

                //multiValue
                JSONArray users = new JSONArray();
                doc.put(ProductSearchFields.MultiValued.USERS, users);

                //multiValue
                JSONArray paths = new JSONArray();
                paths.put("/12454/32232/12345/999");
                paths.put("/something/anything/else");

                doc.put(ProductSearchFields.MultiValued.PATH, paths);
                doc.put(ProductSearchFields.MultiValued.FACET_COLUMN, paths);

                //docs.add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docs;
    }

    public Collection<String> getKeys() throws Exception {
        Vector<String> keys = new Vector<String>();
        for (Product product : this.products) {
            keys.add(product.getId());
        }
        return keys;
    }
}
