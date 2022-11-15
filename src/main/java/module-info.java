module lab01 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires org.json;
    opens lab to javafx.fxml;
    opens lab.enums to javafx.fxml;
    opens lab.entity to javafx.fxml;
    opens lab.enviroment to javafx.fxml;
    opens lab.gui to javafx.fxml;
    opens lab.interfaces to javafx.fxml;
    opens lab.controllers to javafx.fxml;
    exports lab;
    exports lab.enums;
    exports lab.entity;
    exports lab.enviroment;
    exports lab.gui;
    exports lab.interfaces;
    exports lab.controllers;
}