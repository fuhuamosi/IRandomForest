package Ensemble;

import java.util.List;

/**
 * Created by fuhua in UC on 2016/5/10.
 */
 class TreeNode {

    private int featureIndex; //节点特征索引

    private TreeNode leftNode; //左节点

    private TreeNode rightNode; //右节点

    private int label; //取值0或1

    private boolean isLeaf; //是否叶子节点

    private double threshold; //特征划分的阈值

    private List<Double> votes; //0和1的投票

     TreeNode() {
        this.setIsLeaf(false);
    }

     TreeNode getLeftNode() {
        return leftNode;
    }

     void setLeftNode(TreeNode leftNode) {
        this.leftNode = leftNode;
    }

     TreeNode getRightNode() {
        return rightNode;
    }

     void setRightNode(TreeNode rightNode) {
        this.rightNode = rightNode;
    }

     int getLabel() {
        return label;
    }

     void setLabel(int label) {
        this.label = label;
    }

     double getThreshold() {
        return threshold;
    }

     void setThreshold(double threshold) {
        this.threshold = threshold;
    }

     boolean isLeaf() {
        return isLeaf;
    }

     void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

     int getFeatureIndex() {
        return featureIndex;
    }

     void setFeatureIndex(int featureIndex) {
        this.featureIndex = featureIndex;
    }


     List<Double> getVotes() {
        return votes;
    }

     void setVotes(List<Double> votes) {
        this.votes = votes;
    }
}
