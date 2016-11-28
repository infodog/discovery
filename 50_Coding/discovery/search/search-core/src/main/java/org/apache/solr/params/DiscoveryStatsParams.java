/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.params;

/**
 * Stats Parameters
 */
@Deprecated
public interface DiscoveryStatsParams {
    public static final String STATS = "discovery.stats";
    public static final String STATS_FIELD = STATS + ".field";
    public static final String STATS_FACET = STATS + ".facet";
    public static final String STATS_FUNCTION = STATS + ".func";
    public static final String STATS_MIN = STATS + ".min";
    public static final String STATS_MAX = STATS + ".max";
    public static final String STATS_OFFSET = STATS + ".offset";
    public static final String STATS_LIMIT = STATS + ".limit";
    public static final String STATS_SORT = STATS + ".sort";
    public static final String STATS_ORDER_FIELDS = STATS + ".order.fields";

    //Range
    public static final String STATS_FACET_RANGE = STATS_FACET + ".range";
    public static final String STATS_FACET_RANGE_START = STATS_FACET_RANGE + ".start";
    public static final String STATS_FACET_RANGE_END = STATS_FACET_RANGE + ".end";
    public static final String STATS_FACET_RANGE_GAP = STATS_FACET_RANGE + ".gap";
}
