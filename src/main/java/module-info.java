module leaguesim {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.yaml.snakeyaml;
    requires MaterialFX;

    opens com.hitchhikerprod.league to javafx.fxml;
    exports com.hitchhikerprod.league;
}