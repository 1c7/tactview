package com.helospark.tactview.ui.javafx.uicomponents.propertyvalue;

import java.io.File;

import com.helospark.lightdi.annotation.Component;
import com.helospark.tactview.core.timeline.TimelinePosition;
import com.helospark.tactview.core.timeline.effect.EffectParametersRepository;
import com.helospark.tactview.core.timeline.effect.interpolation.ValueProviderDescriptor;
import com.helospark.tactview.core.timeline.effect.interpolation.provider.FileProvider;
import com.helospark.tactview.core.timeline.message.KeyframeAddedRequest;
import com.helospark.tactview.ui.javafx.JavaFXUiMain;
import com.helospark.tactview.ui.javafx.UiCommandInterpreterService;
import com.helospark.tactview.ui.javafx.UiTimelineManager;
import com.helospark.tactview.ui.javafx.commands.impl.AddKeyframeForPropertyCommand;
import com.helospark.tactview.ui.javafx.uicomponents.propertyvalue.contextmenu.ContextMenuAppender;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
public class FilePropertyValueSetterChainItem extends TypeBasedPropertyValueSetterChainItem<FileProvider> {
    private UiCommandInterpreterService commandInterpreter;
    private EffectParametersRepository effectParametersRepository;
    private UiTimelineManager uiTimelineManager;
    private ContextMenuAppender contextMenuAppender;

    public FilePropertyValueSetterChainItem(EffectParametersRepository effectParametersRepository,
            UiCommandInterpreterService commandInterpreter, UiTimelineManager uiTimelineManager, ContextMenuAppender contextMenuAppender) {
        super(FileProvider.class);
        this.commandInterpreter = commandInterpreter;
        this.effectParametersRepository = effectParametersRepository;
        this.uiTimelineManager = uiTimelineManager;
        this.contextMenuAppender = contextMenuAppender;
    }

    @Override
    protected EffectLine handle(FileProvider fileProvider, ValueProviderDescriptor descriptor) {
        TextField textArea = new TextField();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter(fileProvider.getExtension(), new String[]{fileProvider.getExtension()}));
        fileChooser.setTitle("Open Resource File");
        Button browseButton = new Button("Browse");

        HBox hbox = new HBox();
        hbox.getChildren().addAll(textArea, browseButton);

        PrimitiveEffectLine lineItem = PrimitiveEffectLine.builder()
                .withCurrentValueProvider(() -> textArea.getText())
                .withDescriptorId(fileProvider.getId())
                .withUpdateFunction(position -> {
                    String fileName = providerValueToString(fileProvider.getId(), position);
                    textArea.setText(fileName);
                    fileChooser.setInitialFileName(fileName);
                })
                .withVisibleNode(hbox)
                .withCommandInterpreter(commandInterpreter)
                .withEffectParametersRepository(effectParametersRepository)
                .withDescriptor(descriptor)
                .withUpdateFromValue(value -> {
                    textArea.setText((String) value);
                    fileChooser.setInitialFileName((String) value);
                })
                .build();
        browseButton.setOnMouseClicked(e -> {
            File file = fileChooser.showOpenDialog(JavaFXUiMain.STAGE);
            if (file != null) {
                KeyframeAddedRequest keyframeRequest = KeyframeAddedRequest.builder()
                        .withDescriptorId(fileProvider.getId())
                        .withGlobalTimelinePosition(uiTimelineManager.getCurrentPosition())
                        .withValue(file.getAbsolutePath())
                        .withRevertable(true)
                        .build();

                commandInterpreter.sendWithResult(new AddKeyframeForPropertyCommand(effectParametersRepository, keyframeRequest));
            }
        });
        contextMenuAppender.addContextMenu(lineItem, fileProvider, descriptor, hbox);

        return lineItem;
    }

    private String providerValueToString(String id, TimelinePosition position) {
        return effectParametersRepository.getValueAt(id, position);
    }

}
