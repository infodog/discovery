package org.apache.solr.handler.dataimport;

/**
 * <p>
 * Pluggable functions for resolving variables
 * </p>
 * <p>
 * Implementations of this abstract class must provide a public no-arg constructor.
 * </p>
 * <p>
 * Refer to <a
 * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>
 * for more details.
 * </p>
 * <b>This API is experimental and may change in the future.</b>
 *
 * @since solr 1.3
 */
public abstract class Evaluator {

    /**
     * Return a String after processing an expression and a {@link VariableResolver}
     *
     * @param expression string to be evaluated
     * @param context    instance
     * @return the value of the given expression evaluated using the resolver
     * @see VariableResolver
     */
    public abstract String evaluate(String expression, Context context);
}
