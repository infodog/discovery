<dataConfig>
    <script><![CDATA[
    function Price(row)    {
        var keys = ['metric_collectPrice', 'metric_notCollectPrice',
            'metric_logisticFee', 'metric_purchase-price', 'metric_product-totalPrice', 'metric_grossProfit', 'metric_total-price'];

        for (var i=0; i<keys.length; i++) {
            var key = keys[i];
            var price = row.get(key);
            if (price != 0) {
                row.put(key, (price / 100).toFixed(2));
            }
        }

        return row;
    }

    function Category(row)    {
        var c = row.get('category');
        if (c != null) {
            row.put('category_hierarchy', c);
            row.put('category_path', c);
        }
        return row;
    }



    ]]></script>

    <dataSource name="pigeon_log" type="BinFileDataSource" baseUrl="" encoding="UTF-8"/>
    <document>
        <entity name="pigeon" processor="PigeonEntityProcessor" transformer="script:Price,script:Category" url="" dataSource=""/>
    </document>
</dataConfig>
