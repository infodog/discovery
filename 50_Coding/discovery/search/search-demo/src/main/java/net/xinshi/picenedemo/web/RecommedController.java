package net.xinshi.picenedemo.web;

import net.xinshi.discovery.search.client.reco.SuggestItem;
import net.xinshi.discovery.search.client.services.SearchServices;
import net.xinshi.picenedemo.product.ProductSearchArgs;
import net.xinshi.picenedemo.product.ProductSearchFields;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RecommedController {
	

    @Autowired
    private SearchServices ss;


    @RequestMapping("/autoSuggest")
	@ResponseBody
	public String autoSuggest(@RequestParam String term) {
		System.out.println(term);
		
		//StringBuilder buffer = new StringBuilder();
		
		JSONArray result = new JSONArray();

        ProductSearchArgs args =  new ProductSearchArgs();
        args.setComplete(term);
		
		try {
			List<SuggestItem> items = this.ss.autoComplete(args);

			for (SuggestItem suggestItem : items) {
				//buffer.append(suggestItem.getName().replace("@","")).append("@约").append(suggestItem.getNum()).append("个商品|\r\n");
				JSONObject obj = new JSONObject();
				obj.put("value", suggestItem.getName());
				obj.put("num", suggestItem.getNum());

				result.put(obj);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result.toString();
	}

    @RequestMapping("/recommend")
    @ResponseBody
    public String recommend() {
//        ProductSearchArgs args = new ProductSearchArgs();
//        args.setIds(new ArrayList<String>());
//        args.getIds().add("doc1");
//        args.getIds().add("doc4");
//        args.getFacetFields().add(ProductSearchFields.Keyword.USERS);
//
//        String pl = "";
//        try {
//            SearchResults result = this.ss.search(args);
//
//            List<NamePair> users = result.getFacets().get(ProductSearchFields.Keyword.USERS);
//            for (NamePair user : users) {
//                String p =  user.getName() + " : " + user.getValue();
//                System.out.println(p);
//                pl = pl + p + "\n";
//            }
//
//            args = new ProductSearchArgs();
//            args.setFilterFiled(ProductSearchFields.MultiValued.USERS);
//            args.setFilteringArgs(users);
//
//            result = this.ss.search(args);
//            for (String s : result.getLists()) {
//                pl = pl + s + "\n";
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String pl = "";
        ProductSearchArgs args = new ProductSearchArgs();
        List<String> ids = new ArrayList<String>();
        ids.add("doc1");
        ids.add("doc4");
        List<String> rec = this.ss.collaborateFilter(args,ids,ProductSearchFields.MultiValued.USERS);
        for (String s : rec) {
            pl = pl + s + " bingo \n";
        }

        return pl;
    }

}
