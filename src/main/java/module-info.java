module com.example.backgroundapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.backgroundapp to javafx.fxml;
    exports com.example.backgroundapp;
}