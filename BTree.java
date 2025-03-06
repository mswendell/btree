import java.io.*;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Class used for storage using a BTree model
 * @author Michael Wendell
 *
 */
public class BTree {
    BTreeNode root;
    int numberOfNodes = 0;
    int t;
    int k;
    int cacheFlag;
    int cacheSize;
    Cache<Long, BTreeNode> cache;
    // need to increment nextAdress by BTreeNodeSize every time you make a new node
    long nextAdress = 24;
    RandomAccessFile raf;
    private final int BTreeNodeSize;

    public BTree(String fileName, int t, int k, int cacheFlag, int cacheSize) throws IOException {
        raf = new RandomAccessFile(fileName, "rw");
        this.t = t;
        this.k = k;
        this.cacheFlag = cacheFlag;
        // TODO double check to make sure formula is correct - (1 long + 1 int) * (# of keys) + (1 long) * (# of children pointers) + (1 int + 1 long + 1 boolean)
        BTreeNodeSize = 12 * (2 * t) + 8 * (2 * t + 1) + 13;
        root = new BTreeNode(getNextAddress(), true, this.t);
        numberOfNodes++;
        if(cacheFlag == 1){
            cache = new Cache(cacheSize);
        }
        raf.writeInt(t);
        raf.writeInt(k);
        raf.writeInt(cacheFlag);
        raf.writeInt(cacheSize);
        raf.writeLong(root.location);
    }

    public BTree(String fileName) throws IOException {
        raf = new RandomAccessFile(fileName, "rw");
        t = raf.readInt();
        k = raf.readInt();
        cacheFlag = raf.readInt();
        cacheSize = raf.readInt();
        BTreeNodeSize = 12 * (2 * t) + 8 * (2 * t + 1) + 13;
        if(cacheFlag == 1){
            cache = new Cache(cacheSize);
        }
        root = diskRead(raf.readLong());


    }

    public TreeObject BTreeSearch(BTreeNode x, TreeObject key) throws IOException {
        //recursive method to search the tree
        TreeObject retVal = null;
        int i = 1;
        while(i <= x.getN() && key.compareTo(x.keys[i]) == 1) {
            i++;
        }
        if(i <= x.getN() && key.isEqual(x.keys[i])) {
            return x.keys[i];
        }
        if(x.isLeaf()){
            return null;
        }
        else{
            //read child of i
            BTreeNode child = diskRead(x.childPointers[i]);
            retVal = BTreeSearch(child, key);
        }
        return retVal;
    }

    public void BTreeInsert(BTree t, TreeObject k) throws IOException {
        // method to start inserting a new object into the tree
        BTreeNode r = root;
        if(r.n == (2 * t.t - 1)){
            BTreeNode s = new BTreeNode(getNextAddress(), false, t.t);
            root = s;
            numberOfNodes++;
            s.childPointers[1] = r.location;
            BTreeSplitChild(s, 1, r);
            BTreeInsertNonFull(s, k);
        }
        else{
            BTreeInsertNonFull(r, k);
        }
    }

    public void BTreeInsertNonFull(BTreeNode x, TreeObject k) throws IOException {
        // method for the actual insertion when the cell is not full
        int i = x.n;
        if(x.isLeaf()){
            for (int j = 1; j <= x.n; j++) { //to check if a duplicate
                if (k.compareTo(x.keys[j]) == 0) {
                    x.keys[j].incrementTreeObject();
                    diskWrite(x);
                    return;
                }
            }
            while(i >= 1 && k.getData() < (x.keys[i].getData())){
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            x.keys[i + 1] = k;
            x.n++;
            diskWrite(x);
        }
        else{
            while(i > 1 && k.getData() < (x.keys[i].getData())){
                i--;
            }
            if (i >= 1 && k.compareTo(x.keys[i]) == 0) {
                x.keys[i].incrementTreeObject();
                diskWrite(x);
                return;
            }

            if(k.compareTo(x.keys[i]) == 1){
                i++;
            }
            BTreeNode child = diskRead(x.childPointers[i]);
            if(child.n == 2 * t - 1){
                BTreeSplitChild(x , i , child);
                if (k.compareTo(x.keys[i]) == 1) {
                    i++;
                }
                else if (k.compareTo(x.keys[i]) == 0) {
                    x.keys[i].incrementTreeObject();
                    diskWrite(x);
                    return;
                }
                child = diskRead(x.childPointers[i]);
            }

            BTreeInsertNonFull(child, k);
        }
    }

    public void BTreeSplitChild(BTreeNode x, int i, BTreeNode y) throws IOException {
        // method to split the cell when it is full
        BTreeNode z = new BTreeNode(getNextAddress(),y.isLeaf(), t);

        z.setN(t-1);
        for(int j = 1; j <= (t - 1); j++){
            z.keys[j] = y.keys[j + t];
        }
        if(!y.isLeaf()){
            for(int j = 1; j <= t; j++){
                z.childPointers[j] = y.childPointers[j + t];
            }
        }
        y.setN(t-1);
        for(int j = x.n + 1; j >= i; j--){
            x.childPointers[j+1] = x.childPointers[j];
        }
        x.childPointers[i + 1] = z.getLocation();
        for(int j = x.n; j >= i; j--){
            x.keys[j+1] = x.keys[j];
        }
        x.keys[i] = y.keys[t];
        x.n += 1;
        numberOfNodes++;
        diskWrite(x);
        diskWrite(y);
        diskWrite(z);
    }

    private void diskWrite(BTreeNode node) throws IOException {
        BTreeNode node2 = null;
        if (cacheFlag == 1 && cache.getNode(node.location) == null) {                                           // returns null if there is space for the node, if not the node
            node2 = cache.addObject(node.location, node);                //  that is returned is added to disk
            if (node2 != null) {
                node = node2;
                ByteBuffer buffer = ByteBuffer.allocate(BTreeNodeSize);
                buffer.putInt(node.n);
                if(node.isLeaf()){
                    buffer.put((byte)1);
                }
                else{
                    buffer.put((byte)0);
                }

                for(int i = 1; i <= node.n; i++){
                    buffer.putLong(node.keys[i].getData());
                    buffer.putInt(node.keys[i].getFrequency());
                }

                for(int i = 1; i <= node.n + 1; i++){
                    buffer.putLong(node.childPointers[i]);
                }

                raf.seek(node.location);
                raf.write(buffer.array());
            }
        } else if (cacheFlag == 0) {
            ByteBuffer buffer = ByteBuffer.allocate(BTreeNodeSize);
            buffer.putInt(node.n);

            if(node.isLeaf()){
                buffer.put((byte)1);
            }
            else{
                buffer.put((byte)0);
            }

            for(int i = 1; i <= node.n; i++){
                buffer.putLong(node.keys[i].getData());
                buffer.putInt(node.keys[i].getFrequency());
            }

            for(int i = 1; i <= node.n + 1; i++){
                buffer.putLong(node.childPointers[i]);
            }

            raf.seek(node.location);
            raf.write(buffer.array());
        }

    }

    private BTreeNode diskRead(long address) throws IOException {
        if (cacheFlag == 1) {
            BTreeNode node = cache.getNode(address);
            if (node != null) {
                cache.moveToFront(node);
                return node;

            } else {
                BTreeNode returnNode;
                boolean notfound = true;
                ByteBuffer buffer = ByteBuffer.allocate(BTreeNodeSize);
                raf.seek(address);
                raf.read(buffer.array());
                returnNode = new BTreeNode(buffer, address, t);
                BTreeNode n = cache.addObject(returnNode.location, returnNode);
                if (n != null) {
                    buffer = ByteBuffer.allocate(BTreeNodeSize);
                    buffer.putInt(n.n);

                    if(n.isLeaf()){
                        buffer.put((byte)1);
                    }
                    else{
                        buffer.put((byte)0);
                    }

                    for(int i = 1; i <= n.n; i++){
                        buffer.putLong(n.keys[i].getData());
                        buffer.putInt(n.keys[i].getFrequency());
                    }

                    for(int i = 1; i <= n.n + 1; i++){
                        buffer.putLong(n.childPointers[i]);
                    }

                    raf.seek(n.location);
                    raf.write(buffer.array());
                }
                return returnNode;
            }
        } else {
            BTreeNode returnNode;
            boolean notfound = true;

            ByteBuffer buffer = ByteBuffer.allocate(BTreeNodeSize);
            raf.seek(address);
            raf.read(buffer.array());
            returnNode = new BTreeNode(buffer, address, t);

            return returnNode;
        }
    }

    public void dumpTree(BTreeNode x , PrintStream ps) throws IOException {
        if(x.isLeaf()){
            for(int i = 1; i <= x.n; i ++){
                ps.append(x.keys[i].toString(k) + "\n");
            }
        }
        else{
            for(int i = 1; i <= x.n; i++){
                BTreeNode child = diskRead(x.childPointers[i]);
                dumpTree(child, ps);
                ps.append(x.keys[i].toString(k) + "\n");
            }
            BTreeNode child = diskRead(x.childPointers[x.n + 1]);
            dumpTree(child, ps);
        }
    }

    public void BTreeDump(String fileName, BTree tree) throws FileNotFoundException {
        PrintStream ps = new PrintStream(new File(fileName));
        PrintStream stdout = System.out;
        try {
            tree.dumpTree(tree.root, ps);
            System.setOut(ps);
            System.setOut(stdout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        raf.seek(16);
        raf.writeLong(root.location);
        if(cacheFlag == 1){
            cacheFlag = 0;
            for (BTreeNode n : cache.getNodeList()) {
                diskWrite(n);
            }
            cache.clearCache();
        }
    }
    private long getNextAddress(){
        long returnVal = nextAdress;
        nextAdress += BTreeNodeSize;
        return returnVal;
    }
    public String getNodeAtIndex(int i) throws IOException {
        if (i < 1) {
            throw new NoSuchElementException();
        }
        Queue<BTreeNode> queue = new LinkedList<BTreeNode>();
        queue.add(root);
        BTreeNode n = null;
        for (int j = 1; j <= i-1; j++) {
            n = queue.remove();
            if (!n.isLeaf()) {
                for (int a = 1; a <= n.n+1; a++) {
                    BTreeNode c = diskRead(n.childPointers[a]);
                    queue.add(c);
                }
            }

        }
        n = queue.remove();
        return n.toString();
    }

    public class BTreeNode {
        private long location;
        private int n;
        private boolean leaf;
        private TreeObject[] keys;
        private long[] childPointers;

        /*
        Constructor for BTreeNode takes in int location, parent, boolean leaf, and a TreeObject array as input. ChildPointer
        array must be set later if using this constructor
         */
        public BTreeNode(long loc, boolean leaf, int t){
            location = loc;
            this.n = 0;
            this.leaf = leaf;
            keys = new TreeObject[(2 * t)];
            childPointers = new long[2 * t + 1];
        }

        public BTreeNode(ByteBuffer buffer, long address, int t){
            keys = new TreeObject[(2 * t)];
            childPointers = new long[2 * t + 1];
            location = address;
            this.n = buffer.getInt();
            if((int)buffer.get() == 1){
                leaf = true;
            }
            else{
                leaf = false;
            }
            for(int i = 1; i <= n; i++){
                long data = buffer.getLong();
                int frequency = buffer.getInt();
                keys[i] = new TreeObject(data, frequency);

            }
            for(int i = 1; i <= n + 1; i++){
                long pointer = buffer.getLong();
                childPointers[i] = pointer;
            }

        }

        /*
        sets the value of leaf for BtreeNode input is true or false
         */
        public void setLeaf(boolean leaf) {
            this.leaf = leaf;
        }

        /*
        returns true or false based on if node is a leaf node
         */
        public boolean isLeaf() {
            return leaf;
        }

        /*
        returns the int location of the node
         */
        public long getLocation() {
            return location;
        }

        /*
         takes a int value to set to a new location for the node
         */
        public void setLocation(int location) {
            this.location = location;
        }

        /*
        returns the n value of the node (number of keys)
         */
        public int getN() {
            return n;
        }
        /*
        sets a new value for n in this BTreeNode (number of keys)
         */
        public void setN(int n) {
            this.n = n;
        }

        /*
            sets the key array of the BTreeNode to a new node
        */
        public void addKey(TreeObject key){
            keys[n] = key;
            n++;
        }

        /*
        sets a new int array as the child pointers for this node.
         */
        public void setChildPointers(long[] childPointers) {
            this.childPointers = childPointers;
        }

        /*
        returns the int array childPointers for this BTreeNode
         */
        public long[] getChildPointers() {
            return childPointers;
        }
        public String toString() {
            String retVal = "";
            for (int i = 1; i <= n; i++) {
                retVal += String.valueOf(keys[i].getData()) + " ";
            }
            return retVal;
        }
    }


}
