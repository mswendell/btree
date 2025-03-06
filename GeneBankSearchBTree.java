import java.io.*;
import java.util.Scanner;

public class GeneBankSearchBTree {
    public static void main(String args[]) {
        int cache = 0;
        String BTreeFile;
        String queryFile;
        int cacheSize = 0;
        int debugLevel = 0;
        PrintStream ps;

        if (args.length < 3) {
            printUsageAndExit(1);
        }

        if (args.length > 5) {
            printUsageAndExit(1);
        }

        cache = Integer.parseInt(args[0]);
        if (cache != 0 && cache != 1) {
            printUsageAndExit(1);
        }

        BTreeFile = args[1];
        if (BTreeFile.isEmpty()) {
            printUsageAndExit(1);
        }

        queryFile = args[2];
        if (queryFile.isEmpty()) {
            printUsageAndExit(1);
        }

        if(cache == 1) {
            if (args.length > 4) {
                cacheSize = Integer.parseInt(args[3]);
                if (cacheSize < 0) {
                    printUsageAndExit(1);
                }
            }
        }
        if(cache == 1) {
            if (args.length > 5) {
                debugLevel = Integer.parseInt(args[4]);
                if (debugLevel > 1 || debugLevel < 0) {
                    printUsageAndExit(1);
                }
            }
        }
        else{
            if (args.length > 4) {
                debugLevel = Integer.parseInt(args[3]);
                if (debugLevel > 1 || debugLevel < 0) {
                    printUsageAndExit(1);
                }
            }
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(queryFile));
            Scanner scan = new Scanner(br);
            BTree tree = new BTree(BTreeFile);
            while (scan.hasNextLine()) {
                String string = scan.nextLine();
                Long data = serialize(string);
                TreeObject key = new TreeObject(data, 0);
                TreeObject returnKey = tree.BTreeSearch(tree.root, key);
                if(returnKey != null){
                    System.out.println(returnKey.toString(tree.k));
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR: Query file not found!");
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static long serialize(String string) {
        long retVal = 0;
        for (int i = 0; i < string.length(); i++) {
            char j = string.charAt(i);
            if (j == 'A') {

            } else if (j == 'T') {
                retVal += 3;
            } else if (j == 'C') {
                retVal += 1;
            } else if (j == 'G') {
                retVal += 2;
            }
            if (i < string.length()-1) {
                retVal = retVal << 2;
            }
        }
        return retVal;
    }



    public static void printUsageAndExit(String message, int i) {
        System.out.println(message);
        System.out.println("java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
        System.exit(i);
    }

    public static void printUsageAndExit(int i) {
        System.out.println("java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
        System.exit(i);
    }

}
