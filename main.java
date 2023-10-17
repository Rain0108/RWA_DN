import org.json.JSONObject;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        Instance instance = new Instance("C:\\Users\\Rain\\Desktop\\DNILS-RWA-master\\Code\\deploy\\" +
                "Instance\\ATT.json");
        Solution solution = new Solution();
        solution.iniSolution();
        System.out.println();
    }
}
