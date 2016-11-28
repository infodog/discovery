package net.xinshi.picenedemo.web;

import net.xinshi.discovery.search.client.reco.SimilarItem;
import net.xinshi.discovery.search.client.services.*;
import net.xinshi.discovery.search.client.services.impl.JavaSearchClient;
import net.xinshi.discovery.search.client.util.MidThreadLocal;
import net.xinshi.discovery.search.client.util.NamePair;
import net.xinshi.picenedemo.product.ProductSearchArgs;
import net.xinshi.picenedemo.product.ProductSearchFields;
import net.xinshi.picenedemo.search.SearchItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    @Autowired
    private SearchServices ss;

    @RequestMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        ProductSearchArgs args = new ProductSearchArgs();
        //条件
        args.setName(keyword);
//        args.setTrack_keyword(keyword);
//        args.setTrack_category("category_7777");
//        args.setFromPath(0);
//        args.setFetchCount(10);
//        args.setColumnId("12454");
//        //args.setValueField(ProductSearchFields.Store.HNAME);

//        args.setHightlight_keyword(keyword);
//        args.setHightlight_field(ProductSearchFields.HightLight.HNAME);
//        //args.getFacetFields().add(ProductSearchFields.Keyword.PRICE);
//        //args.getFacetFields().add(ProductSearchFields.Keyword.MERCHANTID);
//        int h = 0;
//        args.getFacetFields().add(ProductSearchFields.MultiValued.FACET_COLUMN + (h++));
//        args.getFacetFields().add(ProductSearchFields.MultiValued.FACET_COLUMN + (h++));
//        args.getFacetFields().add(ProductSearchFields.MultiValued.FACET_COLUMN + (h++));
//        args.getFacetFields().add(ProductSearchFields.MultiValued.FACET_COLUMN + (h++));
//        args.getFacetFields().add(ProductSearchFields.MultiValued.PATH);


        //args.setId("http://www.gome.com.cn/ec/homeus/book.html");

        //SortField sf = new SortField(ProductSearchFields.ID.ID, SortField.Type.LONG, true);

//		args.setBeginPrice(new Long(0));

        try {
            long begin = System.currentTimeMillis();




//            JavaSearchClient
//                    javaSearchClient = (JavaSearchClient) this.ss;
            //javaSearchClient.setProjectName("100");


//            MidThreadLocal.set("m7777");

            SearchResults sr = ss.search(args);

            long end = System.currentTimeMillis();

            System.out.println((end - begin) + " ms");


            model.addAttribute("keyword", keyword);
            model.addAttribute("total", sr.getTotal());

            if (sr.getTotal() == 0) {
                args.setSpellcheck(keyword);
                args.setSpell_num(5);
                List<SimilarItem> spellcheck = this.ss.spellCheck(args);
                if (spellcheck != null && spellcheck.size() > 0) {
                    model.addAttribute("spellcheck", spellcheck.get(0));
                }
            }

            ProductSearchArgs autoargs = new ProductSearchArgs();
            autoargs.setAuto_suggest(keyword);
            autoargs.setAuto_suggest_num(5);



			List<String> suggestions = this.ss.autoSuggest(autoargs);



            model.addAttribute("suggestions", suggestions);

            int i = 0;
            List<SearchItem> items = new ArrayList<SearchItem>();
            for (String id : sr.getLists()) {
                SearchItem item = new SearchItem();
                item.setUrl(id);
                //item.setTitle(sr.getDocs().getJSONObject(i).optString("name"));
                try {
                    item.setTitle(sr.getHighlighting().getJSONObject(id).optJSONArray(args.getHightlight_field()).optString(0));
                } catch (Exception e) {
                    item.setTitle(sr.getDocs().getJSONObject(i).optString(ProductSearchFields.HightLight.HNAME));
                }

                items.add(item);
                i++;
            }
            model.addAttribute("items", items);


            List<NamePair> facets = sr.getFacets().get(ProductSearchFields.MultiValued.PATH);
            model.addAttribute("facets", facets);

            List<NamePair> merchantfacets = sr.getFacets().get(ProductSearchFields.MultiValued.FACET_COLUMN + 3);
            model.addAttribute("merchantFacets", merchantfacets);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "searchlist";
    }

    @RequestMapping("/stat")
    @ResponseBody
    public String stat(Model model) {
        ProductSearchArgs args = new ProductSearchArgs();
        //条件
//        args.setName(keyword);

        try {
            long begin = System.currentTimeMillis();


            //Sum
//            args.getSumFields().add(new SumArg(ProductSearchFields.Keyword.PROMOTTION,SumArg.DISTINCT_COUNT)); //指标
//            args.getSumFields().add(new SumArg(ProductSearchFields.Keyword.MERCHANTID,SumArg.DISTINCT_COUNT));


            //FacetSum
//            FacetSumArg fs = new FacetSumArg();
//            fs.setFacetField(ProductSearchFields.Keyword.MERCHANTID);//维度
//            fs.getSumFields().add(new SumArg(ProductSearchFields.ID.ID,SumArg.COUNT));  //指标
//            fs.setOffset(0);//分页 ，第几页
//            fs.setLimit(20);//分页  。每页多少条记录
//            fs.setSort(ProductSearchFields.ID.ID);//排序指标
//            args.getSumFacetFields().add(fs);

            //DQL
            args.setInsight_dql("field id,merchantId_term | stat count(id) as Count group by merchantId_term as Merchant");

            SearchResults results = ss.search(args);

            System.out.println();
            System.out.println(results.getResponse().toString());
            System.out.println();

            Collection<NamePair> sums = results.getSum();
            if (sums != null) {
                for(NamePair np : sums) {
                    System.out.println(np.getName() + " : " + np.getValue());
                }
            }

            Map<String,Collection<FacetSumRow>> sumFacets = results.getSumFacets();
            if (sumFacets != null) {
                for(Map.Entry<String,Collection<FacetSumRow>> entry : sumFacets.entrySet()) {
                    System.out.println(entry.getKey());
                    Collection<FacetSumRow> fsrs = entry.getValue();
                    for(FacetSumRow fsr : fsrs){
                        System.out.println(fsr.getName());
                        Collection<NamePair> nps = fsr.getPairs();
                        for(NamePair np : nps) {
                            System.out.println(np.getName() + " : " + np.getValue());
                        }
                    }
                }
            }


            long end = System.currentTimeMillis();

            System.out.println((end - begin) + " ms");

            return results.getInsight().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @RequestMapping("/keywordlist")
    public String keywordList(Model model) {
        ProductSearchArgs args = new ProductSearchArgs();

        args.setKeyword_stat_offset(0);
        args.setKeyword_stat_limit(20);

//        args.setKeyword_stat_search("i");

        SearchKeywordStat sr = ss.keywordStat(args);

        StringBuilder res = new StringBuilder();
        System.out.println(sr.getTotal());
        res.append(sr.getTotal());
        res.append("<p>");
        for (NamePair pair : sr.getKeywords()) {
            res.append(pair.getName() + " : " + pair.getValue());
            res.append("<p>");
            System.out.println(pair.getName() + " : " + pair.getValue());
        }


        SearchArgs catargs = new ProductSearchArgs();
        catargs.setKeyword_category("ipad");
        List<String> cat = ss.searchKeywordCategory(catargs);
        for (String s : cat) {
            System.out.println(s);
            res.append("<p>");
            res.append(s);
        }

        model.addAttribute("response", res.toString());

        return "keywordlist";
    }

}
