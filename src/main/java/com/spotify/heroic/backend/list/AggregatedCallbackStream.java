package com.spotify.heroic.backend.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.spotify.heroic.aggregator.Aggregator;
import com.spotify.heroic.async.Callback;
import com.spotify.heroic.async.CallbackStream;
import com.spotify.heroic.async.CancelReason;
import com.spotify.heroic.backend.BackendManager.DataPointGroup;
import com.spotify.heroic.backend.BackendManager.QueryMetricsResult;
import com.spotify.heroic.backend.RowStatistics;
import com.spotify.heroic.backend.model.FetchDataPoints;

@Slf4j
public class AggregatedCallbackStream implements
        Callback.StreamReducer<FetchDataPoints.Result, QueryMetricsResult> {
    private final Map<String, String> tags;
    private final Aggregator.Session session;

    AggregatedCallbackStream(Map<String, String> tags,
            Aggregator.Session session) {
        this.tags = tags;
        this.session = session;
    }

    @Override
    public void finish(CallbackStream<FetchDataPoints.Result> stream,
            Callback<FetchDataPoints.Result> callback,
            FetchDataPoints.Result result) throws Exception {
        session.stream(result.getDatapoints());
    }

    @Override
    public void error(CallbackStream<FetchDataPoints.Result> stream,
            Callback<FetchDataPoints.Result> callback, Throwable error)
            throws Exception {
        log.error("Result failed: " + error, error);
    }

    @Override
    public void cancel(CallbackStream<FetchDataPoints.Result> stream,
            Callback<FetchDataPoints.Result> callback, CancelReason reason)
            throws Exception {
        log.error("Result cancelled: " + reason);
    }

    @Override
    public QueryMetricsResult done(int successful, int failed, int cancelled)
            throws Exception {
        final Aggregator.Result result = session.result();
        final RowStatistics rowStatistics = new RowStatistics(successful,
                failed, cancelled);

        final List<DataPointGroup> groups = new ArrayList<DataPointGroup>();
        groups.add(new DataPointGroup(tags, result.getResult()));

        return new QueryMetricsResult(groups, result.getSampleSize(),
                result.getOutOfBounds(), rowStatistics);
    }
}