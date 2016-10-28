package editor;
/**
 * Written by Daniel Chung.
 */

public class ArrayDeque<Item> {

    private Item[] items;


    private int size;
    private int nextFirst;
    private int first;
    private int nextLast;
    private int last;

    private double USAGEFACTOR = .25;
    private int RFACTOR = (int) (1 / USAGEFACTOR);

    public ArrayDeque() {
        size = 0;
        nextFirst = 3;
        nextLast = 4;
        first = 4;
        last = 4;
        items = (Item[]) new Object[8];
    }

    public int itemsLength() {
        return items.length;
    }

    public void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];

        int temp = first;
        for (int i = 0; i < size; i++) {
            if (temp >= items.length) {
                temp = 0;
            }
            a[i] = items[temp];
            temp++;
        }
        if (size == 0) {
            nextFirst = 3;
            nextLast = 4;
            first = 4;
            last = 4;
        } else {
            first = 0;
            last = size - 1;
            nextFirst = capacity - 1;
            nextLast = size;
        }
        /*if(first <= last){
            System.arraycopy(items, first, a, 0, size);
            first = 0;
            last = size-1;
            nextFirst = a.length - 1;
            nextLast = size;
        } else{
            int sizeLooped = items.length - first;
            System.arraycopy(items, 0, a, 0, nextLast);
            System.arraycopy(items, first, a, capacity - sizeLooped, sizeLooped);
            first = capacity - sizeLooped;
            nextFirst = first - 1;
        }*/


        items = a;
    }

    public void addFirst(Item o) {
        if (size == 0) {
            items[first] = o;
            nextFirst--;
            size++;
        } else {
            if (size == items.length) {
                resize(size * RFACTOR);
            }
            items[nextFirst] = o;
            increaseFirst();
        }
    }

    public void addLast(Item o) {
        if (size == 0) {
            items[last] = o;
            nextLast++;
            size++;
        } else {
            if (size == items.length) {
                resize(size * RFACTOR);
            }
            items[nextLast] = o;
            increaseLast();
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        String printed = "" + items[first];
        if (first <= last) {
            for (int i = first + 1; i < last + 1; i++) {
                printed += " " + items[i];
            }
        } else {
            for (int i = first + 1; i < items.length; i++) {
                printed += " " + items[i];
            }
            for (int i = 0; i < last; i++) {
                printed += " " + items[i];
            }
        }
        System.out.println(printed);
    }

    public Item removeFirst() {
        if (size == 0) {
            return null;
        }
        Item itemToRet = items[first];
        items[first] = null;
        decreaseFirst();
        if (size / items.length < USAGEFACTOR) {
            if (size < 8) {
                resize(8);
            } else {
                resize(size);
            }
        }
        /*if(size == 0) {
            nextFirst = 3;
            nextLast = 5;
            first = 4;
            last = 4;
        }*/
        return itemToRet;
    }

    public Item removeLast() {
        if (size == 0) {
            return null;
        }
        Item itemToRet = items[last];
        items[last] = null;
        decreaseLast();
        if (size / items.length < USAGEFACTOR) {
            if (size < 8) {
                resize(8);
            } else {
                resize(size);
            }
        }
        /*if(size == 0){
            nextFirst = 3;
            nextLast = 5;
            first = 4;
            last = 4;
        }*/
        return itemToRet;
    }

    public Item get(int index) {
        int trueIndex = first + index;
        if (trueIndex >= items.length) {
            trueIndex -= items.length;
        }
        return items[trueIndex];
    }

    private void increaseFirst() {
        nextFirst -= 1;
        if (nextFirst < 0) {
            nextFirst = items.length - 1;
        }
        first -= 1;
        if (first < 0) {
            first = items.length - 1;
        }
        size += 1;
    }

    private void decreaseFirst() {
        nextFirst += 1;
        if (nextFirst >= items.length) {
            nextFirst = 0;
        }
        first += 1;
        if (first >= items.length) {
            first = 0;
        }
        size -= 1;
        if (size == 0) {
            first = 4;
            nextFirst = 3;
        }
    }

    private void increaseLast() {
        nextLast += 1;
        if (nextLast >= items.length) {
            nextLast = 0;
        }
        last += 1;
        if (last >= items.length) {
            last = 0;
        }
        size += 1;
    }

    private void decreaseLast() {
        nextLast -= 1;
        if (nextLast < 0) {
            nextLast = items.length - 1;
        }
        last -= 1;
        if (last < 0) {
            last = items.length - 1;
        }
        size -= 1;
    }
}
