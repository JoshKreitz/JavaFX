package T2Final;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by User on 3/9/2016.
 */
public class Thesaurus {

    private Map<String, Set<String>> wordMap;

    public Thesaurus() {
        wordMap = new HashMap<String, Set<String>>();
    }

    public void addSyn(String word, String syn) {
        if (wordMap.get(word) == null) {
            Set<String> temp = new TreeSet<>();
            temp.add(syn);
            wordMap.put(word, temp);
        } else wordMap.get(word).add(syn);
    }

    public Set removeSyn(String syn) {
        Set<String> wordsThatContainSyn = new TreeSet<>();

        for (String key : wordMap.keySet())
            if (wordMap.get(key).contains(syn)) {
                wordsThatContainSyn.add(key);
                wordMap.get(key).remove(syn);
            }

        return wordsThatContainSyn;
    }

    public void load(String line) {
        String[] words = line.split(" ");
        wordMap.put(words[0], new TreeSet<>());
        for (int i = 1; i < words.length; i++)
            wordMap.get(words[0]).add(words[i]);
    }

    public Set getSyns(String word) {
        return wordMap.get(word);
    }
}
