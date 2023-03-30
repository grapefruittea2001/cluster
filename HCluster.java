import java.io.*;
import java.util.*;

//任务：
//3.读取数据时，区分到底是哪种类型的数据存入datapoint，修改维度为一个新类

public class HCluster
{
    public static void main(String[] args) throws IOException
    {
        HCluster hc = new HCluster();

        // 使用链表List存放样本点
        ArrayList<DataPoint> dp = new ArrayList<DataPoint>();

        // 读入样本文件
        int[] dimensions = {0,1,2,3};
        dp = hc.readData("D:/0yw_codes/graduation_code_test/data/iris.data",dimensions);

        /*
         * freq代表了聚类的终止条件，判断还有没有距离小于freq的两个类簇，若有则合并后继续迭代，否则终止迭代
         */

        double freq = 1.92;
        //进行聚类（指定哪维数据进行聚类）
        List<Cluster> clusters = hc.startCluster(dp, freq);

        //输出聚类的结果，两个类簇中间使用----隔开
        File f=new File("H_out.txt");
        FileOutputStream fos1=new FileOutputStream(f);
        OutputStreamWriter dos1=new OutputStreamWriter(fos1);

        System.out.println();
        System.out.println("结果输出---：");

        //结果输出：遍历所有的聚类，输出其中DataPoint的值
        int cluster_num = 0;
        for (Cluster cl : clusters)
        {
            cluster_num++;
            List<DataPoint> tempDps = cl.getDataPoints();
            for (DataPoint tempdp : tempDps)
            {
                System.out.println(tempdp.getDataPointName()+"  "+cluster_num);
                dos1.write(tempdp.getDataPointName()+"  "+cluster_num+"\n");
            }
            System.out.println();
            dos1.write("----\n");
        }
        dos1.close();
    }

    // 层次聚类的主方法***************
    //freq判断还有没有距离小于freq的两个类簇
    private List<Cluster> startCluster(ArrayList<DataPoint> dp, double freq)
    {
        // 声明cluster类，存放类名和类簇中含有的样本
        List<Cluster> finalClusters = new ArrayList<Cluster>();
        // 初始化类簇，开始时认为每一个样本都是一个类簇并将初始化类簇赋值给最终类簇
        List<Cluster> originalClusters = initialCluster(dp);
        finalClusters = originalClusters;
        // flag为判断标志
        boolean flag = true;
        int it = 1;
        while (flag)
        {
            System.out.println("第" + it + "次迭代");
            // 临时表量，存放类簇间 余弦相似度的最大值
            double max = -1;
            // mergeIndexA和mergeIndexB表示每一次迭代聚类最小的两个类簇，也就是每一次迭代要合并的两个类簇
            int mergeIndexA = 0;
            int mergeIndexB = 0;
            /*
             * 迭代开始，分别去计算每个类簇之间的距离，将距离小的类簇合并
             */
            for (int i = 0; i < finalClusters.size() - 1; i++)
            {
                for (int j = i + 1; j < finalClusters.size(); j++)
                {
                    // 得到任意的两个类簇
                    Cluster clusterA = finalClusters.get(i);
                    Cluster clusterB = finalClusters.get(j);
                    // 得到这两个类簇中的样本
                    List<DataPoint> dataPointsA = clusterA.getDataPoints();
                    List<DataPoint> dataPointsB = clusterB.getDataPoints();
                    /*
                     * 定义临时变量tempDis存储两个类簇的大小，这里采用的计算两个类簇的距离的方法是
                     * 得到两个类簇中所有的样本的距离的和除以两个类簇中的样本数量的积，计算两个样本之间的距离用的是余弦相似度。
                     */
                    double tempDis = 0;
                    /*
                     * 可以事先一次性将两两样本点之间的余弦距离计算好存放一个MAP中，
                     */
                    for (int m = 0; m < dataPointsA.size(); m++)
                    {
                        for (int n = 0; n < dataPointsB.size(); n++)
                        {
                            tempDis = tempDis + getDistance(dataPointsA.get(m), dataPointsB.get(n));
                        }
                    }
                    tempDis = tempDis / (dataPointsA.size() * dataPointsB.size());

                    //System.out.println("tempDis="+tempDis);//可修改变量
                    if (tempDis >= max)
                    {
                        max = tempDis;
                        mergeIndexA = i;
                        mergeIndexB = j;
                    }
                }
            }
            /*
             * 若是余弦相似度的最大值都小于给定的阈值， 那么当前的类簇停止合并
             * 当前的聚类可以作为结果，否则合并余弦相似度值最大的两个类簇，继续进行迭代
             * 可以设定别的聚类迭代的结束条件,比如已经聚类为k类时停止
             */

            /*自底向上聚类为k类后停止*/
            // if(finalClusters.size() <= 3)
            // {
            //     flag = false;
            // }
            // else
            // {
            //     finalClusters = mergeCluster(finalClusters, mergeIndexA, mergeIndexB);
            // }
            //System.out.println("max="+max);//可修改变量
            if (max < freq)
            {
                flag = false;
            }
            else
            {
               finalClusters = mergeCluster(finalClusters, mergeIndexA, mergeIndexB);
            }
            it++;
        }
        return finalClusters;
    }

    //合并
    private List<Cluster> mergeCluster(List<Cluster> finalClusters, int mergeIndexA, int mergeIndexB)
    {
        String str_dpB="",str_dpA="";
        if (mergeIndexA != mergeIndexB)
        {
            // 将cluster[mergeIndexB]中的DataPoint加入到 cluster[mergeIndexA]
            Cluster clusterA = finalClusters.get(mergeIndexA);
            Cluster clusterB = finalClusters.get(mergeIndexB);

            List<DataPoint> dpA = clusterA.getDataPoints();
            List<DataPoint> dpB = clusterB.getDataPoints();
            for(DataPoint dp1:dpA)
            {
                str_dpA+=dp1.getDimension()[0];
                str_dpA+=",";
            }
            for (DataPoint dp2:dpB)
            {
                str_dpB+=dp2.getDimension()[0];//得到样本点第一维名字
                str_dpB+=",";
            }
            System.out.println("合并样本点"+str_dpB+"到类别"+mergeIndexA+"(包含样本点:"+str_dpA+")");
            for (DataPoint dp : dpB)
            {
                DataPoint tempDp = new DataPoint();
                tempDp.setDataPointName(dp.getDataPointName());
                tempDp.setDimension(dp.getDimension());
                tempDp.setCluster(clusterA);
                dpA.add(tempDp);
            }
            clusterA.setClusterName(clusterA.getClusterName()+str_dpB);
            clusterA.setDataPoints(dpA);
            finalClusters.remove(mergeIndexB);
        }
        return finalClusters;
    }

    // 初始化类簇
    private List<Cluster> initialCluster(ArrayList<DataPoint> dpoints)
    {
        // 声明存放初始化类簇的链表
        List<Cluster> originalClusters = new ArrayList<Cluster>();
        for (int i = 0; i < dpoints.size(); i++)
        {
            // 得到每一个样本点
            DataPoint tempDataPoint = dpoints.get(i);
            // 声明一个临时的用于存放样本点的链表
            List<DataPoint> tempDataPoints = new ArrayList<DataPoint>();
            // 链表中加入刚才得到的样本点
            tempDataPoints.add(tempDataPoint);
            //设置tempDataPoints的迭代器
            Iterator<DataPoint> it = tempDataPoints.iterator();
            // 声明一个类簇，并且将给类簇设定名字、增加样本点
            Cluster tempCluster = new Cluster();
            //设置类簇的名字，输出格式：Cluster i ;FirstDimension=样本点第一维的数据
            String str = "";
            while (it.hasNext())
            {
                DataPoint tempdp=it.next();
                str+=tempdp.getDimension()[0];
                str+=",";
            }
            tempCluster.setClusterName("Cluster " + String.valueOf(i)+"  FirstDimension="+str);
            tempCluster.setDataPoints(tempDataPoints);
            // 将样本点的类簇设置为tempCluster
            tempDataPoint.setCluster(tempCluster);
            // 将新的类簇加入到初始化类簇链表中
            originalClusters.add(tempCluster);
        }
        return originalClusters;
    }
    /**
     * 计算两样本点间的距离:字符串使用余弦相似度
     * 将每个字符视为一个维度，字符串表示为一个向量，然后通过计算两个向量之间的余弦夹角来度量它们之间的相似度
    **/
    private double getDistance_cosine(DataPoint dataPoint, DataPoint dataPoint2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        String[] dimensions1 = dataPoint.dimension;
        String[] dimensions2 = dataPoint2.dimension;

        for (int i = 0; i < dimensions1.length; i++) {
            dotProduct += Double.parseDouble(dimensions1[i]) * Double.parseDouble(dimensions2[i]);
            magnitude1 += Math.pow(Double.parseDouble(dimensions1[i]), 2);
            magnitude2 += Math.pow(Double.parseDouble(dimensions2[i]), 2);
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0;
        } else {
            return dotProduct / (magnitude1 * magnitude2);
        }
    }


    /**
     * 计算两样本点间的距离:欧氏距离
    **/
    public double getDistance(DataPoint dataPoint, DataPoint dataPoint2) {
        double distance = 0;
        for (int i = 0; i < dataPoint.dimension.length; i++) {
            double diff = Double.parseDouble(dataPoint.dimension[i]) - Double.parseDouble(dataPoint2.dimension[i]);
            distance += diff * diff;
        }

        return Math.sqrt(distance);
    }

    private static ArrayList<DataPoint> readData(String filename, int[] dimensions) throws IOException {
        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            String[] dataTokens = new String[dimensions.length];

            for (int i = 0; i < dimensions.length; i++) {
                dataTokens[i] = tokens[dimensions[i]];
            }

            DataPoint dataPoint = new DataPoint();
            dataPoint.dataPointName = tokens[0];
            dataPoint.cluster = null;
            dataPoint.dimension = dataTokens;

            dataPoints.add(dataPoint);
        }
        System.out.println("加载数据完毕，数据大小为：" + dataPoints.size());

        reader.close();
        return dataPoints;
    }
}