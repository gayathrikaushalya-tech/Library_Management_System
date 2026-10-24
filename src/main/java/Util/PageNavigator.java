
    package Util;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

    public class PageNavigator {

        private final AnchorPane contentPane;

        public PageNavigator(AnchorPane contentPane) {
            this.contentPane = contentPane;
        }

        public void navigate(String pageName) {

            String path = "/View/" + pageName + ".fxml";
            URL resource = getClass().getResource(path);

            if (resource == null) {
                System.err.println(
                        "FXML page not found: " + path
                );
                return;
            }

            try {
                Parent page =
                        FXMLLoader.load(resource);

                AnchorPane.setTopAnchor(page, 0.0);
                AnchorPane.setRightAnchor(page, 0.0);
                AnchorPane.setBottomAnchor(page, 0.0);
                AnchorPane.setLeftAnchor(page, 0.0);

                contentPane.getChildren().setAll(page);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

