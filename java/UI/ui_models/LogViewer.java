package UI.ui_models;

import javafx.geometry.BoundingBox;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LogViewer extends Stage {

    private final String title;
    private final AnchorPane parentPane;
    private final Button hideButton;
    private final Button clearButton;
    private final Button testButton;
    private Scene scene;
    private final AnchorPane anchorPane;
    private ListView<Label> logsListView;

    private boolean isOpen = false;


    public LogViewer(AnchorPane parentPane, String title) {
//        this.initStyle(StageStyle.TRANSPARENT);
        this.title = title;
        this.initStyle(StageStyle.UNDECORATED);
        this.parentPane = parentPane;
        this.logsListView = initListView();
        this.hideButton = initHideButton();
        this.clearButton = initClearButton();
        this.testButton = initTestButton();
        this.anchorPane = initAnchorPane();
    }

    private AnchorPane initAnchorPane(){
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(250, 600);
        Label titleLabel = new Label(title);
        titleLabel.setPrefSize(100, 25);
        titleLabel.setLayoutX(10);
        titleLabel.setLayoutY(2);

        anchorPane.getChildren().addAll(hideButton, clearButton, logsListView, testButton, titleLabel);
        return anchorPane;
    }
    private ListView<Label> initListView(){
        ListView<Label> objectListView = new ListView<>();
        objectListView.setPrefSize(240, 500);
        objectListView.setLayoutX(5);
        objectListView.setLayoutY(50);
        return objectListView;
    }

    private Button initHideButton(){
        Button button = new Button("X");
        button.setPrefSize(25, 25);
        button.setLayoutX(225);
        button.setLayoutY(0);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> this.hide());
        return button;
    }

    private Button initClearButton(){
        Button button = new Button("Очистить");
        button.setPrefSize(75, 25);
        button.setLayoutX(150);
        button.setLayoutY(550);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> logsListView.getItems().clear());
        return button;
    }

    private Button initTestButton(){
        Button button = new Button("Test");
        button.setPrefSize(75, 25);
        button.setLayoutX(50);
        button.setLayoutY(550);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> addLine("WARNING: Loading FXML document with JavaFX API of version 21 by JavaFX runtime of version 8.0.202-ea" + Math.random() * 100));
        return button;
    }

    public void addLine(String text){
        Label label = new Label();
        label.setText(text);
        label.setTextFill(Color.GREEN);

        label.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {

        });


        this.logsListView.getItems().add(label);
    }

    public void open(){
        if (scene == null){
            scene = new Scene(anchorPane, 250, 600);
            this.setScene(scene);
        }
        this.show();
    }

    public BoundingBox getBound(){
        return new BoundingBox(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public boolean isIntersected(LogViewer viewer){
        return this.getBound().intersects(viewer.getBound());
    }


    public boolean isOpen() {
        return this.isShowing();
    }
}
