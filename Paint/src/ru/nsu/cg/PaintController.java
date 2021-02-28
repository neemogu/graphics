package ru.nsu.cg;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import ru.nsu.cg.gui.PaintView;

import java.io.File;
import java.util.function.Consumer;

public class PaintController {
    private static final long minMouseDragDelta = 20000000;
    private static long lastTime = 0;
    public static void linkModelAndView(PaintModel model, PaintView view) {
        model.addSubscriber(view);
        setMainHandlers(model, view);
        setMouseHandlers(model, view);
        setSettingsWindowHandlers(model, view);
        view.update();
    }

    private static void setMouseHandlers(PaintModel model, PaintView view) {
        view.setMousePressedHandler(event -> model.setFirstCoordinates(event.getX(), event.getY()));
        view.setMouseDraggedHandler(event -> {
            long currentTime = System.nanoTime();
            if (currentTime - lastTime < minMouseDragDelta) {
                return;
            }
            lastTime = currentTime;
            model.setTemporarySecondCoordinates(event.getX(), event.getY());
        });
        view.setMouseReleasedHandler(event -> model.setSecondCoordinates(event.getX(), event.getY()));
    }

    private static void setMainHandlers(PaintModel model, PaintView view) {
        view.setLineToolHandler(event -> model.getSettings().setMode(PaintMode.LINE));
        view.setFillToolHandler(event -> model.getSettings().setMode(PaintMode.FILL));
        view.setStampToolHandler(event -> model.getSettings().setMode(PaintMode.STAMP));
        view.setClearHandler(event -> model.clear());
        view.setAboutHandler(event -> view.showAboutWindow());
        view.setColorPickerHandler(event -> {
            if (event.getSource() instanceof ColorPicker) {
                model.getSettings().setColor(((ColorPicker) event.getSource()).getValue());
            }
        });
        view.setOpenToolSettingsWindowHandler(event -> {
            model.getSettings().saveValues();
            view.showToolSettingsWindow();
        });
        view.setSaveFileHandler(event -> {
            File toSave = view.showSaveDialog();
            if (!model.saveImage(toSave)) {
                view.showError("Can't save file " + toSave.getName());
            }
        });
        view.setOpenFileHandler(event -> {
            File toOpen = view.showOpenDialog();
            if (!model.openImage(toOpen)) {
                view.showError("Can't open file " + toOpen.getName());
            }
        });
    }

    private static void setSettingsWindowHandlers(PaintModel model, PaintView view) {
        view.setOkButtonToolSettingsWindowHandler(event -> {
            view.closeToolSettingsWindow();
            model.resizeImage();
        });
        view.setCancelButtonToolSettingsWindowHandler(event -> {
            view.closeToolSettingsWindow();
            model.getSettings().recoverSavedValues();
            model.resetNewSize();
        });
        view.setIsStarStampButtonHandler(event -> {
            if (event.getSource() instanceof CheckBox) {
                model.getSettings().setStarStamp(((CheckBox) event.getSource()).isSelected());
            }
        });
        setLineThicknessSettingHandlers(model, view);
        setStampVertexNumSettingHandlers(model, view);
        setStampSizeSettingHandlers(model, view);
        setStampRotationSettingHandlers(model, view);
        setAreaWidthSettingHandler(model, view);
        setAreaHeightSettingHandler(model, view);
    }

    private static void setLineThicknessSettingHandlers(PaintModel model, PaintView view) {
        Consumer<ActionEvent> onTextFieldAction = event -> {
            if (!(event.getSource() instanceof TextField)) {
                return;
            }
            TextField field = (TextField)(event.getSource());
            int value;
            try {
                value = getIntegerFromTextField(field.getText(), 1, PaintSettings.maxThickness,
                        "line thickness", view);
            } catch (InvalidSettingValueException e) {
                return;
            }
            model.getSettings().setLineThickness(value);
        };
        view.setLineThicknessSettingHandlers((ov, oldValue, newValue) -> {
            model.getSettings().setLineThickness(newValue.intValue());
        }, onTextFieldAction::accept, (ov, oldValue, newValue) -> {
            if (!newValue) {
                if (ov instanceof ReadOnlyBooleanProperty) {
                    onTextFieldAction.accept(new ActionEvent(((ReadOnlyBooleanProperty) ov).getBean(), null));
                }
            }
        });
    }

    private static void setStampVertexNumSettingHandlers(PaintModel model, PaintView view) {
        Consumer<ActionEvent> onTextFieldAction = event -> {
            if (!(event.getSource() instanceof TextField)) {
                return;
            }
            TextField field = (TextField)(event.getSource());
            int value;
            try {
                value = getIntegerFromTextField(field.getText(), PaintSettings.minStampVertexNum, PaintSettings.maxStampVertexNum,
                        "stamp vertex num", view);
            } catch (InvalidSettingValueException e) {
                return;
            }
            model.getSettings().setStampVertexNum(value);
        };
        view.setStampVertexNumSettingHandlers((ov, oldValue, newValue) -> {
            model.getSettings().setStampVertexNum(newValue.intValue());
        }, onTextFieldAction::accept, (ov, oldValue, newValue) -> {
            if (!newValue) {
                if (ov instanceof ReadOnlyBooleanProperty) {
                    onTextFieldAction.accept(new ActionEvent(((ReadOnlyBooleanProperty) ov).getBean(), null));
                }
            }
        });
    }

    private static void setStampSizeSettingHandlers(PaintModel model, PaintView view) {
        Consumer<ActionEvent> onTextFieldAction = event -> {
            if (!(event.getSource() instanceof TextField)) {
                return;
            }
            TextField field = (TextField)(event.getSource());
            int value;
            try {
                value = getIntegerFromTextField(field.getText(), PaintSettings.minStampSize, PaintSettings.maxStampSize,
                        "stamp size", view);
            } catch (InvalidSettingValueException e) {
                return;
            }
            model.getSettings().setStampSize(value);
        };
        view.setStampSizeSettingHandlers((ov, oldValue, newValue) -> {
            model.getSettings().setStampSize(newValue.intValue());
        }, onTextFieldAction::accept, (ov, oldValue, newValue) -> {
            if (!newValue) {
                if (ov instanceof ReadOnlyBooleanProperty) {
                    onTextFieldAction.accept(new ActionEvent(((ReadOnlyBooleanProperty) ov).getBean(), null));
                }
            }
        });
    }

    private static void setStampRotationSettingHandlers(PaintModel model, PaintView view) {
        Consumer<ActionEvent> onTextFieldAction = event -> {
            if (!(event.getSource() instanceof TextField)) {
                return;
            }
            TextField field = (TextField)(event.getSource());
            int value;
            try {
                value = getIntegerFromTextField(field.getText(), "stamp rotation", view);
            } catch (InvalidSettingValueException e) {
                return;
            }
            model.getSettings().setStampRotation(value);
        };
        view.setStampRotationSettingHandlers((ov, oldValue, newValue) -> {
            model.getSettings().setStampRotation(newValue.intValue());
        }, onTextFieldAction::accept, (ov, oldValue, newValue) -> {
            if (!newValue) {
                if (ov instanceof ReadOnlyBooleanProperty) {
                    onTextFieldAction.accept(new ActionEvent(((ReadOnlyBooleanProperty) ov).getBean(), null));
                }
            }
        });
    }

    private static void setAreaWidthSettingHandler(PaintModel model, PaintView view) {
        Consumer<ActionEvent> onTextFieldAction = event -> {
            if (!(event.getSource() instanceof TextField)) {
                return;
            }
            TextField field = (TextField)(event.getSource());
            int value;
            try {
                value = getIntegerFromTextField(field.getText(), PaintModel.minDimensionSize, PaintModel.maxDimensionSize,
                        "workspace width", view);
            } catch (InvalidSettingValueException e) {
                return;
            }
            model.setNewWidth(value);
        };
        view.setAreaWidthSettingHandler(onTextFieldAction::accept, (ov, oldValue, newValue) -> {
            if (!newValue) {
                if (ov instanceof ReadOnlyBooleanProperty) {
                    onTextFieldAction.accept(new ActionEvent(((ReadOnlyBooleanProperty) ov).getBean(), null));
                }
            }
        });
    }

    private static void setAreaHeightSettingHandler(PaintModel model, PaintView view) {
        Consumer<ActionEvent> onTextFieldAction = event -> {
            if (!(event.getSource() instanceof TextField)) {
                return;
            }
            TextField field = (TextField)(event.getSource());
            int value;
            try {
                value = getIntegerFromTextField(field.getText(), PaintModel.minDimensionSize, PaintModel.maxDimensionSize,
                        "workspace height", view);
            } catch (InvalidSettingValueException e) {
                return;
            }
            model.setNewHeight(value);
        };

        view.setAreaHeightSettingHandler(onTextFieldAction::accept, (ov, oldValue, newValue) -> {
            if (!newValue) {
                if (ov instanceof ReadOnlyBooleanProperty) {
                    onTextFieldAction.accept(new ActionEvent(((ReadOnlyBooleanProperty) ov).getBean(), null));
                }
            }
        });
    }

    private static int getIntegerFromTextField(String strInt, int lowerBound, int upperBound, String settingName, PaintView view) throws InvalidSettingValueException {
        int result;
        try {
            result = Integer.parseInt(strInt);
            if (result < lowerBound || result > upperBound) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            view.showError("Invalid " + settingName + " value, must be between " + lowerBound + " and " + upperBound);
            view.update();
            throw new InvalidSettingValueException();
        }
        return result;
    }

    private static int getIntegerFromTextField(String strInt, String settingName, PaintView view) throws InvalidSettingValueException {
        try {
            return Integer.parseInt(strInt);
        } catch (NumberFormatException e) {
            view.showError("Invalid " + settingName + " value");
            view.update();
            throw new InvalidSettingValueException();
        }
    }

    private static class InvalidSettingValueException extends Exception {}
}
