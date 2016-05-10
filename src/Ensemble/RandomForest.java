package Ensemble;

import java.util.*;


/**
 * Created by fuhua in UC on 2016/5/10.
 */
public class RandomForest {

    private List<CartTree> trees;

    private List<String> featureNames;

    private int treeNum = 30;

    private int maxDepth = 20;

    private int minSamplesSplit = 2; //最小节点分割样本数

    public RandomForest(List<String> featureNames, int treeNum)
    {
        this.featureNames = featureNames;
        this.treeNum = treeNum;
    }

    public void fit(List<List<Integer>> x, List<Integer> y) {

    }

    // 自助采样法
    private List<Sample> bootstrap(List<Sample> samples){
        Set<Sample> partSampleSet = new HashSet<>();
        int sampleSize = samples.size();
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < sampleSize; i++){
            int sampleIndex = random.nextInt(sampleSize);
            partSampleSet.add(samples.get(sampleIndex));
        }
        return new ArrayList<>(partSampleSet);
    }

    public List<Integer> predict(List<List<Integer>> x) {
        return null;
    }

    public int getTreeNum() {
        return treeNum;
    }

    public void setTreeNum(int treeNum) {
        this.treeNum = treeNum;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMinSamplesSplit() {
        return minSamplesSplit;
    }

    public void setMinSamplesSplit(int minSamplesSplit) {
        this.minSamplesSplit = minSamplesSplit;
    }

    public List<String> getFeatureNames() {
        return featureNames;
    }

    public void setFeatureNames(List<String> featureNames) {
        this.featureNames = featureNames;
    }
}
