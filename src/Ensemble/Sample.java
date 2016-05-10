package Ensemble;

import java.util.List;

/**
 * Created by fuhua in UC on 2016/5/10.
 */

//每行数据看成一个样本
public class Sample implements Comparable {

    private List<Double> features;

    private int label;

    private Double sortFeature; //用于分割节点时排序的临时特征

    public Sample(List<Double> features, int label){
        this.setFeatures(features);
        this.setLabel(label);
    }

    public List<Double> getFeatures() {
        return features;
    }

    public void setFeatures(List<Double> features) {
        this.features = features;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    @Override
    public int compareTo(Object o) {
        Sample secondSample = (Sample) o;
        double secondFeature = secondSample.getSortFeature();
        return this.getSortFeature().compareTo(secondFeature);
    }

    public void setSortFeature(Double sortFeature) {
        this.sortFeature = sortFeature;
    }

    public Double getSortFeature() {
        return sortFeature;
    }
}
