package seedu.address.ui;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class FzfModule {

    private Supplier<List<String>> optionSupplier;
    private boolean isFzfMode = false;
    private int fzfStartPos; // caret position where fzf was triggered
    private double menuX;
    private double menuY;
    private String theme;

    private TextField query = new TextField() {
        {
            setSkin(createDefaultSkin());
        }
    };
    private TextField attachedTextField;
    private ContextMenu menu;

    private FzfModule(TextField tf, Supplier<List<String>> supplier, String theme) {
        this.optionSupplier = supplier;
        this.attachedTextField = tf;
        this.theme = theme;
        menu = new ContextMenu();

        setupFzf();
    }

    public static FzfModule attachTo(TextField tf, Supplier<List<String>> supplier, String theme) {
        return new FzfModule(tf, supplier, theme);
    }

    private void setupFzf() {
        setupExitPoints();
        setupEntryPoints();
        setupMainLogic();

    }

    private void setupMainLogic() {
        attachedTextField.addEventFilter(KeyEvent.ANY, (event) -> {
            if (isFzfMode) {
                if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP) {
                    menu.fireEvent(event.copyFor(menu, menu));
                    event.consume();
                    return;
                }

                // Redirect Event to query TextField
                query.fireEvent(event.copyFor(query, query));
                refreshMenu(query.getText());

                if (menu.getItems().isEmpty()) {
                    menu.hide();
                }
                if (!menu.isShowing()) {
                    menu.show(attachedTextField, menuX, menuY);
                }
            }
        });
    }
    private void setupEntryPoints() {
        attachedTextField.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.isControlDown() && event.getCode() == KeyCode.SPACE) {
                TextInputControl control = (TextInputControl) event.getSource();
                Point2D pos = control.getInputMethodRequests().getTextLocation(0);

                toggleFzfOff();
                toggleFzfOn(control.getCaretPosition(), pos.getX(), pos.getY());
            }
        });
    }
    private void setupExitPoints() {
        attachedTextField.caretPositionProperty().addListener((unused1, unused2, newPosition) -> {
            if (newPosition.intValue() < fzfStartPos
                    || newPosition.intValue() > fzfStartPos + query.getText().length()) {
                toggleFzfOff();
            }
        });
        attachedTextField.addEventFilter(KeyEvent.ANY, (event) -> {
            if (isFzfMode && event.getCode() == KeyCode.ESCAPE) {
                toggleFzfOff();
            }
            if (!isFzfMode) {
                menu.hide();
            }
        });
    }

    private void refreshMenu(String queryText) {
        menu.getItems().clear();
        menu.getItems().addAll(optionSupplier
                .get()
                .stream()
                .filter(x -> x.toLowerCase().contains(queryText.toLowerCase()))
                .map(x -> buildMenuItem(x, queryText))
                .collect(Collectors.toList())
        );
    }
    private MenuItem buildMenuItem(String option, String query) {
        MenuItem item = new MenuItem("", buildTextFlow(option, query));
        item.setOnAction(e -> {
            attachedTextField.replaceText(fzfStartPos, fzfStartPos + this.query.getText().length(), option);
            toggleFzfOff();
        });
        return item;
    }
    private TextFlow buildTextFlow(String text, String query) {
        Color color;
        switch (theme) {
        case "LightTheme.css":
            color = Color.BLACK;
            break;
        case "DarkTheme.css":
            color = Color.WHITE;
            break;
        default:
            assert false : theme;
            color = Color.BLACK;
        }

        if (query.length() == 0) {
            Text t = new Text(text);
            t.setFill(color);
            return new TextFlow(t);
        }
        int filterIndex = text.toLowerCase().indexOf(query.toLowerCase());

        Text textBefore = new Text(text.substring(0, filterIndex));
        Text textAfter = new Text(text.substring(filterIndex + query.length()));
        Text textFilter = new Text(text.substring(filterIndex, filterIndex + query.length()));


        textBefore.setFill(color);
        textAfter.setFill(color);
        textFilter.setFill(Color.ORANGE);

        return new TextFlow(textBefore, textFilter, textAfter);
    }
    private void toggleFzfOn(int caretPos, double x, double y) {
        if (!isFzfMode) {
            fzfStartPos = caretPos;
            isFzfMode = true;
            menuX = x;
            menuY = y;
        }
    }
    private void toggleFzfOff() {
        if (isFzfMode) {
            isFzfMode = false;
            query.setText("");
        }
    }
}