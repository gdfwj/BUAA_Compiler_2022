package backend.mips.reg;

import java.util.HashMap;
import java.util.Map;

public class RegNameMap {
    private final HashMap<String, Integer> name2num = new HashMap<>();
    private final HashMap<Integer, String> num2name = new HashMap<>();

    private static final RegNameMap Instance = new RegNameMap();

    public static RegNameMap getInstance() {
        return Instance;
    }

    private RegNameMap() {
        name2num.put("$zero", 0);
        name2num.put("$at", 1);
        name2num.put("$v0", 2);
        name2num.put("$v1", 3);
        name2num.put("$a0", 4);
        name2num.put("$a1", 5);
        name2num.put("$a2", 6);
        name2num.put("$a3", 7);
        name2num.put("$t0", 8);
        name2num.put("$t1", 9);
        name2num.put("$t2", 10);
        name2num.put("$t3", 11);
        name2num.put("$t4", 12);
        name2num.put("$t5", 13);
        name2num.put("$t6", 14);
        name2num.put("$t7", 15);
        name2num.put("$s0", 16);
        name2num.put("$s1", 17);
        name2num.put("$s2", 18);
        name2num.put("$s3", 19);
        name2num.put("$s4", 20);
        name2num.put("$s5", 21);
        name2num.put("$s6", 22);
        name2num.put("$s7", 23);
        name2num.put("$t8", 24);
        name2num.put("$t9", 25);
        name2num.put("$k0", 26);
        name2num.put("$k1", 27);
        name2num.put("$gp", 28);
        name2num.put("$sp", 29);
        name2num.put("$fp", 30);
        name2num.put("$ra", 31);

        for(Map.Entry<String, Integer> entry:name2num.entrySet()) {
            num2name.put(entry.getValue(), entry.getKey());
        }


    }

    public Integer getRegNum(String name) {
        return name2num.get(name);
    }

    public String getRegName(Integer num) {
        return num2name.get(num);
    }
}
