import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GeneBankCreateBTree {
    public static void main(String args[]) {
        long startTime = System.currentTimeMillis();
        int cache = 0;
        int degree = -1;
        int sequenceLength = 0;
        int cacheSize = 0;
        int debugLevel = 0;
        long finishTime;
        long startTimeSeq = 0;
        long endTimeSeq = 0;
        String fileName = "";
        try {
            if (args.length < 4) {
                printUsageAndExit(1);
            }
            if (args.length > 6) {
                printUsageAndExit(1);
            }
            cache = Integer.parseInt(args[0]);
            if (cache != 0 && cache != 1) {
                printUsageAndExit(1);
            }
            degree = Integer.parseInt(args[1]);
            if (degree < 0 || degree == 1) {
                printUsageAndExit(1);
            }
            if(degree == 0){
                degree = 4075/40;
            }
            sequenceLength = Integer.parseInt(args[3]);
            if (sequenceLength < 1 || sequenceLength > 31) {
                printUsageAndExit(1);
            }
            fileName = args[2];
            if (args.length == 6) {
                if (cache == 1) {
                    cacheSize = Integer.parseInt(args[4]);
                } else {
                    cacheSize = 0;
                }
                debugLevel = Integer.parseInt(args[5]);
                if (debugLevel == 1) {
                    // create dump file
                } else if (debugLevel != 0) {
                    printUsageAndExit(1);
                }
            }
            if (args.length == 5) {
                int arg = Integer.parseInt(args[4]);
                if (cache == 1) {
                    cacheSize = arg;
                } else if (arg > 1) {
                    printUsageAndExit(1);
                } else if (arg == 1) {
                    debugLevel = 1;
                } else debugLevel = 0;
            }
            if (cache == 1 && cacheSize <= 0) printUsageAndExit(1);
        } catch (NumberFormatException e) {
            printUsageAndExit(1);
        }
        File file = new File(fileName);
        try {
            long insertCount = 0;
            BufferedReader br = new BufferedReader(new FileReader(file));
            Scanner scan = new Scanner(br);
            boolean start = false;
            String BTreeFile = fileName + ".btree.data." + sequenceLength + "." + degree;
            BTree tree = new BTree(BTreeFile, degree, sequenceLength, cache, cacheSize);
            String stringSeq = "";
            int count = 0;
            while (br.ready()) {
                count++;
                if(count % 500 == 0){
                    System.out.print("*");
                }
                String token = br.readLine();
                if (token.startsWith("ORIGIN")) {

                    start = true;
                } else if (token.startsWith("//")) {
                    startTimeSeq = System.currentTimeMillis();
                    start = false;
                    for(int i = 0; i <= stringSeq.length() - sequenceLength; i++){
                        String string = stringSeq.substring(i , sequenceLength + i);      //substring to be length in args
                        if(!string.contains("n")) {
                            long seq = serialize(string);
                            // insert seq into BTree here
                            startTimeSeq = System.currentTimeMillis();
                            endTimeSeq = System.currentTimeMillis();
                            //creates object using the serialized number and adds it to the tree
                            TreeObject newObject = new TreeObject(seq);
                            long startIns = System.currentTimeMillis();

                            tree.BTreeInsert(tree, newObject);
                            Long endTime = System.currentTimeMillis() - startIns;
                        }
                    }
                    stringSeq = "";

                }
                else if (start) {
                    String target = "\\t|[0-9]|\\s|\\n|[A-Z]";
                    token = token.replaceAll(target, "");
                    stringSeq = stringSeq.concat(token);
                }
            }

            if (debugLevel == 1) {
                tree.BTreeDump("dump", tree);
                System.out.println("Tree done writing to dump!");
            }
            tree.close();
        } catch (FileNotFoundException e) {
            printUsageAndExit("File not found: " + file.getPath(), 1);
        } catch (IOException e) {
            // from BTree throws declaration
            e.printStackTrace();
        }
        System.out.println("\nDone!\n");
        finishTime = (System.currentTimeMillis() - startTime);
        System.out.println("Time: " + finishTime + " milliseconds");

    }
    public static long serialize(String string) {
        long retVal = 0;
        for (int i = 0; i < string.length(); i++) {
            char j = string.charAt(i);
            if (j == 'a') {

            } else if (j == 't') {
                retVal += 3;
            } else if (j == 'c') {
                retVal += 1;
            } else if (j == 'g') {
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
        System.out.println("java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
        System.exit(i);
    }

    public static void printUsageAndExit(int i) {
        System.out.println("java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
        System.exit(i);
    }

}
