import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class InvertedIndex implements Serializable{

    private List<String> documents;
    private Map<String, LinkedList<Integer>> index;
    private int docIDcnt = 0;
    private List<Integer> indexSize = new ArrayList<>();

    public InvertedIndex() {
        this.documents = new ArrayList<>();
        this.index = new HashMap<>();
    }

    public void printIndexedDocuments() {

        System.out.println("+--------+--------------------------------------------------------------+-----------+");
        System.out.println("| docID  | file                                                         | indexsize |");
        System.out.println("+--------+--------------------------------------------------------------+-----------+");

        for (int i = 0; i < documents.size(); i++) {
            System.out.printf("| %6d | %-60s | %9d |%n", i, documents.get(i), indexSize.get(i));
        }

        System.out.println("+--------+--------------------------------------------------------------+-----------+");
    }

    public void indexDocument(String path) throws IOException {

        int docID;

        for (String i : documents) {
            if (i.equals(path)) return;
        }

        documents.add(path);

        docID=docIDcnt;
        docIDcnt++;


        String text = Files.readString(Paths.get(path));
        text = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " "); //wth

        String[] words = text.split("\\s+");

        int cnt = 0;

        for(String word : words)
        {

            if(index.get(word) != null) {
                boolean flag = true;
                for(var i : index.get(word)) {
                    if (i == docID) {
                        flag = false;
                        break;
                    }
                }
                if (flag) index.get(word).add(docID);
            }
            else {
                index.put(word, new LinkedList<>());
                index.get(word).add(docID);
                cnt++;
            }
        }

        if (indexSize.size() == 0) indexSize.add(cnt);
        else indexSize.add(indexSize.get(indexSize.size() - 1) + cnt);
        System.out.printf("%s проиндексирован\n", path);
    }

    public void indexCollection(String path) throws IOException, ClassNotFoundException {

        File folder = new File(path);
        File f = new File(path + ".out");

        if(f.exists()) {
            System.out.println("ok"); //просто для проверки, что это работает
            FileInputStream fis = new FileInputStream(path + ".out");
            ObjectInputStream oin = new ObjectInputStream(fis);
            InvertedIndex out = (InvertedIndex) oin.readObject();
            this.documents = out.documents;
            this.index = out.index;
            this.docIDcnt = out.docIDcnt;
            this.indexSize = out.indexSize;
            return;
        }
        else {
            File[] files = folder.listFiles();
            Arrays.sort(files, (File f1, File f2) ->
                    f1.getName().toUpperCase().compareTo(f2.getName().toUpperCase())
            ); // linux problems)))
            for (File i : files) {
                if(i.isDirectory()) indexCollection(i.getPath());
                else indexDocument(i.getPath());
            }
            FileOutputStream fos = new FileOutputStream(path + ".out");

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(this);

            oos.flush();

            oos.close();
        }
    }
    // почему на uml - ???
    public LinkedList<Integer> getIntersection(LinkedList<Integer> list1, LinkedList<Integer> list2) {

        LinkedList<Integer> result = new LinkedList<>();
        Iterator<Integer> it1 = list1.iterator();
        Iterator<Integer> it2 = list2.iterator();

        if (!it1.hasNext() || !it2.hasNext()) {
            return result;
        }

        Integer l1 = it1.next();
        Integer l2 = it2.next();

        while(true) {

            if(l1.compareTo(l2) == 0) {

                result.add(l1);
                if(!it1.hasNext() || !it2.hasNext()) break;
                l1 = it1.next();
                l2 = it2.next();
            }
            else if (l1.compareTo(l2) < 0) {

                if(!it1.hasNext()) break;
                l1 = it1.next();

            }
            else {

                if(!it2.hasNext()) break;
                l2 = it2.next();

            }
        }
        return result;
    }
    // ???
   public LinkedList<Integer> getUnion(LinkedList<Integer> list1, LinkedList<Integer> list2) {

       LinkedList<Integer> result = new LinkedList<>();
       Iterator<Integer> it1 = list1.iterator();
       Iterator<Integer> it2 = list2.iterator();

       if (!it1.hasNext()) {
           if (!it2.hasNext()) return result;
           Integer l2 = it2.next();
           result.add(l2);
           while (it2.hasNext()) {
               l2 = it2.next();
               result.add(l2);
           }
           return result;
       }

       if (!it2.hasNext()) {
           if (!it1.hasNext()) return result;
           Integer l1 = it1.next();
           result.add(l1);
           while (it1.hasNext()) {
               l1 = it1.next();
               result.add(l1);
           }
           return result;
       }

       Integer l1 = it1.next();
       Integer l2 = it2.next();

       while (true) {

           if (l1.compareTo(l2) == 0) {
               result.add(l1);

               if (!it1.hasNext()) {
                   if (!it2.hasNext()) return result;
                   while (it2.hasNext()) {
                       l2 = it2.next();
                       result.add(l2);
                   }
                   return result;
               }

               if (!it2.hasNext()) {
                   if (!it1.hasNext()) return result;
                   while (it1.hasNext()) {
                       l1 = it1.next();
                       result.add(l1);
                   }
                   return result;
               }

               l1 = it1.next();
               l2 = it2.next();

           } else if (l1.compareTo(l2) < 0) {

               result.add(l1);

               if (!it1.hasNext()) {
                   result.add(l2);
                   while (it2.hasNext()) {
                       l2 = it2.next();
                       result.add(l2);
                   }
                   return result;
               }

               if (!it2.hasNext()) {
                   boolean flag = true;
                   while (it1.hasNext()) {
                       l1 = it1.next();
                       if (flag && l2 < l1) {
                           result.add(l2);
                           flag = false;
                       }
                       result.add(l1);
                   }
                   if(flag) result.add(l2);
                   return result;
               }

               l1 = it1.next();

           } else {

               result.add(l2);

               if (!it1.hasNext()) {
                   boolean flag = true;
                   while (it2.hasNext()) {
                       l2 = it2.next();
                       if(flag && l1 < l2) {
                           result.add(l1);
                           flag = false;
                       }
                       result.add(l2);
                   }
                   if(flag) result.add(l1);
                   return result;
               }

               if (!it2.hasNext()) {
                   result.add(l1);
                   while (it1.hasNext()) {
                       l1 = it1.next();
                       result.add(l1);
                   }
                   return result;
               }

               l2 = it2.next();

           }

       }
   }
   public LinkedList<Integer> executeQuery(String query){
       String[] terms = query.toLowerCase().split("\\s+");
       LinkedList<Integer> result = index.getOrDefault(terms[0], new LinkedList<>());

       for (int i = 1; i + 1 < terms.length; i += 2) {

           String operation = terms[i];
           LinkedList<Integer> next = index.getOrDefault(terms[i + 1], new LinkedList<>());

           if (operation.equals("and")) {
               result = getIntersection(result, next);
           } else if (operation.equals("or")) {
               result = getUnion(result, next);
           }
       }

       return result;
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        InvertedIndex a = new InvertedIndex();
        a.indexCollection("collection");
        a.printIndexedDocuments();
        System.out.println(a.executeQuery("Brutus"));
        System.out.println(a.executeQuery("Caesar"));
        System.out.println(a.executeQuery("Calpurnia"));
        System.out.println(a.executeQuery("Brutus AND Brutus"));
        System.out.println(a.executeQuery("Brutus AND Caesar"));
        System.out.println(a.executeQuery("Brutus AND Caesar AND Calpurnia"));
        System.out.println(a.executeQuery("Brutus OR Brutus"));
        System.out.println(a.executeQuery("Brutus OR Caesar"));
        System.out.println(a.executeQuery("Brutus OR Caesar OR Calpurnia"));
        System.out.println(a.executeQuery("SpiderMan"));
        System.out.println(a.executeQuery("Brutus AND SpiderMan"));
        System.out.println(a.executeQuery("Caesar OR SpiderMan"));
    }
}


