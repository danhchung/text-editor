package editor;

//import java.util.LinkedList;
//import javafx.application.Application;
import javafx.event.EventHandler;
//import javafx.geometry.VPos;
import javafx.scene.Group;
//import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;

/** An EventHandler to handle keys that get pressed. */
public class KeyEventHandler implements EventHandler<KeyEvent> {

    private final Group root;
    private int windowHeight;
    private int windowWidth;
    private FastLinkedList buffer;

    KeyEventHandler(final Group r, int wWidth, int wHeight, FastLinkedList bffr) {
        root = r;
        windowHeight = wHeight;
        windowWidth = wWidth;
        buffer = bffr;
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
            // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
            // the KEY_TYPED event, javafx handles the "Shift" key and associated
            // capitalization.
            String characterTyped = keyEvent.getCharacter();
            if (characterTyped.length() > 0
                && characterTyped.charAt(0) != 8 && !keyEvent.isShortcutDown()) {
                // Ignore control keys, which have non-zero length, as well as the backspace
                // key, which is represented as a character of value = 8 on Windows.
                if (characterTyped.equals("\r")) {
                    buffer.addText("\n");
                } else {
                    buffer.addText(characterTyped);
                }
                keyEvent.consume();
            }
        } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
            // events have a code that we can check (KEY_TYPED events don't have an associated
            // KeyCode).
            KeyCode code = keyEvent.getCode();
            if (keyEvent.isShortcutDown()) {
                if (code == KeyCode.S) {
                    Editor.saveFile();
                } else if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                    Editor.incFont();
                } else if (code == KeyCode.MINUS) {
                    Editor.decFont();
                } else if (code == KeyCode.P) {
                    Editor.printCursor();
                }
            }
            if (code == KeyCode.BACK_SPACE) {
                buffer.deleteText();
            } else if (code == KeyCode.LEFT) {
                buffer.cursorBack();
            } else if (code == KeyCode.RIGHT) {
                buffer.cursorForward();
            } else if (code == KeyCode.UP) {
                buffer.cursorUp();
            } else if (code == KeyCode.DOWN) {
                buffer.cursorDown();
            }
        }
        Editor.render(root, windowWidth, windowHeight, buffer);
    }

    public void setWidth(int w) {
        windowWidth = w;
    }

    public void setHeight(int h) {
        windowHeight = h;
    }

}
