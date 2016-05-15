package Ensemble;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fuhua in UC on 2016/5/12.
 */
public class TestRF {

    private static RandomForest rf;

    private static List<String> readNames(String filename) {
        List<String> allNames = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            String[] names = line.split(",");
            allNames = new ArrayList<>(Arrays.asList(names));
            allNames.remove(allNames.size() - 1);
            return allNames;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allNames;
    }

    private static List<List<String>> readContent(String filename) {
        List<List<String>> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] items = line.split(",");
                lines.add(new ArrayList<>(Arrays.asList(items)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void show_res(List<Integer> labels, List<Integer> pres, String name) {
        Integer tp = 0, tn = 0, fp = 0, fn = 0;
        for (int i = 0; i < labels.size(); i++) {
            if (pres.get(i) == 1 && labels.get(i) == 1) tp += 1;
            else if (pres.get(i) == 1 && labels.get(i) == 0) tn += 1;
            else if (pres.get(i) == 0 && labels.get(i) == 1) fp += 1;
            else if (pres.get(i) == 0 && labels.get(i) == 0) fn += 1;
        }
        double precision = (double) tp / (tp + tn);
        double recall = (double) tp / (tp + fp);
        double fScore = 2 * precision * recall / (precision + recall);
        System.out.println(name);
        System.out.println("tp: " + tp.toString() + " fp: " + fp.toString()
                + " tn: " + tn.toString() + " fn: " + fn.toString());
        System.out.println("准确率: " + Math.round(precision * 1000) / 1000.0);
        System.out.println("召回率: " + Math.round(recall * 1000) / 1000.0);
        System.out.println("f值: " + Math.round(fScore * 1000) / 1000.0);
    }

    private static void test(String fileHead, String dataFile, String dataName) {
        List<List<String>> test_data = readContent(fileHead + dataFile);
        List<List<Double>> test_x = new ArrayList<>();
        List<Integer> test_y = new ArrayList<>();
        for (List<String> td : test_data) {
            List<Double> rowX = new ArrayList<>();
            for (int i = 0; i < td.size() - 1; i++) {
                Double temp = new Double(td.get(i));
                rowX.add(temp);
            }
            test_x.add(rowX);
            test_y.add(new Integer(td.get(td.size() - 1)));
        }
        List<Integer> pre_y2 = rf.predict(test_x);
        show_res(test_y, pre_y2, dataName);
    }

    public static void main(String[] args) {
        String fileHead = System.getProperty("user.dir") +
                "/src/dataset";
        List<String> featureNames = readNames(fileHead + "/train_set.csv");
        rf = new RandomForest(featureNames);
        List<List<String>> train_data = readContent(fileHead + "/train_set.csv");
        List<List<Double>> train_x = new ArrayList<>();
        List<Integer> train_y = new ArrayList<>();
        for (List<String> td : train_data) {
            List<Double> rowX = new ArrayList<>();
            for (int i = 0; i < td.size() - 1; i++) {
                Double temp = new Double(td.get(i));
                rowX.add(temp);
            }
            train_x.add(rowX);
            train_y.add(new Integer(td.get(td.size() - 1)));
        }
        rf.fit(train_x, train_y);
        rf.view_weight();
        test(fileHead, "/validation_set.csv", "验证集");
        test(fileHead, "/test_set.csv", "测试集");
    }

}
