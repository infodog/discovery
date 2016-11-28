package net.xinshi.picenedemo.product;

public class ProductSearchFields {

    public static final class ID {
        public static final String ID = "id";
    }

    public static final class Keyword {
        public static final String MERCHANTID = "merchantId";
        public static final String PRICE = "price_l";
        public static final String PROMOTTION = "promotion";
    }

    public static final class Text {
        public static final String NAME = "name_text";
        public static final String CONTENT = "content_text";
        public static final String EMAIL = "email_text";

    }

    public static final class HightLight {
        public static final String HNAME = "name_highlight";
    }

    public static final class MultiValued {
        public static final String COLUMNiD = "columnId_multiValued";
        public static final String PATH = "colomn_path";
        public static final String FACET_COLUMN = "column_facetColumn";
        public static final String USERS = "user_multiValued";
    }

    public static final class SpellCheck {
        public static final String SCNAME = "spellcheck";
    }
}
