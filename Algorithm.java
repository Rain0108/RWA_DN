import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Algorithm {
    public static int MaxValue = 100000;
    //不能设置为Integer.MAX_VALUE，否则两个Integer.MAX_VALUE相加会溢出导致出现负权
    public DataSt findPath(int[][] graph, int src, int dst) {
        //给定一个箱子，一个连接需求，返回最短路径
        int[][] matrix = new int[graph.length][graph.length];
        for(int i=0;i<graph.length;i++){
            for(int j=0;j<graph.length;j++){
                matrix[i][j] = graph[i][j];
            }
        }
        return dijstra(matrix, src, dst);
    }

    public DataSt dijstra(int[][] matrix, int source, int dst) {
        //最短路径长度
        int[] shortest = new int[matrix.length];
        //判断该点的最短路径是否求出
        int[] visited = new int[matrix.length];
        //存储输出路径
        String[] path = new String[matrix.length];
        ArrayList<ArrayList<Integer>> paths = new ArrayList<>(matrix.length);

        //初始化输出路径
        for (int i = 0; i < matrix.length; i++) {
            path[i] = source + "-" + i;
        }

        //初始化源节点
        shortest[source] = 0;
        visited[source] = 1;

        for (int i = 1; i < matrix.length; i++) {
            int min = Integer.MAX_VALUE;
            int index = -1;

            for (int j = 0; j < matrix.length; j++) {
                //已经求出最短路径的节点不需要再加入计算并判断加入节点后是否存在更短路径
                if (visited[j] == 0 && matrix[source][j] < min) {
                    min = matrix[source][j];
                    index = j;
                }
            }

            //更新最短路径
            shortest[index] = min;
            visited[index] = 1;

            //更新从index跳到其它节点的较短路径
            for (int m = 0; m < matrix.length; m++) {
                if (visited[m] == 0 && matrix[source][index] + matrix[index][m] < matrix[source][m]) {
                    matrix[source][m] = matrix[source][index] + matrix[index][m];
                    path[m] = path[index] + "-" + m;
                }
            }

        }

        //打印最短路径
        if (dst != source) {
            if (shortest[dst] == MaxValue) {
                return null;
            } else {
                return new DataSt(path[dst], shortest[dst]);
            }
        }
        return null;
    }
    public void quickSort(ArrayList<LightPath> lightPaths, int[] arr, int low, int high){
        int i,j,temp,t;
        if(low>high){
            return;
        }
        i=low;
        j=high;
        //temp就是基准位
        temp = arr[low];

        while (i<j) {
            //先看右边，依次往左递减
            while (temp>=arr[j]&&i<j) {
                j--;
            }
            //再看左边，依次往右递增
            while (temp<=arr[i]&&i<j) {
                i++;
            }
            //如果满足条件则交换
            if (i<j) {
                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
                Collections.swap(lightPaths, i, j);
            }

        }
        //最后将基准为与i和j相等位置的数字交换
        arr[low] = arr[i];
        arr[i] = temp;
        Collections.swap(lightPaths, low, i);
        //递归调用左半数组
        quickSort(lightPaths, arr, low, j-1);
        //递归调用右半数组
        quickSort(lightPaths, arr, j+1, high);
    }
    public void assignLPtoBin(int[][] graph, int[][] overload, DataSt dataSt){
        //将给定的连接需求分配给当前箱子
        String[] paths = dataSt.path.split("-");
        for(int i=0;i<paths.length-1;i++){
            overload[Integer.parseInt(paths[i])][Integer.parseInt(paths[i+1])] += 1;
            graph[Integer.parseInt(paths[i])][Integer.parseInt(paths[i+1])] = MaxValue;
            overload[Integer.parseInt(paths[i+1])][Integer.parseInt(paths[i])] += 1;
            graph[Integer.parseInt(paths[i+1])][Integer.parseInt(paths[i])] = MaxValue;
        }
    }
    public int calculateOverload(int[][] overload, DataSt dataSt){
        int res = 0;
        String[] paths = dataSt.path.split("-");
        for(int i=0;i<paths.length-1;i++) {
            if (overload[Integer.parseInt(paths[i])][Integer.parseInt(paths[i + 1])] > 1) res++;
        }
        return res;
    }
    public LightPath lightPathClone(LightPath lightPath){
        return new LightPath(lightPath.ID, lightPath.src, lightPath.dst, lightPath.sPath, lightPath.minHop);
    }

    public double calculateSolutionFx(Solution solution) {
        //Fx为 1*惩罚值+0.5*跳数
        int totalOverload = 0;
        int totalHop = 0;
        for (int i = 0; i < solution.overload.length; i++) {
            for (int j = 0; j < solution.overload[i].length; j++) {
                for (int k = 0; k < solution.overload[i][j].length; k++) {
                    if (solution.overload[i][j][k] <= 1) continue;
                    totalOverload += (solution.overload[i][j][k] - 1);
                }
            }
        }
        for (int i = 0; i < solution.routes.size();i++){
            for(int j=0;j<solution.routes.get(i).size();j++){
                totalHop += (solution.routes.get(i).get(j).size() - 1);
            }
        }
        return totalOverload + 0.5 * totalHop;
    }

    public double calculateRouteFx(){

    }

}

class DataSt {
    String path;
    int minDis;
    public DataSt(String path, int minDis){
        this.path = path;
        this.minDis = minDis;
    }
}
