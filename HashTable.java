import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unchecked")
public class HashTable<AnyType> {

    // Instance variables
    private static final int DEFAULT_TABLE_SIZE = 31;
    private static final double LOAD_FACTOR_THRESHOLD = 0.6;
    private static final int HASH_PRIME = 31;

    private List<AnyType>[] table;
    private int currentSize;

    // Constructors
    HashTable() {
        this(DEFAULT_TABLE_SIZE);
    }

    HashTable(int size) {
        table = new List[nextPrime(size)];
        for (int i = 0; i < table.length; i++) {
            table[i] = new LinkedList<>();
        }
        currentSize = 0;
    }

    // Methods
    public AnyType insert(AnyType value) {
        List<AnyType> listToInsert = table[hash(value.toString())];
        listToInsert.add(value);
        currentSize++;

        // Check if rehashing is needed based on load factor
        if ((double) currentSize / table.length > LOAD_FACTOR_THRESHOLD) {
            rehash();
        }
        return value;
    }

    public void remove(AnyType value) {
        List<AnyType> listToRemove = table[hash(value.toString())];
        if (listToRemove.contains(value)) {
            listToRemove.remove(value);
            currentSize--;
        }
    }

    public void clearMonthlyBonuses(){
        for (int i = 0; i< table.length; i++) {
            List<AnyType> currentList = table[i];
            for (AnyType item : currentList) {
                Branch b = (Branch) item;
                b.monthlyBonuses = 0;
            }
        }
    }

    public AnyType findInTable(String keyString) {
        List<AnyType> listToCheck = table[hash(keyString)];

        for (AnyType item : listToCheck) { // find the object with the given toString()
            if (item.toString().equals(keyString)) return item;
        }
        // if it is not already inserted return null
        return null;
    }

    // Basic hash function using ASCII values
    private int hash(String key) {
        int hashValue = 0;
        for (int i = 0; i < key.length(); i++) {
            hashValue = (hashValue * HASH_PRIME + key.charAt(i));
        }
        hashValue = hashValue % table.length;
        if (hashValue < 0)
            return hashValue + table.length;

        return hashValue;
    }

    // rehash method to create a bigger table when the load factor is exceeded
    private void rehash() {
        List<AnyType>[] oldLists = table;
        table = new List[nextPrime(table.length * 2)];
        for(int i = 0; i < table.length; i++) {
            table[i] = new LinkedList<>();
        }
        currentSize = 0;
        for(List<AnyType> list : oldLists) {
            for(AnyType item: list) {
                insert(item);
            }
        }
    }

    // methods to arrange the table size, since it should be prime
    private static int nextPrime(int currentPrime) {
        if (currentPrime % 2 == 0) {
            currentPrime++;
        }
        while (!isPrime(currentPrime)) {
            currentPrime += 2;
        }
        return currentPrime;
    }

    private static boolean isPrime(int n) {
        if (n == 2 || n == 3) {
            return true;
        }
        if (n == 1 || n % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        for (List<AnyType> item: table) {
            System.out.print(item + " ");
        }
        System.out.println();
        return "";
    }
}
