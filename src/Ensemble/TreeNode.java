package Ensemble;

/**
 * Created by fuhua in UC on 2016/5/10.
 */
public class TreeNode {

    private int featureIndex; //�ڵ���������

    private TreeNode leftNode; //��ڵ�

    private TreeNode rightNode; //�ҽڵ�

    private int label; //ȡֵ0��1

    private boolean isLeaf; //�Ƿ�Ҷ�ӽڵ�

    private double threshold; //�������ֵ���ֵ

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
