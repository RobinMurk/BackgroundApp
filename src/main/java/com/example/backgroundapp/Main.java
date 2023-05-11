package com.example.backgroundapp;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
import java.util.Objects;

public class Main extends Application {
    private ListView<Pilt> tekstid = new ListView<>();
    private int laius = 500;
    private int kõrgus = 500;


    //TODO lisada faili sobivuse kontrollimise meetod
    //TODO Erind sisukamaks teha

    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage lava) {


        looPildiList(); //loeb failist sisse pildid

        VBox alus = new VBox();//alus kus kõik ülejaanud objektid asetsevad

        //ülemise vboxi sisu
        HBox ülemine = new HBox();
        Button lisa = new Button("Lisa");
        Button sätted = new Button("Sätted"); // TODO hetkel funktsioon puudub
        ülemine.getChildren().addAll(sätted, lisa);
        ülemine.setSpacing(laius-87);
        alus.getChildren().add(ülemine);

        //listviewi sisu
        VBox vb = new VBox(tekstid); //ListView<Pilt> lisamine
        tekstid.setPrefHeight(kõrgus-80);
        alus.getChildren().add(vb);

        //akna alumine osa
        HBox alumine = new HBox();
        Label valitud = new Label();

        alumine.getChildren().add(valitud);
        alus.getChildren().add(alumine);

        Scene stseen = new Scene(alus, laius, kõrgus);




        //nuppude funktsioonid:
        lisa.setOnMouseClicked(event -> dragAken()); //loob uue akna kuhu saab lohistada uusi pilte

        tekstid.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                if (event.getClickCount() == 1){//kui ühe korra vajautatakse siis kuvatakse vastav tekst
                    if (tekstid.getItems().size() > 0 && tekstid.getSelectionModel().getSelectedItem() != null)
                        valitud.setText("Valitud on: " + tekstid.getSelectionModel().getSelectedItem().getNimi() + System.lineSeparator() + "Taustapildi muutmiseks tehke topeltklõps" + System.lineSeparator() + "Eemaldamiseks vajuta 'delete'");
                }
                if (event.getClickCount()==2) {//kui tehakse topeltklikk siis muudetakse desktopi pilt valitud pidli vastu
                    if (tekstid.getItems().size() > 0 && tekstid.getSelectionModel().getSelectedItem() != null) {
                        valitud.setText("Valik '" + tekstid.getSelectionModel().getSelectedItem().getNimi() + "' on kinnitatud");
                        TasutapildiMuutja.muudatTaustakat(tekstid.getSelectionModel().getSelectedItem().getTee());
                    }
                }
            }
        }); //muudab valitud pildi arvuti uueks taustapildiks

        tekstid.setOnKeyPressed(keyEvent -> {
            int indeks = tekstid.getSelectionModel().getSelectedIndex();
            if ( keyEvent.getCode().equals( KeyCode.DELETE ) && tekstid.getSelectionModel().getSelectedItem() != null){
                tekstid.getItems().remove(indeks);
            }
        }); //delete nupu vajutamisel kui pilt on aktiveeritud siis kustutab selle ListView-st

        lava.setOnCloseRequest(event ->salvestaPildidKausta()); //programmi sulgemisel salvestab failid kausta "piltide_kaust"


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


    /**
     * Meetod võtab ListView<Pilt> ja loob kuvatava objekti mille ta kuvab stseenil.
     * @tekstid - ListView<Pilt> kus on kõik Pilt objektid mida kuvatakse.
     * @TODO muuta cell vormindus
     */
    private void listiKoostamine(){

        tekstid.setCellFactory(param -> new ListCell<Pilt>() {

            @Override
            public void updateItem(Pilt pilt, boolean tühi) {
                Text tekst;
                super.updateItem(pilt, tühi);

                if (tühi) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hb = new HBox();
                    tekst = new Text(pilt.getNimi());
                    hb.getChildren().addAll(pilt.getEelvaade(), tekst);
                    hb.setSpacing(5);
                    tekst.setTextAlignment(TextAlignment.CENTER);
                    setGraphic(hb);
                }
            }

        });
    }


    /**
     * Meetod salvestab ListView<Pilt> sisu kausta "piltide_kaust".
     *Kontrollitakse kas on kustuatud või lisatud ListView<Pilt>-sse.
     */
    private void salvestaPildidKausta(){//sulgemisel salvestab failid vajalikku kausta
        File kaust = new File(System.getProperty("user.dir") + "/piltide_kaust");

       //kontrollib kas pilte on kustutatud
        for (File fail : Objects.requireNonNull(kaust.listFiles())){
            boolean kasEemaldati = false; //kontroll kas vahepeal kustutati ListView<Pilt>-st element

            for (Pilt pilt : tekstid.getItems()) {
                if (fail.getAbsolutePath().equals(pilt.getTee())) {
                    kasEemaldati = true;
                    break;
                }
            }
            if(!kasEemaldati) {
                fail.delete();//kui tsükkel käidi läbi ilma if lauset täitmata siis kustutatakse pilt kaustast
            }
        }

        //kas on pilte lisatud
        for (Pilt p : tekstid.getItems()) {
            boolean kasOnOlemas = false; //kontroll, kas faile lisati juurde vahepeal
            File[] kaustasFailid = kaust.listFiles();//piltide kaustas olevad pildifailid
            assert kaustasFailid != null; //eeldus et ei ole tühi
            for(File fail : kaustasFailid){
                if(fail.getAbsolutePath().equals(p.getTee())) {
                    kasOnOlemas = true;//kui on olemas siis pole vaja salvestada
                    break;
                }

            }

            //kui pilti ei olnud kaustas siis salvestatakse kausta
            if(!kasOnOlemas) {
                File pilt = new File(p.getTee());
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
            }
        }//for
    }//salvestaPiltKausta


    /**
     * Loeb kaustast "piltide_kaust" sisse kõik pildid ListVieW<Pilt> objekti
     * kui sisse loetav fail ei ole .jpg või .png formaadis siis viskab EbasobivaFailiErind
     * lõpus kutsub välja meetodi listiKoostamine()
     */
    private void looPildiList(){
        File kaust1 = new File(System.getProperty("user.dir") + "/piltide_kaust");
        File[] failid = kaust1.listFiles();
        if (failid != null){//kui failid on tühi pole vaja midagi teha
            for (File fail : failid) {
                if (fail.isFile()) {
                    Pilt pilt = new Pilt(fail.getAbsolutePath(), fail.getName().substring(0, fail.getName().lastIndexOf('.')));
                    try {
                        String nimi = fail.getName();
                        if (!nimi.substring(nimi.lastIndexOf('.')).equals(".jpg") && !nimi.substring(nimi.lastIndexOf('.')).equals(".png")){
                            throw new EbasobivaFailiErind("Kaustas oli vales formaadis fail");
                        }
                        tekstid.getItems().add(pilt);
                    }catch (EbasobivaFailiErind ignored){
                    }
                }
            }
        }

        listiKoostamine();
    }

    /**
     *Luuakse uus aken, kuhu saab lohistada soovitava faili, et see ListView<Pilt>
     *objekti lisada. Fail peab juba olema arvuti mälus. Ei saa veel netist otse lohistada.
     * kui fail on vale vorminguga (ei ole .png või .jpg) viskab EbasobivaFailiErind.
     * Kutsub lõpus välja meetodi listiKoostamine().
     * @TODO lisada funktsioon et saaks otse netist tõmmata
     */
    private void dragAken(){
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
                    Pilt pilt = new Pilt(failitee, nimi);

                    tekstid.getItems().add(pilt);//

                }//try
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
