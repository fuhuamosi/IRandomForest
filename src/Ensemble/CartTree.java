package Ensemble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fuhua in UC on 2016/5/10.
 */
public class CartTree {

    private TreeNode rootNode; //���ڵ�

    private List<Sample> samples; //�����õ�������

    private int maxDepth; //������

    private int minSamplesSplit; //��С�ڵ�ָ�������

    private int featureCnt; //��������

    private List<Double> infoGain; //ÿά��������Ϣ����(Ĭ��ʹ�û���ָ��)

    public CartTree(ArrayList<Sample> samples) {
        this(samples, 20, 2);
    }

    public CartTree(List<Sample> samples, int maxDepth, int minSamplesSplit) {
        this.setRootNode(new TreeNode());
        this.setSamples(samples);
        this.setMaxDepth(maxDepth);
        this.setMinSamplesSplit(minSamplesSplit);
        this.setFeatureCnt(this.getSamples().get(0).getFeatures().size());
        this.setInfoGain(new ArrayList<>(Collections.nCopies(this.getFeatureCnt(), 0.0)));
    }

    public void buildTree() {
        growTree(getRootNode(), getSamples(), generateRandomFeatureIndexes(),
                    0, 1);
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getFeatureCnt() {
        return featureCnt;
    }

    public void setFeatureCnt(int featureCnt) {
        this.featureCnt = featureCnt;
    }

    public List<Double> getInfoGain() {
        return infoGain;
    }

    public void setInfoGain(List<Double> infoGain) {
        this.infoGain = infoGain;
    }

    public int getMinSamplesSplit() {
        return minSamplesSplit;
    }

    public void setMinSamplesSplit(int minSamplesSplit) {
        this.minSamplesSplit = minSamplesSplit;
    }

    public List<Integer> predict(List<Sample> newSamples){
        List<Integer> predictions = new ArrayList<>();
        for(Sample sample : newSamples){
            int prediction = traverseTree(sample, getRootNode());
            predictions.add(prediction);
        }
        return predictions;
    }

    private int traverseTree(Sample sample, TreeNode node) {
        if(node.isLeaf()){
            return node.getLabel();
        }
        else {
            int featureIndex = node.getFeatureIndex();
            double threshold = node.getThreshold();
            if(sample.getFeatures().get(featureIndex) < threshold)
            {
                return traverseTree(sample, node.getLeftNode());
            }
            else {
                return traverseTree(sample, node.getRightNode());
            }
        }
    }

    // ��ĳ���ڵ���������
    private void growTree(TreeNode node, List<Sample> samples,
                          List<Integer> featureIndexes, int fatherLabel,
                          int depth) {
        int sampleSize = samples.size();
        // �ýڵ�û������ʱ��ʹ�ø��ڵ���������ı�ǩ��Ϊ�Լ���ǩ
        if (sampleSize == 0) {
            node.setIsLeaf(true);
            node.setLabel(fatherLabel);
        } else {
            List<Integer> counts = calSample(samples);
            int majorLabel = (counts.get(0) > counts.get(1)) ? 0 : 1;
            // ������������ͬһ����ǩ
            if (counts.get(0) == sampleSize || counts.get(1) == sampleSize) {
                node.setIsLeaf(true);
                node.setLabel(majorLabel);
                // ���������Ȼ�����С�ڵ�ָ���������ֹͣ�ָ�
            } else if (depth >= getMaxDepth() || sampleSize < getMinSamplesSplit()) {
                node.setIsLeaf(true);
                node.setLabel(majorLabel);
            } else {
                // �ָ�ڵ�
                splitNode(node, samples, featureIndexes, majorLabel, depth);
            }
        }
    }

    private void splitNode(TreeNode node, List<Sample> samples,
                           List<Integer> featureIndexes,
                           int majorLabel, int depth) {
        List<Object> bestList = chooseFeature(samples, featureIndexes);
        int bestFeatureIndex = (int) bestList.get(0);
        double bestThreshold = (double) bestList.get(1);
        @SuppressWarnings("unchecked")
        List<Sample> bestLeftSamples = (List<Sample>) bestList.get(2);
        @SuppressWarnings("unchecked")
        List<Sample> bestRightSamples = (List<Sample>) bestList.get(3);
        double bestGiniGain = (double) bestList.get(4);
        double newGiniGain = this.getInfoGain().get(bestFeatureIndex) + bestGiniGain;
        // ���������������Ϣ����
        this.getInfoGain().set(bestFeatureIndex, newGiniGain);
        node.setFeatureIndex(bestFeatureIndex);
        node.setThreshold(bestThreshold);
        node.setLeftNode(new TreeNode());
        node.setRightNode(new TreeNode());
        // ������������������
        growTree(node.getLeftNode(), bestLeftSamples, generateRandomFeatureIndexes(),
                majorLabel, depth + 1);
        growTree(node.getRightNode(), bestRightSamples, generateRandomFeatureIndexes(),
                majorLabel, depth + 1);
    }

    //ѡ������ʵķָ���������ֵ
    private List<Object> chooseFeature(List<Sample> samples, List<Integer> featureIndexes) {
        int sampleSize = samples.size();
        double oriGini = calGini(samples);
        double minGini = oriGini;
        int bestFeatureIndex = -1;
        double bestThreshold = -1;
        List<Sample> bestLeftSamples = null, bestRightSamples = null;
        for (Integer featureIndex : featureIndexes) {
            // ������ʱ����������
            for (Sample sample : samples) {
                sample.setSortFeature(sample.getFeatures().get(featureIndex));
            }
            // ��������������������
            Collections.sort(samples);
            for (int i = 0; i < sampleSize - 1; i++) {
                Sample leftSample = samples.get(i), rightSample = samples.get(i + 1);
                if (leftSample.getLabel() == rightSample.getLabel()) continue;
                List<Sample> leftSamples = samples.subList(0, i + 1);
                List<Sample> rightSamples = samples.subList(i + 1, sampleSize);
                double splitGini = 0.0;
                splitGini += (double) leftSamples.size() / sampleSize * calGini(leftSamples);
                splitGini += (double) rightSamples.size() / sampleSize * calGini(rightSamples);
                // �ָ�������GiniС�ڵ��ڵ�ǰ��СGini
                if (splitGini <= minGini) {
                    bestFeatureIndex = featureIndex;
                    bestThreshold = (leftSample.getFeatures().get(featureIndex) +
                            rightSample.getFeatures().get(featureIndex)) / 2.0;
                    bestLeftSamples = leftSamples;
                    bestRightSamples = rightSamples;
                    minGini = splitGini;
                }
            }
        }
        List<Object> bestList = new ArrayList<>();
        bestList.add(bestFeatureIndex);
        bestList.add(bestThreshold);
        bestList.add(bestLeftSamples);
        bestList.add(bestRightSamples);
        bestList.add(oriGini - minGini);
        return bestList;
    }

    // ����һ�������Ļ���ָ��
    private double calGini(List<Sample> samples) {
        double res = 0.0;
        int sampleSize = samples.size();
        List<Integer> counts = calSample(samples);
        res += (double) counts.get(0) / sampleSize;
        res += (double) counts.get(1) / sampleSize;
        res = 1 - res;
        return res;
    }

    // ���������������ִ���
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

    // ���ѡȡ����
    private List<Integer> generateRandomFeatureIndexes() {
        List<Integer> featureIndexes = new ArrayList<>();
        for (int i = 0; i < getFeatureCnt(); i++) {
            featureIndexes.add(i);
        }
        Collections.shuffle(featureIndexes);
        int num = (int) Math.sqrt(getFeatureCnt());
        return featureIndexes.subList(0, num);
    }

}
