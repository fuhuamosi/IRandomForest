package Ensemble;

import java.util.*;

/**
 * Created by fuhua in UC on 2016/5/10.
 */
class CartTree {

    private TreeNode rootNode; //根节点

    private List<Sample> samples; //采样得到的样本

    private int maxDepth; //最大深度

    private int minSamplesSplit; //最小节点分割样本数

    private int featureCnt; //特征总数

    private int maxFeature; //最多使用的特征数

    private List<Double> infoGain; //每维特征的信息增益(默认使用基尼指数)


    CartTree(List<Sample> samples, int maxDepth,
             int minSamplesSplit, int maxFeature) {
        this.setRootNode(new TreeNode());
        this.setSamples(samples);
        this.setMaxDepth(maxDepth);
        this.setMinSamplesSplit(minSamplesSplit);
        this.setMaxFeature(maxFeature);
        this.setFeatureCnt(this.getSamples().get(0).getFeatures().size());
        this.setInfoGain(new ArrayList<>(Collections.nCopies(this.getFeatureCnt(), 0.0)));
    }

    void buildTree() {
        growTree(getRootNode(), getSamples(), generateRandomFeatureIndexes(),
                1);
    }

    TreeNode getRootNode() {
        return rootNode;
    }

    private void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    private List<Sample> getSamples() {
        return samples;
    }

    private void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    private int getMaxDepth() {
        return maxDepth;
    }

    private void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private int getFeatureCnt() {
        return featureCnt;
    }

    private void setFeatureCnt(int featureCnt) {
        this.featureCnt = featureCnt;
    }

    List<Double> getInfoGain() {
        return infoGain;
    }

    private void setInfoGain(List<Double> infoGain) {
        this.infoGain = infoGain;
    }

    private int getMinSamplesSplit() {
        return minSamplesSplit;
    }

    private void setMinSamplesSplit(int minSamplesSplit) {
        this.minSamplesSplit = minSamplesSplit;
    }

    //遍历树得到预测结果
    List<Double> traverseTree(Sample sample, TreeNode node) {
        if (node.isLeaf()) {
            return node.getVotes();
        } else {
            int featureIndex = node.getFeatureIndex();
            double threshold = node.getThreshold();
            if (sample.getFeatures().get(featureIndex) <= threshold) {
                return traverseTree(sample, node.getLeftNode());
            } else {
                return traverseTree(sample, node.getRightNode());
            }
        }
    }

    // 在某个节点上生长树
    private void growTree(TreeNode node, List<Sample> samples,
                          List<Integer> featureIndexes,
                          int depth) {
        int sampleSize = samples.size();
        List<Integer> counts = calSample(samples);
        int majorLabel = (counts.get(0) > counts.get(1)) ? 0 : 1;
        List<Double> votes = new ArrayList<>();
        votes.add(0.0);
        votes.add(0.0);
        // 所有样本都是同一个标签
        if (counts.get(0) == sampleSize || counts.get(1) == sampleSize) {
            node.setIsLeaf(true);
            node.setLabel(majorLabel);
        }
        // 到达最大深度或者最小节点分割样本数，停止分割
        else if (depth >= getMaxDepth() || sampleSize < getMinSamplesSplit()) {
            node.setIsLeaf(true);
            node.setLabel(majorLabel);
        } else {
            // 分割节点
            splitNode(node, samples, featureIndexes, majorLabel, depth);
        }
        //投票
        if (node.isLeaf()) {
            double majorProb = (double) counts.get(majorLabel) / sampleSize;
            if(majorLabel == 1)
                votes.set(majorLabel, majorProb);
            else
                votes.set(majorLabel, majorProb);
            node.setVotes(votes);
        }
    }

    private void splitNode(TreeNode node, List<Sample> samples,
                           List<Integer> featureIndexes,
                           int majorLabel, int depth) {
        List<Object> bestList = chooseFeature(samples, featureIndexes);
        int bestFeatureIndex = (int) bestList.get(0);
        // 分割失败
        if (bestFeatureIndex < 0) {
            node.setIsLeaf(true);
            node.setLabel(majorLabel);
            return;
        }
        double bestThreshold = (double) bestList.get(1);
        @SuppressWarnings("unchecked")
        List<Sample> bestLeftSamples = (List<Sample>) bestList.get(2);
        @SuppressWarnings("unchecked")
        List<Sample> bestRightSamples = (List<Sample>) bestList.get(3);
        double bestGiniGain = (double) bestList.get(4);
        double newGiniGain = this.getInfoGain().get(bestFeatureIndex) + bestGiniGain;
        // 保存最好特征的信息增益
        this.getInfoGain().set(bestFeatureIndex, newGiniGain);
        node.setFeatureIndex(bestFeatureIndex);
        node.setThreshold(bestThreshold);
        node.setLeftNode(new TreeNode());
        node.setRightNode(new TreeNode());
        // 生长左子树和右子树
        growTree(node.getLeftNode(), bestLeftSamples, generateRandomFeatureIndexes(),
                depth + 1);
        growTree(node.getRightNode(), bestRightSamples, generateRandomFeatureIndexes(),
                depth + 1);
    }

    //选择最合适的分割特征和阈值
    private List<Object> chooseFeature(List<Sample> samples, List<Integer> featureIndexes) {
        int sampleSize = samples.size();
        double oriGini = calGini(samples);
        double minGini = oriGini;
        int bestFeatureIndex = -1;
        double bestThreshold = -1;
        List<Sample> bestLeftSamples = null, bestRightSamples = null;
        double bestLeftGini = 0, bestRightGini = 0;
        for (Integer featureIndex : featureIndexes) {
            // 设置临时的排序特征
            for (Sample sample : samples) {
                sample.setSortFeature(sample.getFeatures().get(featureIndex));
            }
            // 将样本按特征正向排序
            Collections.sort(samples);
            for (int i = 0; i < sampleSize - 1; i++) {
                Sample leftSample = samples.get(i), rightSample = samples.get(i + 1);
                Double leftFeature = leftSample.getFeatures().get(featureIndex);
                Double rightFeature = rightSample.getFeatures().get(featureIndex);
                // 标签相同或特征相同则不分割
                if (leftSample.getLabel() == rightSample.getLabel()
                        || leftFeature.equals(rightFeature)) {
                    continue;
                }
                List<Sample> leftSamples = samples.subList(0, i + 1);
                List<Sample> rightSamples = samples.subList(i + 1, sampleSize);
                Double splitGini = 0.0;
                Double leftGini = calGini(leftSamples), rightGini = calGini(rightSamples);
                splitGini += (double) leftSamples.size() / sampleSize * leftGini;
                splitGini += (double) rightSamples.size() / sampleSize * rightGini;
                // 分割样本的Gini小于当前最小Gini
                if (splitGini < minGini) {
                    bestFeatureIndex = featureIndex;
                    bestThreshold = (leftFeature + rightFeature) / 2.0;
                    bestLeftSamples = leftSamples;
                    bestRightSamples = rightSamples;
                    minGini = splitGini;
                    bestLeftGini = leftGini;
                    bestRightGini = rightGini;
                }
            }
        }

        List<Object> bestList = new ArrayList<>();
        bestList.add(bestFeatureIndex);
        bestList.add(bestThreshold);
        bestList.add(bestLeftSamples);
        bestList.add(bestRightSamples);
        double sonGini = 0, fatherGini = oriGini * sampleSize;
        if (bestLeftSamples != null) sonGini += bestLeftGini * bestLeftSamples.size();
        if (bestRightSamples != null) sonGini += bestRightGini * bestRightSamples.size();
        bestList.add(fatherGini - sonGini);
        return bestList;
    }

    // 计算一份样本的基尼指数
    private double calGini(List<Sample> samples) {
        double res = 0.0;
        if (samples == null) return 0;
        int sampleSize = samples.size();
        List<Integer> counts = calSample(samples);
        res += Math.pow((double) counts.get(0) / sampleSize, 2);
        res += Math.pow((double) counts.get(1) / sampleSize, 2);
        res = 1 - res;
        return res;
    }

    // 计算正负样本出现次数
    private List<Integer> calSample(List<Sample> samples) {
        int positiveCnt = 0, negativeCnt = 0;
        for (Sample sample : samples) {
            if (sample.getLabel() == 1) {
                positiveCnt += 1;
            } else if (sample.getLabel() == 0) {
                negativeCnt += 1;
            }
        }
        List<Integer> counts = new ArrayList<>();
        counts.add(negativeCnt);
        counts.add(positiveCnt);
        return counts;
    }

    // 随机选取特征
    private List<Integer> generateRandomFeatureIndexes() {
        List<Integer> featureIndexes = new ArrayList<>();
        for (int i = 0; i < getFeatureCnt(); i++) {
            featureIndexes.add(i);
        }
        Collections.shuffle(featureIndexes);
        return featureIndexes.subList(0, getMaxFeature());
    }

    private int getMaxFeature() {
        return maxFeature;
    }

    private void setMaxFeature(int maxFeature) {
        this.maxFeature = maxFeature;
    }
}
