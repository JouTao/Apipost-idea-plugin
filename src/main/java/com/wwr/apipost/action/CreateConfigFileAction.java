package com.wwr.apipost.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.wwr.apipost.config.DefaultConstants;
import com.wwr.apipost.handle.apipost.config.ApiPostSettingsDialog;
import com.wwr.apipost.parse.util.NotificationUtils;
import com.wwr.apipost.util.FileUtilsExt;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 创建配置文件的通知动作
 */
public class CreateConfigFileAction extends NotificationAction {

    private final Project project;
    private final Module module;

    private static final String TEMPLATE_FILE = "config_template.properties";

    public CreateConfigFileAction(Project project, Module module, String text) {
        super(text);
        checkNotNull(project);
        checkNotNull(module);
        this.project = project;
        this.module = module;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event, @NotNull Notification notification) {
        // 参数校验
        File moduleRoot = new File(module.getModuleFilePath()).getParentFile();
        String rootPath = moduleRoot.getPath();
        String cachePath = ".idea";
        if (!rootPath.contains(cachePath)) {
            rootPath = rootPath + "/" + cachePath;
        }
        File file = Paths.get(rootPath, DefaultConstants.DEFAULT_PROPERTY_FILE_CACHE).toFile();
        try {
            String content = FileUtilsExt.readTextInResource(TEMPLATE_FILE);
            FileUtilsExt.writeText(file, content);
        } catch (IOException ex) {
            NotificationUtils.notifyError("Create config file error: " + ex.getMessage());
        }
        finally {
            Project project = event.getData(CommonDataKeys.PROJECT);
            ApiPostSettingsDialog.show(project, event.getPresentation().getText());
        }
    }
}
