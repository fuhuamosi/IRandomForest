package Ensemble;

import java.util.*;


/**
 * Created by fuhua in UC on 2016/5/10.
 */
public class RandomForest {

    private List<CartTree> trees;

    private List<String> featureNames;

    private int treeNum;

    private int maxDepth;

    private int minSamplesSplit; //最小节点分割样本数

    private List<Double> featureImportances;

    private int maxFeature;

    public RandomForest(List<String> featureNames) {
        this(featureNames, 30, 20, 20, (int) Math.sqrt(featureNames.size()));
    }

    public RandomForest(List<String> featureNames, int treeNum,
                        int maxDepth, int minSamplesSplit, int maxFeature) {
        this.setFeatureNames(featureNames);
        this.setTreeNum(treeNum);
        this.setMaxDepth(maxDepth);
        this.setMinSamplesSplit(minSamplesSplit);
        this.setMaxFeature(maxFeature);
        this.setTrees(new ArrayList<CartTree>());
        this.setFeatureImportances(new ArrayList<>(Collections.nCopies(featureNames.size(), 0.0)));
    }

    public void fit(List<List<Double>> x, List<Integer> y) {
        List<Sample> samples = null;
        try {
            samples = packDataSet(x, y);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("x和y长度不一致或x和特征名长度不一致");
        }
        //以后可以拓展为并行化建树
        for (int i = 0; i < getTreeNum(); i++) {
            List<Sample> partSamples = bootstrap(samples);
            CartTree tree = new CartTree(partSamples, getMaxDepth(),
                    getMinSamplesSplit(), getMaxFeature());
            tree.buildTree();
            getTrees().add(tree);
            //计算特征权重
            calFeatureImportances(tree.getInfoGain());
        }
    }

    private void calFeatureImportances(List<Double> infoGain) {
        List<Double> importances = getFeatureImportances();
        for (int i = 0; i < importances.size(); i++) {
            importances.set(i, importances.get(i) + infoGain.get(i));
        }
    }

    private List<Sample> packDataSet(List<List<Double>> x, List<Integer> y) throws Exception {
        if (x.size() != y.size() || x.get(0).size() != getFeatureNames().size()) {
            throw new Exception();
        }
        List<Sample> samples = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            samples.add(new Sample(x.get(i), y.get(i)));
        }
        return samples;
    }

    // 自助采样法
    private List<Sample> bootstrap(List<Sample> samples) {
        List<Sample> partSample = new ArrayList<>();
        int sampleSize = samples.size();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < sampleSize; i++) {
            int sampleIndex = random.nextInt(sampleSize);
            partSample.add(samples.get(sampleIndex));
        }
        return partSample;
    }

    public List<Integer> predict(List<List<Double>> x) {
        List<Integer> predictions = new ArrayList<>();
        for (List<Double> aX : x) {
            Sample sample = new Sample(aX, -1);
            double posVote = 0, negVote = 0;
            for (int j = 0; j < getTreeNum(); j++) {
                CartTree tree = getTrees().get(j);
                List<Double> pre_y = tree.traverseTree(sample, tree.getRootNode());
                negVote += pre_y.get(0);
                posVote += pre_y.get(1);
            }
            int predict = (posVote >= 10.5 ? 1 : 0);
            predictions.add(predict);
        }
        return predictions;
    }

    //查看归一化后的特征权重
    public void view_weight() {
        double sum = 0;
        for (double weight : getFeatureImportances()) {
            sum += weight;
        }
        for (int i = 0; i < getFeatureImportances().size(); i++) {
            String name = getFeatureNames().get(i);
            Double weight = getFeatureImportances().get(i) / sum;
            System.out.println(name + ": " + weight.toString());
        }
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


    public List<Double> getFeatureImportances() {
        return featureImportances;
    }

    public void setFeatureImportances(List<Double> featureImportances) {
        this.featureImportances = featureImportances;
    }

    public int getMaxFeature() {
        return maxFeature;
    }

    public void setMaxFeature(int maxFeature) {
        this.maxFeature = maxFeature;
    }
}
