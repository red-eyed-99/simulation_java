package main.ui.custom_controls;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ZoomableScrollPane extends ScrollPane {
    private double scaleValue = 0.7;
    private double zoomIntensity = 0.02;
    private double maxZoomValue;
    private double minZoomValue;
    private double currentZoomValue = 0;

    public Node target;
    public Node zoomNode;

    public ZoomableScrollPane() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main/ui/layouts/ZoomableScrollPane.fxml"));
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setZoomValues(double areaSize) {
        double sizeMultiplier = 2.0;

        if (areaSize >= 15 && areaSize <= 25) {
            sizeMultiplier = 1.5;
        }
        if (areaSize >= 26 && areaSize <= 30) {
            sizeMultiplier = 1.65;
        }
        if (areaSize >= 31 && areaSize <= 35) {
            sizeMultiplier = 1.8;
        }

        minZoomValue = areaSize * -scaleValue / sizeMultiplier;
        maxZoomValue = areaSize * (scaleValue / 3);
    }

    public void setZoomableTarget(Node target) {
        this.target = target;
        this.zoomNode = new Group(target);

        zoomNode.setCursor(Cursor.OPEN_HAND);

        setContent(outerNode(zoomNode));
        setPannable(true);
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setFitToHeight(true); //center
        setFitToWidth(true); //center

        updateScale();
    }

    public Node outerNode(Node node) {
        Node outerNode = centeredNode(node);

        outerNode.setOnScroll(e -> {
            if (e.isControlDown()) {
                if (e.getDeltaY() > 0) {
                    if (currentZoomValue < maxZoomValue) {
                        currentZoomValue = currentZoomValue + scaleValue;
                    }
                } else {
                    if (currentZoomValue > minZoomValue) {
                        currentZoomValue = currentZoomValue - scaleValue;
                    }
                }

                e.consume();

                if (currentZoomValue <= maxZoomValue && currentZoomValue >= minZoomValue) {
                    onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
                }
            }
        });
        outerNode.setOnDragDetected(e -> zoomNode.setCursor(Cursor.CLOSED_HAND));
        outerNode.setOnMousePressed(e -> zoomNode.setCursor(Cursor.CLOSED_HAND));
        outerNode.setOnMouseReleased(e -> zoomNode.setCursor(Cursor.OPEN_HAND));

        return outerNode;
    }

    private Node centeredNode(Node node) {
        VBox vBox = new VBox(node);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    public void updateScale() {
        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
    }

    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        updateScale();
        this.layout(); // refresh ScrollPane scroll positions & target bounds

        // convert target coordinates to zoomTarget coordinates
        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint));

        // calculate adjustment of scroll position (pixels)
        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }
}