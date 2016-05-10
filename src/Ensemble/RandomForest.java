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
        this.setFeatureNames(featureNames);
        this.setTreeNum(treeNum);
        this.setTrees(new ArrayList<CartTree>());
    }

    public void fit(List<List<Double>> x, List<Integer> y) {
        List<Sample> samples = null;
        try {
            samples = packDataSet(x, y);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("x和y长度不一致");
        }
        //以后可以拓展为并行化建树
        for(int i = 0; i < getTreeNum(); i++){
            List<Sample> partSamples = bootstrap(samples);
            CartTree tree = new CartTree(partSamples, getMaxDepth(), getMinSamplesSplit());
            tree.buildTree();
            getTrees().add(tree);
        }
    }

    private List<Sample> packDataSet(List<List<Double>> x, List<Integer> y) throws Exception{
        if(x.size() != y.size()) {
            throw new Exception();
        }
        List<Sample> samples = new ArrayList<>();
        for(int i = 0; i < x.size(); i++){
            samples.add(new Sample(x.get(i), y.get(i)));
        }
        return samples;
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

    public List<Integer> predict(List<List<Double>> x) {
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

    public List<CartTree> getTrees() {
        return trees;
    }

    public void setTrees(List<CartTree> trees) {
        this.trees = trees;
    }
}
