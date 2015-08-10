package ru.yandex.qatools.allure;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.ToolCannotBeFoundException;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.yandex.qatools.allure.AllureConstants.ISSUE_TRACKER_PATTERN;
import static ru.yandex.qatools.allure.AllureConstants.REPORT_VERSION;
import static ru.yandex.qatools.allure.AllureConstants.TMS_PATTERN;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureBuildServiceAdapter extends BuildServiceAdapter {

    public static final String BUNDLE = "allure-bundle.jar";

    public static final String ALLURE_TOOL_NAME = "allure";

    public static final String JAVA_HOME = "JAVA_HOME";

    public static final String TEAMCITY_TOOL_ALLURE = "teamcity.tool.allure";

    public static final String ALLURE_REPORT = "allure-report";

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        getLogger().message("Allure report generator runner parameters:");

        String version = getRunnerParameter(REPORT_VERSION);
        String issuesPattern = getRunnerParameter(ISSUE_TRACKER_PATTERN);
        String tmsPattern = getRunnerParameter(TMS_PATTERN);

        String reportVersion = getRunnerParameters().get(REPORT_VERSION);
        getLogger().message("Generate Allure report version " + reportVersion);

        String allureDirectory = getAllureDirectory();
        getLogger().message("Allure tool found: " + allureDirectory);

        String javaExecutable = getJavaExecutable();
        getLogger().message("Java executable found: " + javaExecutable);

        Path bundle = Paths.get(allureDirectory, BUNDLE);
        File reportDirectory = new File(getBuildTempDirectory(), ALLURE_REPORT);

        List<String> args = Arrays.asList(
                createSystemPropertyArgument(issuesPattern, ISSUE_TRACKER_PATTERN),
                createSystemPropertyArgument(tmsPattern, TMS_PATTERN),
                "-jar",
                bundle.toAbsolutePath().toString(),
                "/Users/charlie/IdeaProjects/allure-teamcity-plugin/allure-results",
                reportDirectory.getAbsolutePath()
        );

        return createProgramCommandline(javaExecutable, args);
    }

    protected String createSystemPropertyArgument(String key, String value) {
        return String.format("-D%s=%s", key, value);
    }

    protected String getRunnerParameter(String key) {
        String value = getRunnerParameters().get(key);
        getLogger().message(String.format("%s: %s", key, value));
        return value;
    }

    protected String getAllureDirectory() throws ToolCannotBeFoundException {
        if (!getConfigParameters().containsKey(TEAMCITY_TOOL_ALLURE)) {
            throw new ToolCannotBeFoundException("Could not tool by name: allure");
        }

        return getConfigParameters().get(TEAMCITY_TOOL_ALLURE);
    }

    protected String getJavaExecutable() throws RunBuildException {
        if (!getEnvironmentVariables().containsKey(JAVA_HOME)) {
            throw new RunBuildException("Could not find java installation: " +
                    "environment variable " + JAVA_HOME + " is not installed.");
        }
        String javaHome = getEnvironmentVariables().get(JAVA_HOME);
        return Paths.get(javaHome, "bin", "java").toAbsolutePath().toString();
    }
}
