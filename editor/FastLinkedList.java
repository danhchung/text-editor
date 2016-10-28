/* Code written by Daniel Chung
*/
package editor;

import javafx.scene.Group;
import javafx.scene.text.Text;
import java.util.Iterator;
//import java.lang.Iterable;
import javafx.scene.shape.Rectangle;

public class FastLinkedList implements Iterable<TextNode> {

    private final Group root;
    private TextNode sentinel;
    //private Node currentNode;
    private TextNode cursorPos;
    public static final ArrayDeque<TextNode> LINES = new ArrayDeque<TextNode>();

    public FastLinkedList(Group r) {
        root = r;
        sentinel = new TextNode(null, null, null);
        sentinel.setNextNode(sentinel);
        sentinel.setPrevNode(sentinel);
        //currentNode = sentinel;
        cursorPos = new TextNode(sentinel, new Text("Cursor"), sentinel);
        //lines.addLast(currentNode);
    }

    public TextNode cNode() {
        return cursorPos;
    }

    public TextNode sent() {
        return sentinel;
    }



    public void cursorBack() {
        TextNode beforeC = cursorPos.getPrevNode();
        TextNode b4bC = beforeC.getPrevNode();
        TextNode afterC = cursorPos.getNextNode();
        if (beforeC != sentinel) {
            b4bC.setNextNode(cursorPos);
            cursorPos.setPrevNode(b4bC);
            beforeC.setPrevNode(cursorPos);
            cursorPos.setNextNode(beforeC);
            beforeC.setNextNode(afterC);
            afterC.setPrevNode(beforeC);
            //Editor.setNextLine(true);
        }
        Editor.setNextLine(true);
    }

    public void cursorForward() {
        TextNode beforeC = cursorPos.getPrevNode();
        TextNode afterC = cursorPos.getNextNode();
        TextNode afaC = afterC.getNextNode();
        if (afterC != sentinel) {
            beforeC.setNextNode(afterC);
            afterC.setPrevNode(beforeC);
            afterC.setNextNode(cursorPos);
            cursorPos.setPrevNode(afterC);
            afaC.setPrevNode(cursorPos);
            cursorPos.setNextNode(afaC);
            //Editor.setNextLine(true);
        }
        Editor.setNextLine(true);
    }

    public void cursorUp() {
        Rectangle cursor = Editor.getCursor();
        int targetX = (int) Math.round(cursor.getX());
        int indexOfRow = 0;
        for (int i = 1; i < LINES.size(); i++) {
            TextNode temp = LINES.get(i);
            Text t = temp.getText();
            if (t.getY() == cursor.getY()) {
                indexOfRow = i;
            }
        }
        if (indexOfRow == 0) {
            TextNode beforeC = cursorPos.getPrevNode();
            TextNode afterC = cursorPos.getNextNode();
            TextNode afterS = sentinel.getNextNode();
            if (beforeC != sentinel) {
                beforeC.setNextNode(afterC);
                afterC.setPrevNode(beforeC);
                sentinel.setNextNode(cursorPos);
                afterS.setPrevNode(cursorPos);
                cursorPos.setPrevNode(sentinel);
                cursorPos.setNextNode(afterS);
            }
        } else if (indexOfRow > 0) {
            indexOfRow -= 1;
            TextNode pos = LINES.get(indexOfRow);
            if (pos == sentinel) {
                pos = pos.getNextNode();
            } else if (pos.getText().getText().equals("\n")) {
                pos = pos.getNextNode();
            } else {
                Editor.setNextLine(true);
            }
            TextNode closest = pos;
            //while (pos.getText().getY() != currentY)
            int textWidth = -1;
            while (pos != LINES.get(indexOfRow + 1)) {
                if (Math.abs(closest.getText().getX() - targetX)
                    >= Math.abs(pos.getText().getX() - targetX)) {
                //if (pos.getText().getX() < targetX)
                    closest = pos;
                    textWidth = (int) Math.round(pos.getText().getLayoutBounds().getWidth());
                }
                pos = pos.getNextNode();
            }
            if (Math.abs(closest.getText().getX() - targetX)
                >= Math.abs((closest.getText().getX() + textWidth) - targetX)) {
            //if (pos.getText().getX() < targetX)
                closest = pos;
                Editor.setNextLine(false);
                //Editor.setRightOfText(true);
            }
            pos = closest;
            TextNode beforeC = cursorPos.getPrevNode();
            TextNode afterC = cursorPos.getNextNode();
            TextNode beforePos = pos.getPrevNode();
            beforeC.setNextNode(afterC);
            afterC.setPrevNode(beforeC);
            beforePos.setNextNode(cursorPos);
            pos.setPrevNode(cursorPos);
            cursorPos.setPrevNode(beforePos);
            cursorPos.setNextNode(pos);
        }
    }

    public void cursorDown() {
        Rectangle cursor = Editor.getCursor();
        int targetX = (int) Math.round(cursor.getX());
        int indexOfRow = 0;
        for (int i = 1; i < LINES.size(); i++) {
            TextNode temp = LINES.get(i);
            Text t = temp.getText();
            if (t.getY() == cursor.getY()) {
                indexOfRow = i;
            }
        }
        if (indexOfRow == LINES.size() - 1) {
            TextNode beforeC = cursorPos.getPrevNode();
            TextNode afterC = cursorPos.getNextNode();
            TextNode beforeS = sentinel.getPrevNode();
            if (afterC != sentinel) {
                beforeC.setNextNode(afterC);
                afterC.setPrevNode(beforeC);
                sentinel.setPrevNode(cursorPos);
                beforeS.setNextNode(cursorPos);
                cursorPos.setPrevNode(beforeS);
                cursorPos.setNextNode(sentinel);
            }
        } else if (indexOfRow < LINES.size() - 1) {
            indexOfRow += 1;
            TextNode pos = LINES.get(indexOfRow);
            if (pos.getText().getText().equals("\n")) {
                pos = pos.getNextNode();
                if (pos == sentinel) { //|| pos.getText().getText().equals("\n"))
                    TextNode beforeC = cursorPos.getPrevNode();
                    TextNode afterC = cursorPos.getNextNode();
                    TextNode beforeS = sentinel.getPrevNode();
                    if (afterC != sentinel) {
                        beforeC.setNextNode(afterC);
                        afterC.setPrevNode(beforeC);
                        sentinel.setPrevNode(cursorPos);
                        beforeS.setNextNode(cursorPos);
                        cursorPos.setPrevNode(beforeS);
                        cursorPos.setNextNode(sentinel);
                    }
                    return;
                }
            } else {
                Editor.setNextLine(true);
            }
            TextNode closest = pos;
            //while (pos.getText().getY() != currentY)
            int textWidth = -1;
            while (pos != sentinel && pos != LINES.get(indexOfRow + 1)) {
                if (Math.abs(closest.getText().getX() - targetX)
                    >= Math.abs(pos.getText().getX() - targetX)) {
                //if (pos.getText().getX() < targetX)
                    closest = pos;
                    textWidth = (int) Math.round(pos.getText().getLayoutBounds().getWidth());
                }
                pos = pos.getNextNode();
            }
            if (Math.abs(closest.getText().getX() - targetX)
                >= Math.abs((closest.getText().getX() + textWidth) - targetX)) {
            //if (pos.getText().getX() < targetX)
                closest = pos;
                Editor.setNextLine(false);
                //Editor.setRightOfText(true);
            }
            pos = closest;
            TextNode beforeC = cursorPos.getPrevNode();
            TextNode afterC = cursorPos.getNextNode();
            TextNode beforePos = pos.getPrevNode();
            beforeC.setNextNode(afterC);
            afterC.setPrevNode(beforeC);
            beforePos.setNextNode(cursorPos);
            pos.setPrevNode(cursorPos);
            cursorPos.setPrevNode(beforePos);
            cursorPos.setNextNode(pos);


        }
    }

    public void cursorMouse(int x, int y) {
        boolean left = true;
        int ind = 0;
        int currY = 0;
        TextNode targetRow = LINES.get(0);
        while (currY < y && ind < LINES.size()) {
            targetRow = LINES.get(ind);
            ind++;
            currY += Editor.getFontHeight();
            //System.out.print("a");
        }
        TextNode targetCol = targetRow;
        if (targetCol == sentinel || targetCol.getText().getText().equals("\n")) {
            targetCol = targetCol.getNextNode();
        }
        if ((int) Math.round(targetCol.getText().getLayoutBounds().getWidth())
            + Editor.getLeftMargin() > x) {
            Editor.setNextLine(true);
        }
        int currX = Editor.getLeftMargin();
        while (targetCol != sentinel && targetCol
            != LINES.get((ind + 1) % LINES.size()) && currX < x) {
            //System.out.print("b");
            int textWidth = (int) Math.round(targetCol.getText().getLayoutBounds().getWidth());
            /*if (x < currX + textWidth/2) {
                left = true;
            } else {
                left = false;
            }*/
            currX += textWidth;
            targetCol = targetCol.getNextNode();
        }
        if (left) {
            TextNode beforeC = cursorPos.getPrevNode();
            TextNode afterC = cursorPos.getNextNode();
            TextNode beforePos = targetCol.getPrevNode();
            beforeC.setNextNode(afterC);
            afterC.setPrevNode(beforeC);
            beforePos.setNextNode(cursorPos);
            targetCol.setPrevNode(cursorPos);
            cursorPos.setPrevNode(beforePos);
            cursorPos.setNextNode(targetCol);
        } else {
            TextNode beforeC = cursorPos.getPrevNode();
            TextNode afterC = cursorPos.getNextNode();
            TextNode afterPos = targetCol.getNextNode();
            beforeC.setNextNode(afterC);
            afterC.setPrevNode(beforeC);
            afterPos.setPrevNode(cursorPos);
            targetCol.setNextNode(cursorPos);
            cursorPos.setPrevNode(targetCol);
            cursorPos.setNextNode(afterPos);
        }
    }



    public void addText(String x) {
        Text t = Editor.createText(x);
        root.getChildren().add(t);
        //Node curr = currentNode;
        TextNode curr = cursorPos.getPrevNode();
        //Node next = currentNode.getNextNode();
        TextNode next = cursorPos;
        TextNode toAdd = new TextNode(curr, t, next);
        curr.setNextNode(toAdd);
        next.setPrevNode(toAdd);
        //currentNode = toAdd;
    }

    public void deleteText() {
        TextNode toDelete = cursorPos.getPrevNode();
        if (toDelete != sentinel) {
            //root.getChildren().remove(currentNode.getText());
            root.getChildren().remove(toDelete.getText());
            //Node prev = currentNode.getPrevNode();
            TextNode prev = toDelete.getPrevNode();
            //Node next = currentNode.getNextNode();
            TextNode next = cursorPos;
            //currentNode = prev;
            prev.setNextNode(next);
            next.setPrevNode(prev);
        }
    }

    public void clearLines() {
        while (!LINES.isEmpty()) {
            LINES.removeLast();
        }
        LINES.addLast(sentinel);
    }

    public void addLine(TextNode n) {
        LINES.addLast(n);
    }

    public TextNode[] linesToArray() {
        TextNode[] lns = new TextNode[LINES.size()];
        for (int i = 0; i < LINES.size(); i++) {
            lns[i] = LINES.get(i);
        }
        return lns;
    }

    private class NodeIterator implements Iterator {
        private TextNode current;

        public NodeIterator() {
            current = sentinel;
        }

        public boolean hasNext() {
            if (current.getNextNode() == sentinel) {
                return false;
            }
            return true;
        }

        public TextNode next() {
            if (!hasNext()) {
                return null;
            }
            TextNode returnItem = current.getNextNode();
            current = current.getNextNode();
            return returnItem;
        }
    }

    @Override
    public Iterator<TextNode> iterator() {
        return new NodeIterator();
    }
}
