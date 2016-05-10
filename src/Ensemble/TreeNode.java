package Ensemble;

/**
 * Created by fuhua in UC on 2016/5/10.
 */
public class TreeNode {

    private int featureIndex; //节点特征索引

    private TreeNode leftNode; //左节点

    private TreeNode rightNode; //右节点

    private int label; //取值0或1

    private boolean isLeaf; //是否叶子节点

    private double threshold; //特征划分的阈值

    public TreeNode() {
        this.setIsLeaf(false);
    }

    public TreeNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(TreeNode leftNode) {
        this.leftNode = leftNode;
    }

    public TreeNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(TreeNode rightNode) {
        this.rightNode = rightNode;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getFeatureIndex() {
        return featureIndex;
    }

    public void setFeatureIndex(int featureIndex) {
        this.featureIndex = featureIndex;
    }
}
