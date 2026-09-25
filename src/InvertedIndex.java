import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;




public class InvertedIndex {

    private List<String> documents;
    private Map<String, LinkedList<Integer>> index;

    public void indexDocument(String path) throws IOException, ClassNotFoundException {
        String text = Files.readString(Paths.get(path));
        text = text.toLowerCase().strip().replaceAll("\\p{P}", "");;
        String words[] = text.split("\\s+");

        for(String s : words){
            index.put(s, new LinkedList<>());
        }
        for(String s : words){
            index.get(s).add(0);
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        InvertedIndex a = new InvertedIndex();
        a.indexDocument("collection/King_Lear.txt");
    }
}


