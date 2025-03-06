import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Test {
    static int tests = 1;
    static int passed = 0;
    static String testFile = "testfile.gbk";
    static int t = 2;
    static int k = 4;
    public static void main(String args[]){
        try {
            BTree tree = new BTree(testFile, t, k, 0, 0);
            TreeObject O1 = new TreeObject(1,1);
            tree.BTreeInsert(tree, O1);
            TreeObject O2 = new TreeObject(2,1);
            tree.BTreeInsert(tree, O2);
            TreeObject O3 = new TreeObject(3,1);
            tree.BTreeInsert(tree, O3);
            TreeObject O4 = new TreeObject(4,1);
            tree.BTreeInsert(tree, O4);
            TreeObject O5 = new TreeObject(5,1);
            tree.BTreeInsert(tree, O5);
            TreeObject O6 = new TreeObject(6,1);
            tree.BTreeInsert(tree, O6);
            TreeObject O7 = new TreeObject(7,1);
            tree.BTreeInsert(tree, O7);
            TreeObject O8 = new TreeObject(8,1);
            tree.BTreeInsert(tree, O8);
            TreeObject O9 = new TreeObject(9,1);
            tree.BTreeInsert(tree, O9);
            TreeObject O10 = new TreeObject(10,1);
            tree.BTreeInsert(tree, O10);
            TreeObject O11 = new TreeObject(10,1);
            tree.BTreeInsert(tree, O11);
            TreeObject O12 = new TreeObject(10,1);
            tree.BTreeInsert(tree, O12);
            TreeObject O13 = new TreeObject(13,1);
            tree.BTreeInsert(tree, O13);
            TreeObject O14 = new TreeObject(14,1);
            tree.BTreeInsert(tree, O14);
            TreeObject O15 = new TreeObject(15,1);
            tree.BTreeInsert(tree, O15);
            TreeObject O16 = new TreeObject(7,1);
            tree.BTreeInsert(tree, O16);
            TreeObject O17 = new TreeObject(17,1);
            tree.BTreeInsert(tree, O17);
            TreeObject O18 = new TreeObject(18,1);
            tree.BTreeInsert(tree, O18);
            TreeObject O19 = new TreeObject(19,1);
            tree.BTreeInsert(tree, O19);
            TreeObject O20 = new TreeObject(20,1);
            tree.BTreeInsert(tree, O20);
//            tree.BTreeSearch(tree.root, O3);
            tree.close();

            tree.BTreeDump("dump", tree);
            BTree treeFromFile = new BTree(testFile);
            TreeObject returnKey = treeFromFile.BTreeSearch(treeFromFile.root, new TreeObject(5,1));
            System.out.print("BTreeSearch test: ");
            if(returnKey != null){
                System.out.println(returnKey.toString(tree.k));
            }

            System.out.println("\n\n");

            for (int i = 0; i < tree.numberOfNodes; i++) {
            	System.out.println(tree.getNodeAtIndex(i + 1));
            }

            System.out.println("\n\n");

            for (int i = 0; i < tree.numberOfNodes; i++) {
                System.out.println(treeFromFile.getNodeAtIndex(i + 1));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

