module com.example.image_processing_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.image_processing to javafx.fxml;
    exports com.example.image_processing;
}