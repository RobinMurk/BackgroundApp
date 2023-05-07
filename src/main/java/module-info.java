module com.example.backgroundapp {
    requires javafx.controls;
    requires javafx.fxml;
    //requires jna;
    requires com.sun.jna.platform;
    requires com.sun.jna;


    opens com.example.backgroundapp to javafx.fxml;
    exports com.example.backgroundapp;
}