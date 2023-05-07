package com.example.backgroundapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * tekstid - ListView piltide kirjelduste jaoks
 * data - sisaldab piltide nimesid selleks, et nad ListViewi lisada
 * pildid - piltide massiiv
 * valitudListist - kui topeltklikkitakse siis saab kasutada, et taustapilt ära vahetada
 * laius, kõrgus - on esialgsed akna parameetrid
 */
public class Main extends Application {
    ListView<String> tekstid = new ListView<String>();
    ObservableList<String> data = FXCollections.observableArrayList();
    ImageView[] pildid = new ImageView[0];
    String valitudListist;
    List<String> teeNimed = new ArrayList<>();

    int laius = 500;
    int kõrgus = 500;


    public static void main(String[] args) {
        launch();
    }
    public void listiKoostamine(){

        tekstid.setCellFactory(param -> new ListCell<String>() {

            @Override
            public void updateItem(String tekst, boolean tühi) {
                Text t;
                super.updateItem(tekst, tühi);
                if (tühi) {
                    setText(null);
                    setGraphic(null);
                } else {
                    for (int i = 0; i < pildid.length; i++) {
                        if (data.get(i).equals(tekst)) {
                            HBox hb = new HBox();
                            t = new Text(tekst);
                            hb.getChildren().addAll(pildid[i], t);
                            hb.setSpacing(5);
                            setGraphic(hb);
                            break;
                        }
                    }
                }
            }

        });
    }

    @Override
    public void start(Stage lava) throws Exception {
        /**Pilt pilt1 = new Pilt("C:\\Users\\markusmi\\IdeaProjects\\OOP\\Projekt2\\BackgroundApp\\led3.png", "wallpaper");
        Pilt pilt2 = new Pilt("C:\\Users\\markusmi\\IdeaProjects\\OOP\\Projekt2\\BackgroundApp\\1.png", "öööö");
        Pilt pilt3 = new Pilt("C:\\Users\\markusmi\\IdeaProjects\\OOP\\Projekt2\\BackgroundApp\\kolmas_mmp.png", "kolmas");

        pildid = new ImageView[]{pilt1.getEelvaade(), pilt2.getEelvaade(), pilt3.getEelvaade()};
        data = FXCollections.observableArrayList(pilt1.getNimi(), pilt2.getNimi(), pilt3.getNimi());
        tekstid.setItems(data);*/

        listiKoostamine();

        VBox alus = new VBox();

        //ülemise vboxi sisu
        HBox ülemine = new HBox();
        Button lisa = new Button("Lisa");
        Button sätted = new Button("Sätted");
        ülemine.getChildren().addAll(sätted, lisa);
        ülemine.setSpacing(laius-87);
        alus.getChildren().add(ülemine);
        //nuppude funktsioonid:
        lisa.setOnMouseClicked(event -> dragAken());

        //listi sisu
        VBox vb = new VBox(tekstid);
        tekstid.setPrefHeight(kõrgus-50);
        alus.getChildren().add(vb);

        HBox alumine = new HBox();
        Label valitud = new Label();

        tekstid.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                if (event.getClickCount() == 1){
                    valitud.setText("Valitud on: " + tekstid.getSelectionModel().getSelectedItem() + System.lineSeparator() + "Taustapildi muutmiseks tehke topeltklõps");
                }
                if (event.getClickCount()==2) {
                    valitud.setText("Valik '" + tekstid.getSelectionModel().getSelectedItem() + "' on kinnitatud");//TODO: vaja fail nyyd panna taustapildiks
                    valitudListist = tekstid.getSelectionModel().getSelectedItem();
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).equals(tekstid.getSelectionModel().getSelectedItem())){
                            TasutapildiMuutja.muudatTaustakat(teeNimed.get(i));
                        }
                    }

                }
            }

        });
        alumine.getChildren().add(valitud);
        alus.getChildren().add(alumine);

        Scene stseen = new Scene(alus, laius, kõrgus);
        //akna suuruse muutmisel:
        stseen.widthProperty().addListener((observable, vanaVäärtus, uusVäärtus) -> {
            ülemine.setSpacing(uusVäärtus.doubleValue()-87);
        });
        stseen.heightProperty().addListener((observable, vanaVäärtus, uusVäärtus) -> {
            tekstid.setPrefHeight(uusVäärtus.doubleValue()-50);
        });


        lava.setScene(stseen);
        lava.setResizable(true);
        lava.show();
    }
    public void dragAken(){
        Stage lava = new Stage();
        Group juur = new Group();
        Scene stseen = new Scene(juur, 300, 300);
        Rectangle ruut = new Rectangle(300, 300);
        ruut.setArcHeight(40);
        ruut.setArcWidth(40);
        Text tekst = new Text("Lohista lisatav pildifail siia");
        tekst.setFill(Color.SNOW);
        tekst.setFont(Font.font(null, FontWeight.BOLD, 15));
        tekst.layoutXProperty().bind(stseen.widthProperty().subtract(tekst.prefWidth(-1)).divide(2));
        tekst.layoutYProperty().bind(stseen.heightProperty().subtract(tekst.prefHeight(-1)).divide(2));
        tekst.setTextOrigin(VPos.TOP);
        juur.getChildren().addAll(ruut, tekst);

        ruut.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        ruut.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean kas = false;
            if (db.hasFiles()) {
                String failitee = db.getFiles().get(0).getAbsolutePath();
                teeNimed.add(failitee);
                String nimi = db.getFiles().get(0).getName();
                nimi = nimi.substring(0, nimi.lastIndexOf('.'));
                kas = true;
                Pilt pilt = new Pilt(failitee, nimi);
                ImageView[] pildidAjutine = new ImageView[pildid.length+1];

                for (int i = 0; i < pildid.length; i++) {
                    pildidAjutine[i] = pildid[i];
                }

                pildidAjutine[pildidAjutine.length-1] = pilt.getEelvaade();
                pildid = pildidAjutine;//draggitud pilt lisatud piltide hulka, et ta nüüd listi panna
                data.add(nimi);
                tekstid.setItems(data);
            }
            event.setDropCompleted(kas);
            event.consume();
            if(event.isConsumed()) {
                lava.close();
                listiKoostamine();
            }
        });

        lava.setScene(stseen);
        lava.setResizable(false);
        lava.show();
    }

}
