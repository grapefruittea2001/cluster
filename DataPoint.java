public class DataPoint
{
    String dataPointName; // 样本点名
    Cluster cluster; // 样本点所属类簇
    String[] dimension; // 样本点的维度

    public DataPoint()
    {
    }

    public DataPoint(String[] dimension,String dataPointName)
    {
        this.dataPointName=dataPointName;
        this.dimension =dimension;
    }

    public String[] getDimension()
    {
        return dimension;
    }

    public void setDimension(String[] dimension)
    {
        this.dimension = dimension;
    }

    public Cluster getCluster()
    {
        return cluster;
    }

    public void setCluster(Cluster cluster)
    {
        this.cluster = cluster;
    }

    public String getDataPointName()
    {
        return dataPointName;
    }

    public void setDataPointName(String dataPointName)
    {
        this.dataPointName = dataPointName;
    }
}