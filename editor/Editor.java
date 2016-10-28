package editor;

//import java.util.LinkedList;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
//import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//import java.io.InputStream;
import java.io.BufferedReader;
import javafx.scene.input.MouseEvent;
import java.io.File;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;


public class Editor extends Application {

    private static String file;

    //Window values
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;
    private static final int LEFT_MARGIN = 5;
    private static int RIGHT_MARGIN = 5;
    private static final int TOP_MARGIN = 0;
    private static final int BOTTOM_MARGIN = 0;

    private static int windowWidth = WINDOW_WIDTH;
    private static int windowHeight = WINDOW_HEIGHT;

    // Create a Node that will be the parent of all things displayed on the screen.
    private static Group root = new Group();
    private static Group textRoot = new Group();
    // The Scene represents the window: its height and width will be the height and width
    // of the window displayed.
    private static Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);
    // To get information about what keys the user is pressing, create an EventHandler.
    // EventHandler subclasses must override the "handle" function, which will be called
    // by javafx.
    private EventHandler<KeyEvent> keyEventHandler =
        new KeyEventHandler(textRoot, WINDOW_WIDTH, WINDOW_HEIGHT, buffer);

    private static ScrollBar scrollBar = new ScrollBar();
    private static int offset = 0;

    private static FastLinkedList buffer = new FastLinkedList(textRoot);
    private static boolean debugMode = false;

    //Font values
    private static final int STARTING_FONT_SIZE = 12;
    private static int fontSize = STARTING_FONT_SIZE;
    private static String fontName = "Verdana";
    private static int fontHeight =
        (int) Math.round(createText("Oh boy!").getLayoutBounds().getHeight());

    //Cursor variables.
    private static final Rectangle cursor = new Rectangle(1, fontHeight);
    private static boolean nextLine = false;

    @Override
    public void start(Stage primaryStage) {
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        root.getChildren().add(textRoot);
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);


        scrollBar.setOrientation(Orientation.VERTICAL);
        // Set the height of the scroll bar so that it fills the whole window.
        scrollBar.setPrefHeight(WINDOW_HEIGHT);
        // Set the range of the scroll bar.
        scrollBar.setMin(0);
        scrollBar.setMax(windowHeight);
        // Add the scroll bar to the scene graph, so that it appears on the screen.
        root.getChildren().add(scrollBar);
        int usableScreenWidth = WINDOW_WIDTH - (int) Math.round(scrollBar.getLayoutBounds().getWidth());
        RIGHT_MARGIN += (int) Math.round(scrollBar.getLayoutBounds().getWidth());
        scrollBar.setLayoutX(usableScreenWidth);

        // All new Nodes need to be added to the root in order to be displayed.
        cursor.setX(LEFT_MARGIN);
        textRoot.getChildren().add(cursor);
        makeCursorBlink();

        //Initial rendering of file.
        render(textRoot, windowWidth, windowHeight, buffer);

        primaryStage.setTitle("Text Editor");

        /** When the scroll bar changes position, change the height of Josh. */
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                // newValue describes the value of the new position of the scroll bar. The numerical
                // value of the position is based on the position of the scroll bar, and on the min
                // and max we set above. For example, if the scroll bar is exactly in the middle of
                // the scroll area, the position will be:
                //      scroll minimum + (scroll maximum - scroll minimum) / 2
                // Here, we can directly use the value of the scroll bar to set the height of Josh,
                // because of how we set the minimum and maximum above.
                offset = -newValue.intValue();
                textRoot.setLayoutY(offset);
            }
        });

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                // Re-compute Allen's width.
                int newWidth = newScreenWidth.intValue();
                windowWidth = newWidth;
                ((KeyEventHandler)keyEventHandler).setWidth(newWidth);
                render(textRoot, windowWidth, windowHeight, buffer);
                int usableScreenWidth = windowWidth - (int) Math.round(scrollBar.getLayoutBounds().getWidth());
                scrollBar.setLayoutX(usableScreenWidth);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                int newHeight = newScreenHeight.intValue();
                windowHeight = newHeight;
                ((KeyEventHandler)keyEventHandler).setHeight(newHeight);
                render(textRoot, windowWidth, windowHeight, buffer);
                scrollBar.setPrefHeight(windowHeight);
            }
        });

        scene.setOnMouseClicked(new MouseClickEventHandler(textRoot));

        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();


    }


    private static int wordWidth(ArrayDeque<TextNode> w) {
        int sum = 0;
        for (int i = 0; i < w.size(); i++) {
            sum += (int) Math.round(w.get(i).getText().getLayoutBounds().getWidth());
        }
        return sum;
    }

    /* Used to render the text during resizing/textinput/deletion operations. */
    public static void render(final Group root, int wWidth, int wHeight, FastLinkedList bffr) {
        bffr.clearLines();
        int currX = LEFT_MARGIN;
        int currY = TOP_MARGIN;
        int cursorX = currX;
        int cursorY = currY;
        int limit = wWidth - RIGHT_MARGIN;
        //int incY = (int)Math.round(createText("Creates new lines!").getLayoutBounds().getHeight());
        ArrayDeque<TextNode> word = new ArrayDeque<TextNode>();
        boolean cursorOnEdge = false;
        for (TextNode n : bffr) {
            if (n != bffr.cNode()) {
                Text t = n.getText();
                String str = t.getText();
                int textWidth = (int) Math.round(t.getLayoutBounds().getWidth());
                if (str.equals("\n") || n == bffr.sent()) {
                    while (!word.isEmpty()) {
                        word.removeLast();
                    }
                }
                if (str.equals("\n")) {
                    currX = LEFT_MARGIN;
                    currY += fontHeight;
                    bffr.addLine(n);

                }/* else if (cursorOnEdge && n.getPrevNode() == bffr.cNode()) {
                    bffr.addLine(n);
                    bffr.lines.printDeque();
                    while (!word.isEmpty()) {
                        word.removeFirst();
                    }
                } */else if ((wordWidth(word) + textWidth >= limit && !str.equals(" "))) {
                    currX = LEFT_MARGIN;
                    currY += fontHeight;
                    bffr.addLine(n);
                    while (!word.isEmpty()) {
                        word.removeLast();
                    }

                } else if (currX + textWidth >= limit && !str.equals(" ")) {
                    currX = LEFT_MARGIN;
                    currY += fontHeight;
                    if (word.isEmpty()) {
                        bffr.addLine(n);
                    } else {
                        bffr.addLine(word.get(0));
                    }
                    while (!word.isEmpty()) {
                        TextNode tn = word.removeFirst();
                        Text tx = tn.getText();
                        int tnWidth = (int) Math.round(tx.getLayoutBounds().getWidth());
                        tx.setX(currX);
                        tx.setY(currY);
                        tx.toFront();
                        updateText(tx);
                        if (tn.getPrevNode() == bffr.cNode()) {
                            cursorX = currX;
                            cursorY = currY;
                            if (cursorX > limit) {
                                cursorX = limit;
                            }
                            cursor.setX(cursorX);
                            cursor.setY(cursorY);
                            cursor.toFront();
                            updateCursor(cursor);
                        }
                        currX += tnWidth;

                    }
                } else if (str.equals(" ")) {
                    while (!word.isEmpty()) {
                        word.removeLast();
                    }
                }
                t.setX(currX);
                t.setY(currY);
                t.toFront();
                currX += textWidth;
                updateText(t);
                if (!str.equals(" ") && !str.equals("\n")) {
                    word.addLast(n);
                }
            } else {
                cursorX = currX;
                cursorY = currY;
                if (n.getNextNode() != bffr.sent()) {
                    Text next = n.getNextNode().getText();
                    String nextstr = next.getText();
                    int nextTextWidth = (int) Math.round(next.getLayoutBounds().getWidth());
                    if (nextLine && (wordWidth(word) + nextTextWidth >= limit && !nextstr.equals(" "))) {
                        cursorX = LEFT_MARGIN;
                        cursorY += fontHeight;
                        cursorOnEdge = true;
                    } else if (nextLine && currX + nextTextWidth >= limit && !nextstr.equals(" ")) {
                        cursorX = LEFT_MARGIN + wordWidth(word);
                        cursorY += fontHeight;
                    }
                }
                if (cursorX > limit) {
                    cursorX = limit;
                }
                cursor.setX(cursorX);
                cursor.setY(cursorY);
                cursor.toFront();
                updateCursor(cursor);
            }
        }
        setNextLine(false);
        int textHeight = bffr.linesToArray().length * fontHeight;
        if (textHeight > wHeight) {
            scrollBar.setMax(textHeight%wHeight);
            //int off = offset + (wHeight-textHeight);
            //textRoot.setLayoutY(off);
        } else {
            scrollBar.setMax(-offset);
        }
    }

    /* Reads file and stores it in the buffer. */
    private static void readFile(String fileName) {
        //Some of this code was taken from CopyFile.java provided in the examples folder.
        try {
            String dbg = "";
            File inputFile = new File(fileName);
            if (!inputFile.exists()) {
                //System.out.println(fileName + " does not exist!");
                FileWriter writer = new FileWriter(fileName);
                writer.close();
                //return;
            }
            FileReader reader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            int intRead = -1;
            while ((intRead = bufferedReader.read()) != -1) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                char charRead = (char) intRead;
                buffer.addText(""+charRead);
                dbg += charRead;
            }
            if (debugMode) {
                System.out.println(dbg);
            }
            bufferedReader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when reading file; exception was: " + ioException);
        }
    }

    public static void saveFile() {
        try {
            FileWriter writer = new FileWriter(file);
            for (TextNode n : buffer) {
                String t = n.getText().getText();
                if (n != buffer.cNode()) {
                    if (!t.equals("\r")){
                        writer.write(t);
                    }
                }
            }
            writer.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when reading file; exception was: " + ioException);
        }
    }

    public static void main(String[] args) {
        //Some of this code was taken from CopyFile.java provided in the examples folder.
        if (args.length > 2) {
            System.out.println("Cannot take more than 2 arguments!");
            System.exit(1);
        } else if (args.length == 0) {
            System.out.println("No filename was provided.");
            System.exit(1);
        }
        String inputFileName = args[0];
        file = inputFileName;
        if (args.length == 2 && args[1].equals("debug")) {
            debugMode = true;
        }
        readFile(inputFileName);
        launch(args);
    }






    /* Various utility methods. */
    public static Rectangle getCursor() {
        return cursor;
    }

    public static int getLeftMargin() {
        return LEFT_MARGIN;
    }

    public static int getFontHeight() {
        return fontHeight;
    }

    public static Text createText(String str) {
        Text t = new Text();
        t.setText(str);
        t.setFont(Font.font(fontName, fontSize));
        t.setTextOrigin(VPos.TOP);
        return t;
    }

    private static void updateText(Text t) {
        t.setFont(Font.font(fontName, fontSize));
    }

    public static void printCursor() {
        int cX = (int) Math.round(cursor.getX());
        int cY = (int) Math.round(cursor.getY());
        System.out.println(cX + ", " + cY);
    }

    private static void updateCursor(Rectangle r) {
        r.setHeight(fontHeight);
    }

    public static void decFont() {
        if (fontSize > 4) {
            fontSize -= 1;
        } else {
            fontSize = 4;
        }
        fontHeight = (int) Math.round(createText("Oh boy!").getLayoutBounds().getHeight());
    }

    public static void incFont() {
        fontSize += 1;
        fontHeight = (int) Math.round(createText("Oh boy!").getLayoutBounds().getHeight());
    }





    //Cursor Code

    /** An EventHandler to handle changing the color of the rectangle. */
    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private boolean blink;
        //private Color[] boxColors = {Color.LIGHTPINK, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.LIGHTBLUE, Color.PURPLE};

        RectangleBlinkEventHandler() {
            // Set the color to be the first color in the list.
            //changeColor();
            blink = true;
        }

        private void changeBlink() {
            if (blink) {
                cursor.setOpacity(0.0);
            } else {
                cursor.setOpacity(1.0);
            }
            blink = !blink;
            //cursor.setFill(boxColors[currentColorIndex]);
            //currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            //changeBlink();
        }
    }

    /** Makes the text bounding box change color periodically. */
    public void makeCursorBlink() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public static void setNextLine(boolean b) {
        nextLine = b;
    }

    public static boolean getNextLine() {
        return nextLine;
    }


    /** An event handler that displays the current position of the mouse whenever it is clicked. */
    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        /** A Text object that will be used to print the current mouse position. */

        MouseClickEventHandler(Group root) {
        }


        @Override
        public void handle(MouseEvent mouseEvent) {
            // Because we registered this EventHandler using setOnMouseClicked, it will only called
            // with mouse events of type MouseEvent.MOUSE_CLICKED.  A mouse clicked event is
            // generated anytime the mouse is pressed and released on the same JavaFX node.
            int mousePressedX = (int) Math.round(mouseEvent.getX());
            int mousePressedY = (int) Math.round(mouseEvent.getY());

            //System.out.println("" + mousePressedX + "" + mousePressedY);
            buffer.cursorMouse(mousePressedX, mousePressedY);
            Editor.render(root, windowWidth, windowHeight, buffer);
        }
    }
}
