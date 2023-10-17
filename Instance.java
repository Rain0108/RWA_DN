import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class Instance {
    public int[][] graph;
    public ArrayList<LightPath> lightPaths;
    public int num_of_node;
    public int num_of_lightPaths;
    public int num_of_edges;
    Parameters parameters = new Parameters();

    public Instance(String jsonPath) {
        File jsonFile = new File(jsonPath);
        try {
            FileReader fileReader = new FileReader(jsonFile);
            BufferedReader reader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            while (true) {
                int ch = reader.read();
                if (ch != -1) {
                    sb.append((char) ch);
                } else {
                    break;
                }
            }
            fileReader.close();
            reader.close();
            JSONObject jsonObject = new JSONObject(sb.toString());
            num_of_node = jsonObject.getJSONObject("graph").getInt("nodeNum");
            JSONArray jsonArray = jsonObject.getJSONObject("graph").getJSONArray("edges");
            num_of_edges = jsonArray.length();
            JSONArray jsonArray1 = jsonObject.getJSONArray("traffics");
            graph = new int[num_of_node][num_of_node];
            lightPaths = new ArrayList<>(jsonArray1.length());
            for(int i=0;i<graph.length;i++){
                for(int j=0;j<graph[0].length;j++){
                    graph[i][j] = parameters.MaxValue;
                }
            }
            for(int i=0;i<jsonArray.length();i++){
                graph[jsonArray.getJSONObject(i).getInt("source")][jsonArray.getJSONObject(i).getInt("target")] = 1;
                graph[jsonArray.getJSONObject(i).getInt("target")][jsonArray.getJSONObject(i).getInt("source")] = 1;
            }
            for(int i=0;i<jsonArray1.length();i++){
                lightPaths.add(new LightPath(jsonArray1.getJSONObject(i).getInt("ID"),
                        jsonArray1.getJSONObject(i).getInt("src"), jsonArray1.getJSONObject(i).getInt("dst")));
            }
            num_of_lightPaths = lightPaths.size();

        } catch (IOException e) {
            System.out.println("IOException found");
        }

    }

}
class LightPath{
    int ID;
    int src;
    int dst;
    String sPath;
    int minHop;
    public LightPath(int id, int src, int dst){
        this.ID = id;
        this.src = src;
        this.dst = dst;
    }
    public LightPath(int id, int src, int dst, String sPath, int minHop){
        this.ID = id;
        this.src = src;
        this.dst = dst;
        this.sPath = sPath;
        this.minHop = minHop;
    }
}
