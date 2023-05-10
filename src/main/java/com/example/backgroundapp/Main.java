package com.example.backgroundapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * tekstid - ListView piltide kirjelduste jaoks
 * data - sisaldab piltide nimesid selleks, et nad ListViewi lisada
 * pildid - piltide massiiv
 * valitudListist - kui topeltklikkitakse siis saab kasutada, et taustapilt ära vahetada
 * laius, kõrgus - on esialgsed akna parameetrid
 */
public class Main extends Application {
    ListView<String> tekstid = new ListView<>();
    ObservableList<String> nimed = FXCollections.observableArrayList();
    ImageView[] pildid = new ImageView[0];
    List<String> teeNimed = new ArrayList<>();

    int laius = 500;
    int kõrgus = 500;


    public static void main(String[] args) {
        launch();
    }

    /**
     * Kuvatakse ListView list graafiliselt
     */
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
                        if (nimed.get(i).equals(tekst)) {
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
    public void start(Stage lava) throws EbasobivaFailiErind {
        //kui juba on salvestatud kausta pilte või on varem kasutatud, siis kuvatakse need kohe
        List<Pilt> ajutinePildid = new ArrayList<>();// ajutine on selleks, et kätte saada vajalik suurus pildid imageview massiivi jaoks, kuhu kuuluksid ainult õiges formaadis failid
        File kaust1 = new File(System.getProperty("user.dir") + "/piltide_kaust");
        File[] failid = kaust1.listFiles();
        if (failid != null){//kui failid on tühi pole vaja midagi teha
            for (File fail : failid) {
                if (fail.isFile()) {
                    Pilt pilt = new Pilt(fail.getAbsolutePath(), fail.getName());
                    try {
                        String nimi = fail.getName();
                        if (!nimi.substring(nimi.lastIndexOf('.')).equals(".jpg") && !nimi.substring(nimi.lastIndexOf('.')).equals(".png")){
                            throw new EbasobivaFailiErind("Kaustas oli vales formaadis fail");
                        }
                        nimed.add(fail.getName().substring(0, fail.getName().lastIndexOf(".")));
                        teeNimed.add(fail.getAbsolutePath());
                        ajutinePildid.add(pilt);
                    }catch (EbasobivaFailiErind e){

                    }

                }
            }
            tekstid.setItems(nimed);//kõik datas olnud nimed pannakse listview objekti
            pildid = new ImageView[ajutinePildid.size()];
            for (int i = 0; i < pildid.length; i++) {
                pildid[i] = ajutinePildid.get(i).getEelvaade();
            }
        }

        listiKoostamine();//koostab imageview listi

        VBox alus = new VBox();//alus kus kõik ülejaanud objektid asetsevad

        //ülemise vboxi sisu
        HBox ülemine = new HBox();
        Button lisa = new Button("Lisa");
        Button sätted = new Button("Sätted");
        ülemine.getChildren().addAll(sätted, lisa);
        ülemine.setSpacing(laius-87);
        alus.getChildren().add(ülemine);
        //nuppude funktsioonid:
        lisa.setOnMouseClicked(event -> dragAken());

        //listviewi sisu
        VBox vb = new VBox(tekstid);
        tekstid.setPrefHeight(kõrgus-80);
        alus.getChildren().add(vb);
        //akna alumine osa
        HBox alumine = new HBox();
        Label valitud = new Label();

        tekstid.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                if (event.getClickCount() == 1){//kui ühe korra vajautatakse siis kuvatakse vastav tekst
                    valitud.setText("Valitud on: " + tekstid.getSelectionModel().getSelectedItem() + System.lineSeparator() + "Taustapildi muutmiseks tehke topeltklõps" + System.lineSeparator() + "Eemaldamiseks vajuta 'delete'");
                }
                if (event.getClickCount()==2) {//kui tehakse topeltklikk siis muudetakse desktopi pilt valitud pidli vastu
                    valitud.setText("Valik '" + tekstid.getSelectionModel().getSelectedItem() + "' on kinnitatud");
                    for (int i = 0; i < nimed.size(); i++) {
                        if (nimed.get(i).equals(tekstid.getSelectionModel().getSelectedItem())){
                            TasutapildiMuutja.muudatTaustakat(teeNimed.get(i));
                        }
                    }

                }
            }
        });
        tekstid.setOnKeyPressed( new EventHandler<KeyEvent>()
        {
            @Override
            public void handle( final KeyEvent keyEvent )
            {
                int indeks = tekstid.getSelectionModel().getSelectedIndex();
                if ( keyEvent.getCode().equals( KeyCode.DELETE ) ){//kui listist on valitud objekt ja delete vajutatakse siis kustutatakse see ära
                    tekstid.getItems().remove(indeks);// kustutuab automaatselt data listist ära
                    teeNimed.remove(indeks);
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

        lava.setOnCloseRequest(event ->{//sulgemisel salvestab failid vajalikku kausta
            File kaust = new File(System.getProperty("user.dir") + "/piltide_kaust");
            for (File pilt : Objects.requireNonNull(kaust.listFiles())){
                boolean kasEemaldati = false; //kontroll kas vahepeal kustutati fail listist
                for (int i = 0; i < teeNimed.size(); i++) {
                    if (teeNimed.get(i) != null) {
                        if (pilt.getName().equals(nimed.get(i) + teeNimed.get(i).substring(teeNimed.get(i).lastIndexOf('.')))) {
                            kasEemaldati = true;
                            break;
                        }
                    }
                }
                if(!kasEemaldati) {
                    pilt.delete();//kui tsükkel käidi läbi ilma if lauset täitmata siis kustutatakse pilt kaustast
                }
            }

            for (String pilditee : teeNimed) {
                boolean kasOnOlemas = false; //kontroll, kas faile lisati juurde vahepeal
                File[] kaustasFailid = kaust.listFiles();//piltide kaustas olevad pildifailid
                for(File fail : kaustasFailid){
                    if(fail.getAbsolutePath().equals(pilditee)) kasOnOlemas = true;//kui on olemas siis pole vaja salvestada
                }
                if(!kasOnOlemas) {//kui polnud sii salvestatakse fail kausta
                    File pilt = new File(pilditee);
                    try (InputStream in = new FileInputStream(pilt);
                         OutputStream out = new FileOutputStream(System.getProperty("user.dir") + "/piltide_kaust/" + pilt.getName())) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else System.out.println(pilditee);
            }

        });

        lava.setScene(stseen);
        lava.setResizable(true);
        lava.show();
    }

    /**
     *Luuakse uus aken, kuhu saab lohistada soovitava faili, et see valikusse lisada
     */
    public void dragAken(){
        Stage lava = new Stage();//uus aken
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
            try {
                if (db.hasFiles()) {
                    String failitee = db.getFiles().get(0).getAbsolutePath();
                    String nimi = db.getFiles().get(0).getName();
                    if (!nimi.substring(nimi.lastIndexOf('.')).equals(".jpg") && !nimi.substring(nimi.lastIndexOf('.')).equals(".png")) {//kontroll, kas on sobiv fail
                        tekst.setText("Vale faili formaat!" + System.lineSeparator() + "Lohista .jpg või .png pildifail siia");// kui pole siis visatakse erind ja kuvatakse ka ekraanil
                        tekst.layoutXProperty().bind(stseen.widthProperty().subtract(tekst.prefWidth(-1)).divide(2));
                        tekst.layoutYProperty().bind(stseen.heightProperty().subtract(tekst.prefHeight(-1)).divide(2));
                        tekst.setTextOrigin(VPos.TOP);
                        tekst.setTextAlignment(TextAlignment.CENTER);
                        throw new EbasobivaFailiErind("Vale faili formaat!");
                    }
                    nimi = nimi.substring(0, nimi.lastIndexOf('.'));
                    kas = true;
                    teeNimed.add(failitee);
                    Pilt pilt = new Pilt(failitee, nimi);
                    ImageView[] pildidAjutine = new ImageView[pildid.length + 1];//ajutine massiiv, et pildid massiivi uuendada

                    for (int i = 0; i < pildid.length; i++) {
                        pildidAjutine[i] = pildid[i];
                    }

                    pildidAjutine[pildidAjutine.length - 1] = pilt.getEelvaade();
                    pildid = pildidAjutine;//draggitud pilt lisatud piltide hulka, et ta nüüd listi panna
                    nimed.add(nimi);
                    tekstid.setItems(nimed);//uuendame ka data listi ja uuendame tekstid listi
                }
                event.setDropCompleted(kas);
                event.consume();
                if (event.isConsumed()) {
                    lava.close();
                    listiKoostamine();//kuvame graafiliselt uue listi
                }
            }catch (EbasobivaFailiErind ignored){
            }
        });
        lava.setScene(stseen);
        lava.setResizable(false);
        lava.show();
    }
}
