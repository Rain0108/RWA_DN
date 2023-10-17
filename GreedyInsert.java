import java.util.*;

public class GreedyInsert {
    Instance instance;
    ArrayList<Integer> route;
    public GreedyInsert(Instance instance){
        this.instance = instance;
        route = new ArrayList<>();
    }
    //邻域搜索的算子
    public double greedyInsert(Solution solution, int bin, LightPath lightPath){
        route.clear();
        //首先把图中节点分层，0层只包含需求的终点，最高层只包含起点
        ArrayList<ArrayList<Integer>> nodeLayers = new ArrayList<>();
        ArrayList<Integer> curNodes = new ArrayList<>();
        curNodes.add(lightPath.dst);
        nodeLayers.add(curNodes);
        while (true){
            if(isContained_nodeInAl(nodeLayers, lightPath.src)) break;
            ArrayList<Integer> nextLayer = new ArrayList<>();
            for(int i=0;i<curNodes.size();i++){
                for(int j=0;j<solution.instance.graph.length;j++){
                    if(solution.instance.graph[j][curNodes.get(i)] == 1 && !isContained_nodeInAl(nodeLayers, j) && !nextLayer.contains(j)){
                        nextLayer.add(j);
                        if(j == lightPath.src) break;
                    }
                }
            }
            nodeLayers.add(nextLayer);
            curNodes = nextLayer;
        }
        System.out.println();
        //从最高层向下删掉起点无法到达的点
        ArrayList<ArrayList<Integer>> new_NodeLayers = new ArrayList<>();
        ArrayList<Integer> tempNodeLayer = new ArrayList<>();
        tempNodeLayer.add(lightPath.src);
        new_NodeLayers.add(tempNodeLayer);
        for(int i=nodeLayers.size()-2;i>=0;i--){
            ArrayList<Integer> arrayList = new ArrayList<>();
            for(int j=0;j<nodeLayers.get(i).size();j++){
                for(int k=0;k<tempNodeLayer.size();k++){
                    if(solution.instance.graph[tempNodeLayer.get(k)][nodeLayers.get(i).get(j)] == 1 && !arrayList.contains(nodeLayers.get(i).get(j))){
                        arrayList.add(nodeLayers.get(i).get(j));
                    }
                }

            }
            tempNodeLayer = arrayList;
            new_NodeLayers.add(tempNodeLayer);
        }
        Collections.reverse(new_NodeLayers);
        if(new_NodeLayers.size() < 3){
            route.add(lightPath.src);
            route.add(lightPath.dst);
            //层数只有2，说明起点终点直通，跳数为1
            return 0.5;
        }
        else {
            route.add(lightPath.dst);
            return recursion(0, solution.overload[bin], lightPath.dst, solution.instance, lightPath, new_NodeLayers);
        }
    }

    public double recursion(int layer, int[][] overload, int curNode, Instance instance, LightPath lightPath, ArrayList<ArrayList<Integer>> nodeLayers){
        //最初的层数curNode为0, 位于连接需求的终点，逐层向前直至起点，默认层数超过2层
        if(layer == nodeLayers.size() - 2){

            route.add(lightPath.src);
            if(overload[curNode][lightPath.src] == 0) return 0.5;
            return overload[curNode][lightPath.src] - 0.5;
        }
        //从下一层中找到最佳连接点，递归跳转到下一层
        int bestNode = -1;
        double bestFx = Double.MAX_VALUE;
        for(int i=0;i<nodeLayers.get(layer+1).size();i++) {
            try{
            if (instance.graph[curNode][nodeLayers.get(layer + 1).get(i)] == 1) {
                double curFx = overload[curNode][nodeLayers.get(layer + 1).get(i)] - 0.5;
                if (curFx < bestFx) {
                    bestNode = nodeLayers.get(layer + 1).get(i);
                    bestFx = curFx;
                }
            }
        }catch (ArrayIndexOutOfBoundsException E){
                System.out.println();
            }
        }
        route.add(bestNode);
        return bestFx + recursion(layer+1, overload, bestNode, instance, lightPath, nodeLayers);
    }

    //以下为功能函数
    public boolean isContained_nodeInAl(ArrayList<ArrayList<Integer>> arr, int i){
        for(int x=0;x<arr.size();x++){
            for(int y=0;y<arr.get(x).size();y++){
                if(i == arr.get(x).get(y)) return true;
            }
        }
        return false;
    }
    public boolean isContained_nodeInQueue(Queue<Integer> arr, int i){
        for(int n:arr){
            if(n == i) return true;
        }
        return false;
    }
}
class nodeLayer{
    //带有分层的节点
    int nodeNum;
    int layer;
    public nodeLayer(int nodeNum, int layer){
        this.nodeNum = nodeNum;
        this.layer = layer;
    }
}
