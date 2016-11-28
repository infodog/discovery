package org.apache.solr.handler.dataimport;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * A {@link Transformer} instance which can extract numbers out of strings. It uses
 * {@link java.text.NumberFormat} class to parse strings and supports
 * Number, Integer, Currency and Percent styles as supported by
 * {@link java.text.NumberFormat} with configurable locales.
 * </p>
 * <p/>
 * <p>
 * Refer to <a
 * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>
 * for more details.
 * </p>
 * <p/>
 * <b>This API is experimental and may change in the future.</b>
 *
 * @since solr 1.3
 */
public class NumberFormatTransformer extends Transformer {

    private static final Pattern localeRegex = Pattern.compile("^([a-z]{2})-([A-Z]{2})$");

    @Override
    @SuppressWarnings("unchecked")
    public Object transformRow(Map<String, Object> row, Context context) {
        for (Map<String, String> fld : context.getAllEntityFields()) {
            String style = context.replaceTokens(fld.get(FORMAT_STYLE));
            if (style != null) {
                String column = fld.get(DataImporter.COLUMN);
                String srcCol = fld.get(RegexTransformer.SRC_COL_NAME);
                Locale locale = null;
                String localeStr = context.replaceTokens(fld.get(LOCALE));
                if (srcCol == null)
                    srcCol = column;
                if (localeStr != null) {
                    Matcher matcher = localeRegex.matcher(localeStr);
                    if (matcher.find() && matcher.groupCount() == 2) {
                        locale = new Locale(matcher.group(1), matcher.group(2));
                    } else {
                        throw new DataImportHandlerException(DataImportHandlerException.SEVERE, "Invalid Locale specified for field: " + fld);
                    }
                } else {
                    locale = Locale.getDefault();
                }

                Object val = row.get(srcCol);
                String styleSmall = style.toLowerCase(Locale.ROOT);

                if (val instanceof List) {
                    List<String> inputs = (List) val;
                    List results = new ArrayList();
                    for (String input : inputs) {
                        try {
                            results.add(process(input, styleSmall, locale));
                        } catch (ParseException e) {
                            throw new DataImportHandlerException(
                                    DataImportHandlerException.SEVERE,
                                    "Failed to apply NumberFormat on column: " + column, e);
                        }
                    }
                    row.put(column, results);
                } else {
                    if (val == null || val.toString().trim().equals(""))
                        continue;
                    try {
                        row.put(column, process(val.toString(), styleSmall, locale));
                    } catch (ParseException e) {
                        throw new DataImportHandlerException(
                                DataImportHandlerException.SEVERE,
                                "Failed to apply NumberFormat on column: " + column, e);
                    }
                }
            }
        }
        return row;
    }

    private Number process(String val, String style, Locale locale) throws ParseException {
        if (INTEGER.equals(style)) {
            return parseNumber(val, NumberFormat.getIntegerInstance(locale));
        } else if (NUMBER.equals(style)) {
            return parseNumber(val, NumberFormat.getNumberInstance(locale));
        } else if (CURRENCY.equals(style)) {
            return parseNumber(val, NumberFormat.getCurrencyInstance(locale));
        } else if (PERCENT.equals(style)) {
            return parseNumber(val, NumberFormat.getPercentInstance(locale));
        }

        return null;
    }

    private Number parseNumber(String val, NumberFormat numFormat) throws ParseException {
        ParsePosition parsePos = new ParsePosition(0);
        Number num = numFormat.parse(val, parsePos);
        if (parsePos.getIndex() != val.length()) {
            throw new ParseException("illegal number format", parsePos.getIndex());
        }
        return num;
    }

    public static final String FORMAT_STYLE = "formatStyle";

    public static final String LOCALE = "locale";

    public static final String NUMBER = "number";

    public static final String PERCENT = "percent";

    public static final String INTEGER = "integer";

    public static final String CURRENCY = "currency";
}
