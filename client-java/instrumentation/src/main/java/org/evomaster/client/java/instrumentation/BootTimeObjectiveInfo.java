package org.evomaster.client.java.instrumentation;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * contain targets info which are collected during SUT boot-time
 */
public class BootTimeObjectiveInfo implements Serializable {


    /**
     * Key descriptive id of the target,
     * note that here, we do not use unique integer id as the key
     * since the integer id might be changed in multiple search.
     *
     * Value heuristic [0,1], where 1 means covered.
     * Only the highest value found so far is kept.
     *
     *
     * TODO: for JVM, the target in static init seems to be skipped,
     * Need a further check whether it would have any side-effect
     */
    private final Map<String, Double> maxObjectiveCoverage =
            new ConcurrentHashMap<>(65536);

    /**
     * a list of external service which are initialized during SUT startup
     */
    private final List<ExternalServiceInfo> externalServiceInfo = new CopyOnWriteArrayList<>();

    public void reset(){
        maxObjectiveCoverage.clear();
        externalServiceInfo.clear();
    }


    public void registerExternalServiceInfoAtSutBootTime(ExternalServiceInfo info){
        if (externalServiceInfo.isEmpty() || externalServiceInfo.stream().noneMatch(s-> s.equals(info)))
            externalServiceInfo.add(info.copy());
    }

    public List<ExternalServiceInfo> getExternalServiceInfo(){
        // read-only
        return Collections.unmodifiableList(externalServiceInfo);
    }

    public void updateMaxObjectiveCoverage(String descriptiveId, double value){
        Double h = maxObjectiveCoverage.get(descriptiveId);
        if (h == null || value > h)
            maxObjectiveCoverage.put(descriptiveId, value);
    }

    public Map<String, Double> getObjectiveCoverageAtSutBootTime(){
        // read-only
        return Collections.unmodifiableMap(maxObjectiveCoverage);
    }

}
