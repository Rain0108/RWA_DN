import java.util.ArrayList;
import java.util.Objects;

public class ECS {
    GreedyInsert gInsert;
    Parameters parameters;
    Algorithm algorithm;
    public ECS(Instance instance){
        gInsert = new GreedyInsert(instance);
        parameters = new Parameters();
        algorithm = new Algorithm();
    }
    public void ECS(Solution solution, LightPath lightPath, int bin) throws CloneNotSupportedException{
        //弹射链震动过程，返回lightpath插入bin后的Xbest
        double Fx = gInsert.greedyInsert(solution, bin, lightPath);
        aLPtB(solution, gInsert.route, bin);
        int[] x_best = new int[solution.assignment.length];
        for(int i=0;i<solution.assignment.length;i++){
            x_best[i] = solution.assignment[i];
        }
        x_best[lightPath.ID] = bin;
        //EjectionMove, 移除一个惩罚值最高的链路(重复的边最多)
        int Le = -1;
        int tempOverload = Integer.MIN_VALUE;
        for(int i=0;i<gInsert.instance.num_of_lightPaths;i++){
            if(solution.assignment[gInsert.instance.lightPaths.get(i).ID] == bin){
                int curOverload = getRouteOverload(solution, gInsert.route, bin);
                if(curOverload > tempOverload){
                    Le = gInsert.instance.lightPaths.get(i).ID;
                    tempOverload = curOverload;
                }
            }
        }
        eLPfB(solution, gInsert.route, bin);
        int no_improve_iter = 0;
        while (no_improve_iter < parameters.maxIter){
            Solution x_R_prev = solution.clone();
            //先找到与其他链路有重合边的所有链路
            ArrayList<ArrayList<Integer>> L0 = new ArrayList<>();
            for(int i=0;i<solution.overload[bin].length;i++){
                for(int j=0;j<solution.overload[bin][i].length;j++){
                    if(solution.overload[bin][i][j] > 1){
                        L0 = selectConflict(solution.routes.get(bin), i, j);
                    }
                }
            }
            ArrayList<Solution> P = new ArrayList<>();
            for(ArrayList<Integer> Li : L0){
                P.add(reroutingMove(solution, bin, Li));
            }
            if(Math.random() > parameters.alpha){
                //选择所有部分解中最好的
                int bestID = -1;
                double bestFx = Double.MAX_VALUE;
                for(int i=0;i<P.size();i++){
                    double curFx = algorithm.calculateSolutionFx(P.get(i));
                    if(curFx < bestFx){
                        bestID = i;
                        bestFx = curFx;
                    }
                }
                Solution x_R = P.get(bestID);
            }else{
                Solution x_R = P.get((int)(Math.random() * P.size()));
            }

        }
    }

    public Solution reroutingMove(Solution solution, int bin, ArrayList<Integer> lightPath) throws CloneNotSupportedException{
        //将一条有惩罚值的链路抽取出来再用greedyInsert插回去
        //完成抽取
        Solution newSolution = solution.clone();
        int flag = -1;
        for(int i=0;i<newSolution.routes.get(bin).size();i++){
            if(Objects.equals(newSolution.routes.get(bin).get(i).get(0), lightPath.get(0)) &&
                    Objects.equals(newSolution.routes.get(bin).get(i).get(newSolution.routes.get(bin).get(i).size() - 1), lightPath.get(lightPath.size() - 1))){
                flag = i;
            }
        }
        newSolution.routes.remove(flag);
        int lpID = getLightPathID(lightPath.get(0), lightPath.get(lightPath.size()-1));
        newSolution.assignment[lpID] = -1;
        for(int i=0;i<lightPath.size()-1;i++){
            newSolution.overload[bin][lightPath.get(i)][lightPath.get(i+1)] -= 1;
            newSolution.overload[bin][lightPath.get(i+1)][lightPath.get(i)] -= 1;
        }
        //开始greedyInsert
        GreedyInsert greedyInsert = new GreedyInsert(newSolution.instance);
        greedyInsert.greedyInsert(newSolution, bin, new LightPath(lpID, lightPath.get(0), lightPath.get(lightPath.size()-1)));
        aLPtB(newSolution, lightPath, bin);
        return newSolution;
    }
    public void aLPtB(Solution solution, ArrayList<Integer> lightPath, int bin){
         //当lightPath只包含节点序号时的分配方式
        for(int i=0;i<lightPath.size()-1;i++){
            solution.overload[bin][lightPath.get(i)][lightPath.get(i+1)] += 1;
            solution.overload[bin][lightPath.get(i+1)][lightPath.get(i)] += 1;
        }
        solution.assignment[getLightPathID(lightPath.get(0), lightPath.get(lightPath.size()-1))] = bin;
        solution.routes.get(bin).add(lightPath);
    }
    public void eLPfB(Solution solution, ArrayList<Integer> lightPath, int bin){
        for(int i=0;i<lightPath.size()-1;i++){
            solution.overload[bin][lightPath.get(i)][lightPath.get(i+1)] -= 1;
            solution.overload[bin][lightPath.get(i+1)][lightPath.get(i)] -= 1;
        }
        solution.assignment[getLightPathID(lightPath.get(0), lightPath.get(lightPath.size()-1))] = -1;
        int flag = -1;
        for(int i=0;i<solution.routes.get(bin).size();i++){
            if(Objects.equals(solution.routes.get(bin).get(i).get(0), lightPath.get(0)) &&
                    Objects.equals(solution.routes.get(bin).get(i).get(solution.routes.get(bin).get(i).size()-1), lightPath.get(lightPath.size()-1))){
                flag = i;
                break;
            }
        }
        solution.routes.get(bin).remove(flag);
    }
    public int getLightPathID(int src, int dst){
        for(int i=0;i<gInsert.instance.num_of_lightPaths;i++) {
            if (gInsert.instance.lightPaths.get(i).src == src && gInsert.instance.lightPaths.get(i).dst == dst) {
                return gInsert.instance.lightPaths.get(i).ID;
            }
        }
        return -1;
    }
    public int getRouteOverload(Solution solution, ArrayList<Integer> route, int bin){
        int res = 0;
        for(int i=0;i<route.size()-1;i++){
            if(solution.overload[bin][route.get(i)][route.get(i+1)] > 1) res++;
        }
        return res;
    }
    public ArrayList<ArrayList<Integer>> selectConflict(ArrayList<ArrayList<Integer>> routes, int src, int dst){
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();
        for(int i=0;i<routes.size();i++){
            for(int j=0;j<routes.get(i).size()-1;j++){
                if((routes.get(i).get(j) == src && routes.get(i).get(j+1) == dst) ||
                        (routes.get(i).get(j) == dst && routes.get(i).get(j+1) == src)){
                    res.add(routes.get(i));
                }
            }
        }
        return res;
    }
}
