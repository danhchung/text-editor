package editor;
//Code Written by Daniel Chung

import javafx.scene.text.Text;

public class TextNode {

    private Text text;
    private TextNode nextNode;
    private TextNode prevNode;

    public TextNode(TextNode p, Text t, TextNode n) {
        text = t;
        nextNode = n;
        prevNode = p;
    }

    public Text getText() {
        return text;
    }

    public TextNode getNextNode() {
        return nextNode;
    }

    public TextNode getPrevNode() {
        return prevNode;
    }

    public void setText(Text o) {
        text = o;
    }

    public void setNextNode(TextNode n) {
        nextNode = n;
    }

    public void setPrevNode(TextNode n) {
        prevNode = n;
    }
}
