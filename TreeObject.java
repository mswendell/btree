/*
class for defining and creating a TreeObject to store in a BTreeNode
 */
public class TreeObject {
    private long data;
    private int frequency;

    public TreeObject(long data){
        this.data = data;
        frequency = 1;
    }

    public TreeObject(long data, int frequency){
        this.data = data;
        this.frequency = frequency;
    }

    /*
    r
     */
    public int compareTo(TreeObject o2){
        if(this.data > o2.data){
            return 1;
        }
        else if(this.data < o2.data){
            return -1;
        }
        else{
            return 0;
        }
    }

    public long getData(){
        return data;
    }

    public boolean isEqual(TreeObject o2){
        if(this.data == o2.data){
            return true;
        }
        else{
            return false;
        }
    }


    /*
    increments frequency by 1
    */
    public void incrementTreeObject(){
        frequency++;
    }

    /*
    returns the frequency of the TreeObject
     */
    public int getFrequency() {
        return frequency;
    }

    public String toString(int k) {
        StringBuilder str = new StringBuilder();
        String string = Long.toBinaryString(data);
        String zeros = "";
        for (int i = 0; i < 2 * k - string.length(); i++) {
            zeros += "0";
        }
        string = zeros + string;
        for (int i = 0; i <= string.length() - 2; i += 2) {
            String sub = string.substring(i, i + 2);
            if (sub.equals("00")) {
                str.append('a');
            }
            else if (sub.equals("11")){
                str.append('t');
            }
            else if (sub.equals("01")){
                str.append('c');
            }
            else if (sub.equals("10")){
                str.append('g');
            }
        }
        str.append(": " + frequency);
        return str.toString();
    }
}
