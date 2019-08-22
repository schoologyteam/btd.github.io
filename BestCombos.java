import java.util.*;
import static java.lang.System.exit;

/**
 * Generate a list of combinations which represent the most 4TC combos
 * @author (name = "Brian Wei", date = "2019-08-22")
 */
public class BestCombos {
    private static final String[] ALL_TOWERS = {
            "Dart", "Tack", "Ice", "Glue", "Boomer", "Bomb",
            "Sniper", "Sub", "Bucc", "Heli", "Mortar", "Ace",
            "Super", "Ninja", "Alch", "Druid", "Wizard",
            "Spact", "Village",
            "Quincy", "Gwen", "Striker", "Obyn", "Church", "Ben", "Ezili", "Pat"
    };

    static class Combo {
        HashSet<String> towers;
        Combo(String s) {
            towers = parse(s);
        }
        Combo(ArrayList<String> sa){
            towers = new HashSet<>();
            towers.addAll(sa);
        }

        /**
         * Check if towers of c is a subset of the towers of this
         */
        boolean check(Combo c) {
            for(String tower : c.towers){
                if(!towers.contains(tower))
                    return false;
            }
            return true;
        }

        @Override public int hashCode(){
            return towers.hashCode();
        }
        @Override public boolean equals(Object o){
            if(!(o instanceof Combo)) return false;
            Combo c = (Combo)o;
            return this.towers.equals(c.towers);
        }
        @Override public String toString(){
            return towers.toString();
        }
    }

    /**
     * Parse a string of format |TOWER1  | TOWER2  |... into a HashSet
     */
    private static HashSet<String> parse(String s){
        ArrayList<String> pieces = new ArrayList<>(Arrays.asList(s.split("\\|")));
        for(int i = pieces.size(); --i >= 0; ) {
            String t = pieces.get(i);
            if(t.equals("")) {
                pieces.remove(i);
            } else {
                pieces.set(i, t.trim());
            }
        }
        return new HashSet<>(pieces);
    }

    /**
     * generate all combos of numElems towers
     * This code is modified based on
     *      http://hmkcode.com/calculate-find-all-possible-combinations-of-an-array-using-java/
     */
    private static ArrayList<Combo> generateCombos(int numElems){
        ArrayList<Combo> out = new ArrayList<>();
        int N = ALL_TOWERS.length; // 27
        int[] combination = new int[numElems];
        int r = 0;
        int index = 0;
        while(r >= 0){
            if(index <= (N + (r - numElems))){
                combination[r] = index;
                if(r == numElems - 1){
                    ArrayList<String> towers = new ArrayList<>();
                    for(int i : combination)
                        towers.add(ALL_TOWERS[i]);
                    out.add(new Combo(towers));
                    index++;
                } else {
                    index = combination[r]+1;
                    r++;
                }
            } else {
                r--;
                if(r > 0)
                    index = combination[r]+1;
                else
                    index = combination[0]+1;
            }
        }
        return out;
    }

    /**
     * generate pairs of Combo and value (number of 4t combos)
     */
    private static HashMap<Combo, Integer> getValues(ArrayList<Combo> toTry, ArrayList<Combo> remaining){
        HashMap<Combo, Integer> results = new HashMap<>();
        for(Combo c : toTry){
            ArrayList<Combo> clone = (ArrayList<Combo>) remaining.clone();
            results.put(c, clone.stream().filter(combo -> combo.check(c)).toArray().length);
        }
        return results;
    }

    /**
     * remove any combinations deemed impossible by impossible-combos.txt
     */
    private static void filterImpossible(HashMap<Combo, Integer> map){
        ArrayList<String> impossible = BTD6_4TC.readFromFile("impossible-combos");
        if(impossible == null) exit(1);
        ArrayList<Combo> impossibleList = new ArrayList<>();
        for(String s : impossible){
            impossibleList.add(new Combo(s));
        }
        for (Map.Entry<Combo, Integer> comboIntegerEntry : map.entrySet()) {
            for (Combo c : impossibleList) {
                if (c.check((Combo) ((Map.Entry) comboIntegerEntry).getKey())) {
                    map.compute((Combo) ((Map.Entry) comboIntegerEntry).getKey(), (k, v) -> 0);
                    break;
                }
            }
        }
        map.entrySet().removeIf(e -> (e.getValue() == 0));
    }

    public static void main(String[] args){
        ArrayList<String> remaining = BTD6_4TC.readFromFile("remaining-combos");
        if(remaining == null) exit(1);
        ArrayList<Combo> comboList = new ArrayList<>();
        for(String s : remaining){
            comboList.add(new Combo(s));
        }
        ArrayList<Combo> allCombos = generateCombos(3);
        // uncomment this line to include 2TC combos -- My guess is that none are actually possible
//        allCombos.addAll(generateCombos(2));

        HashMap<Combo, Integer> values = getValues(allCombos, comboList);
        filterImpossible(values);

        // Sort in descending order
        List<Map.Entry<Combo, Integer>> list = new ArrayList<>(values.entrySet());
        list.sort((o1, o2) -> -1 * (o1.getValue()).compareTo(o2.getValue()));
        System.out.println(list.toString());

    }
}
