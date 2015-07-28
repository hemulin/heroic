package com.spotify.heroic.aggregation.simple;

import java.util.Collection;
import java.util.function.DoubleBinaryOperator;

import com.google.common.collect.ImmutableList;
import com.spotify.heroic.aggregation.DoubleBucket;
import com.spotify.heroic.model.DataPoint;

public class MinBucketIntegrationTest extends ValueBucketIntegrationTest {
    public MinBucketIntegrationTest() {
        super(Double.POSITIVE_INFINITY, new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return Math.min(left, right);
            }
        });
    }

    @Override
    public Collection<DoubleBucket<DataPoint>> buckets() {
        return ImmutableList.<DoubleBucket<DataPoint>> of(new MinBucket(0l), new StripedMinBucket(0l));
    }
}