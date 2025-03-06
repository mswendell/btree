import java.util.LinkedList;

/**
 * This is the Cache.java class that was made to simulate the behavior of a cache.
 * It uses a Linked List data structure to do this.
 * This class contains the constructor for Cache. Also, the following methods
 * getObject, addObject, removeObject, clearCache, getHist, and getRef.
 *
 * @author Connor Thorpe
 *
 * @param <K> type to store
 */

public class Cache<T, K>{


    private LinkedList<BTree.BTreeNode> nodeList = new LinkedList<BTree.BTreeNode>();
    private int maxSize;


    /*
     * This is the constructor for the cache object. This only takes a integer s for the max size of the
     * cache as input.
     */
    public Cache(int s) {

        maxSize = s;

    }

    public LinkedList<BTree.BTreeNode> getNodeList() {
        return nodeList;
    }

    /*
     * this method is used to search though the cache for a element. it will increment ref every time it is called.
     * the once it searches the cache and it gets a match for the element it will increment hits and return the element.
     * If it never finds the element in the cache it will return null.
     */
    public BTree.BTreeNode getNode(T element) {
        BTree.BTreeNode retVal = null;
        for (BTree.BTreeNode k : nodeList) {
            if (k.getLocation() == (Long)element) {
                retVal = k;
            }
        }
        return retVal;
    }

    public void moveToFront(BTree.BTreeNode node) {
        nodeList.remove(node);
        nodeList.addFirst(node);
    }

    /*
     * the addObject method takes in a element for input. Then it will add a element to the front of the cache using LinkedList
     * addFirst method. Also if the cache is at its max size then it will remove the last item in the cache before adding the new element
     * using LinkedList removeLast method.
     */
    public BTree.BTreeNode addObject(T address, BTree.BTreeNode node) {      // returns null if there is space for the objects, if not, the object must be written to disk
        BTree.BTreeNode nodeRemoved = null;
        if (!nodeList.isEmpty()) {
            if(nodeList.size() >= maxSize) {
                nodeRemoved = nodeList.removeLast();
            }
        }
        nodeList.addFirst(node);
        return nodeRemoved;
    }


    /*
     * The clearCache Method used LinkedList clear method to remove all elements in the cache.
     */
    public void clearCache() {
        nodeList = null;
    }

}
