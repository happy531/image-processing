module com.example.image_processing_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.image_processing to javafx.fxml;
    exports com.example.image_processing;
    exports com.example.image_processing.enums;
    opens com.example.image_processing.enums to javafx.fxml;
    exports com.example.image_processing.modals;
    opens com.example.image_processing.modals to javafx.fxml;
    exports com.example.image_processing.algorithms;
    opens com.example.image_processing.algorithms to javafx.fxml;
    exports com.example.image_processing.utils;
    opens com.example.image_processing.utils to javafx.fxml;
}