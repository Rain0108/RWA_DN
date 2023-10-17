import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Solution implements Cloneable {
    Instance instance;
    Parameters parameters;
    Algorithm algorithm;
    GreedyInsert gInsert;
    int[] assignment;
    int[][][] overload;
    HashMap<Integer, ArrayList<ArrayList<Integer>>> routes;
    double totalFx;
    public Solution() {
        instance = new Instance("C:\\Users\\Rain\\Desktop\\DNILS-RWA-master\\Code\\deploy\\" +
                "Instance\\ATT.json");
        parameters = new Parameters();
        algorithm = new Algorithm();
        gInsert = new GreedyInsert(instance);
        assignment = new int[instance.num_of_lightPaths];
        Arrays.fill(assignment, -1);
        overload = new int[parameters.k][instance.num_of_node][instance.num_of_node];
        routes = new HashMap<>();
    }
    public void iniSolution(){
        //创建k个箱子
        int [][][] bins = new int[parameters.k][instance.num_of_node][instance.num_of_node];
        //overload为1时边上只有一条路径
        //int [][][] overload = new int[parameters.k][instance.num_of_node][instance.num_of_node];
        for(int i=0;i<parameters.k;i++){
            for(int j=0;j< instance.num_of_node;j++){
                for(int k=0;k<instance.num_of_node;k++){
                    bins[i][j][k] = instance.graph[j][k];
                }
            }
        }
        //所有连接需求按最短路径的跳数降序排列
        int[] hop_of_lp = new int[instance.num_of_lightPaths];
        for(int i=0;i<instance.num_of_lightPaths;i++){
            String s = algorithm.findPath(instance.graph, instance.lightPaths.get(i).src, instance.lightPaths.get(i).dst).path;
            String[] ss = s.split("-");
            hop_of_lp[i] = ss.length - 1;
            instance.lightPaths.get(i).sPath = s;
            instance.lightPaths.get(i).minHop = ss.length - 1;
        }
        algorithm.quickSort(instance.lightPaths, hop_of_lp, 0, hop_of_lp.length-1);
        //-------------------执行排序后连接需求顺序会乱，要按序号分配------------------------

        //对每个连接需求，找到“最适配”的箱子
        for(int l=0;l<instance.num_of_lightPaths;l++){
            int minHop = Integer.MAX_VALUE;
            int tempW = -1;
            DataSt tempDataSt = new DataSt(null, -1);
            for(int w=0;w<bins.length;w++){
                DataSt dataSt = algorithm.findPath(bins[w], instance.lightPaths.get(l).src, instance.lightPaths.get(l).dst);
                //如果需求放在这一箱子中可行则直接分配
                if(dataSt != null && (dataSt.path.split("-").length - 1 <
                        Math.max(instance.num_of_node, Math.sqrt(instance.num_of_edges)))){
                    int tempHop = dataSt.path.split("-").length - 1;
                    if(tempHop < minHop) {
                        tempW = w;
                        minHop = tempHop;
                        tempDataSt = dataSt;
                    }
                }
            }
            if(tempW != -1){
                algorithm.assignLPtoBin(bins[tempW], overload[tempW], tempDataSt);
                assignment[instance.lightPaths.get(l).ID] = tempW;
                String[] str = tempDataSt.path.split("-");
                ArrayList<Integer> curPath = new ArrayList<>();
                for(int i=0;i<str.length;i++){
                    curPath.add(Integer.parseInt(str[i]));
                }
                routes.get(tempW).add(curPath);
            }
            //如果需求在所有箱子中都不可行则根据惩罚值和跳数选出最好的，暂定目标函数为惩罚值+0.5*跳数
            else{
                /*
                DataSt dataSt = algorithm.findPath(instance.graph, instance.lightPaths.get(l).src, instance.lightPaths.get(l).dst);
                double tempFx = Double.MAX_VALUE;
                int tempBin = -1;
                for(int w=0;w<bins.length;w++){
                    double curFx = algorithm.calculateOverload(overload[w], dataSt) + 0.5 * (dataSt.path.split("-").length);
                    if(curFx < tempFx){
                        tempFx = curFx;
                        tempBin = w;
                    }
                }*/
                int tempBin = -1;
                double tempFx = Double.MAX_VALUE;
                for(int w=0;w<bins.length;w++){
                    double curFx = gInsert.greedyInsert(this, w, instance.lightPaths.get(l));
                    if(curFx < tempFx){
                        tempBin = w;
                        tempFx = curFx;
                    }
                }
                StringBuilder path = new StringBuilder();
                Collections.reverse(gInsert.route);
                for(Integer i : gInsert.route) {
                    path.append(i);
                    path.append("-");
                }
                int minDis = gInsert.route.size() - 1;
                algorithm.assignLPtoBin(bins[tempBin], overload[tempBin], new DataSt(path.substring(0, path.length()-1), minDis));
                assignment[instance.lightPaths.get(l).ID] = tempBin;
                routes.get(tempW).add(gInsert.route);
            }
        }
    }

    @Override
    public Solution clone() throws CloneNotSupportedException {
        return (Solution) super.clone();
    }
}
