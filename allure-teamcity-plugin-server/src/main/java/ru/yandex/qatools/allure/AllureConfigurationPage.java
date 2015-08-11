package ru.yandex.qatools.allure;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 11.08.15
 */
public class AllureConfigurationPage extends AdminPage {

    protected final PluginDescriptor descriptor;

    public AllureConfigurationPage(@NotNull PagePlaces pagePlaces, @NotNull PluginDescriptor descriptor) {
        super(pagePlaces);
        this.descriptor = descriptor;
        setPluginName("allureConfiguration");
        setIncludeUrl(descriptor.getPluginResourcesPath("configuration.jsp"));
        setTabTitle("Allure");
        setPosition(PositionConstraint.first());
        register();
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
        model.put(AllureConstants.ALLURE_ADMIN_VERSIONS, new Info(getInstalledVersions()));
    }

    @NotNull
    @Override
    public String getGroup() {
        return INTEGRATIONS_GROUP;
    }

    protected Path getAllureToolDirectory() {
        return Paths.get(descriptor.getPluginRoot().getParentFile().getParent(), ".tools", "allure");
    }

    protected List<String> getInstalledVersions() {
        Path allureDirectory = getAllureToolDirectory();

        List<String> results = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(allureDirectory, "*.jar")) {
            for (Path child : stream) {
                results.add(child.getFileName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }
}
