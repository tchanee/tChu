package ch.epfl.tchu.gui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * This class is responsible for the graphical interface of the welcome screen. An additional CSS style sheet
 * was made for it
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public class WelcomeScreenView {

    /**
     * Creates the welcome screen view
     * @return the node that shows everything
     */

    public static Node createWelcomeScreen(){

        HBox finalHbox = new HBox();

        VBox main = new VBox();

        HBox grid = new HBox();
        grid.getStylesheets().add("welcome.css");

        GridPane t = new GridPane();
        t.getStyleClass().add("logo");
        Rectangle t1 = new Rectangle(20, 20);
        t1.getStyleClass().add("visible");
        Rectangle t11 = new Rectangle(20, 20);
        t11.getStyleClass().add("visible");
        Rectangle t12 = new Rectangle(20, 20);
        t12.getStyleClass().add("visible");
        Rectangle t2 = new Rectangle(20, 40);
        t2.getStyleClass().add("visible");
        Rectangle in_t3 = new Rectangle(20, 40);
        in_t3.getStyleClass().add("invisible");
        Rectangle in_t4 = new Rectangle(20, 40);
        in_t4.getStyleClass().add("invisible");
        t.add(t1,0,0);
        t.add(t11,1,0);
        t.add(t12,2,0);
        t.add(in_t3,0,1);
        t.add(t2,1,1);
        t.add(in_t4,2,1);

        grid.getChildren().add(t);
        grid.getChildren().add(new Separator(Orientation.VERTICAL));

        GridPane c = new GridPane();
        c.getStyleClass().add("logo");
        Rectangle c1 = new Rectangle(20, 20);
        c1.getStyleClass().add("visible");
        Rectangle c11 = new Rectangle(20, 20);
        c11.getStyleClass().add("visible");
        Rectangle c12 = new Rectangle(20, 20);
        c12.getStyleClass().add("visible");
        Rectangle c2 = new Rectangle(40, 20);
        c2.getStyleClass().add("visible");
        Rectangle c3 = new Rectangle(40, 20);
        c3.getStyleClass().add("visible");
        Rectangle in_c4 = new Rectangle(40, 20);
        in_c4.getStyleClass().add("invisible");
        in_c4.getStyleClass().add("up");
        c.add(c1,0,0);
        c.add(c11,0,1);
        c.add(c12,0,2);
        c.add(c2,1,0);
        c.add(c3,1,2);
        c.add(in_c4,1,1);

        grid.getChildren().add(c);
        grid.getChildren().add(new Separator(Orientation.VERTICAL));

        GridPane h = new GridPane();
        h.getStyleClass().add("logo");
        Rectangle h1 = new Rectangle(20, 60);
        h1.getStyleClass().add("visible");
        Rectangle h2 = new Rectangle(20, 20);
        h2.getStyleClass().add("visible");
        Rectangle h3 = new Rectangle(20, 60);
        h3.getStyleClass().add("visible");
        h.add(h1,0,0);
        h.add(h2,1,0);
        h.add(h3,2,0);

        grid.getChildren().add(h);
        grid.getChildren().add(new Separator(Orientation.VERTICAL));

        GridPane u = new GridPane();
        u.getStyleClass().add("logo");
        Rectangle u1 = new Rectangle(20, 60);
        u1.getStyleClass().add("visible");

        VBox middle = new VBox();
        Rectangle u2 = new Rectangle(20, 20);
        u2.getStyleClass().add("visible");
        Rectangle in_u4 = new Rectangle(20, 40);
        in_u4.getStyleClass().add("invisible");
        middle.getChildren().add(in_u4);
        middle.getChildren().add(u2);

        Rectangle u3 = new Rectangle(20, 60);
        u3.getStyleClass().add("visible");

        u.add(u1,0,0);
        u.add(middle,1,0);
        u.add(u3,2,0);

        grid.getChildren().add(u);

        main.getChildren().add(new Separator(Orientation.VERTICAL));
        main.getChildren().add(grid);
        main.getChildren().add(new Separator(Orientation.VERTICAL));

        finalHbox.getChildren().add(new Separator(Orientation.VERTICAL));
        finalHbox.getChildren().add(main);
        finalHbox.getChildren().add(new Separator(Orientation.VERTICAL));

        return finalHbox;
    }
}
