package ru.yandex.qatools.allure.teamcity;

import jetbrains.buildServer.agent.AgentBuildFeature;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.messages.DefaultMessagesInfo;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import ru.yandex.qatools.allure.report.AllureReportBuilder;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class AgentBuildEventsProvider extends AgentLifeCycleAdapter {

    public static final String ALLURE_ACTIVITY_NAME = "Allure report generation";

    public static final ClassLoader PARENT = createParent();

    private final ArtifactsWatcher artifactsWatcher;

    public AgentBuildEventsProvider(@NotNull final EventDispatcher<AgentLifeCycleListener> dispatcher,
                                    @NotNull final ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
        dispatcher.addListener(this);
    }

    @Override
    public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
        super.buildStarted(runningBuild);
        runningBuild.getBuildLogger();
    }

    @Override
    public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
        super.beforeBuildFinish(build, buildStatus);
        BuildProgressLogger logger = build.getBuildLogger();
        logger.activityStarted(ALLURE_ACTIVITY_NAME, DefaultMessagesInfo.BLOCK_TYPE_BUILD_STEP);
        try {
            AgentBuildFeature buildFeature = getAllureBuildFeature(build);
            if (buildFeature == null) {
                return;
            }

            AllureReportConfig config = new AllureReportConfig(buildFeature.getParameters());
            ReportBuildPolicy reportBuildPolicy = config.getReportBuildPolicy();

            if (!isNeedToBuildReport(reportBuildPolicy, buildStatus)) {
                logger.message("Build was interrupted. Skipping Allure report generation.");
                return;
            }

            File checkoutDirectory = build.getCheckoutDirectory();
            String resultsPattern = config.getResultsPattern();
            logger.message(String.format("analyse results pattern %s", resultsPattern));

            File[] allureResultDirectoryList = FileUtils.findFilesByGlob(checkoutDirectory, resultsPattern);
            logger.message(String.format("analyse results directories %s",
                    Arrays.toString(allureResultDirectoryList)));

            File tempDirectory = build.getAgentTempDirectory();
            File allureReportDirectory = new File(tempDirectory, AllureReportConfig.REPORT_PATH);
            logger.message(String.format("prepare allure report directory [%s]",
                    allureReportDirectory.getAbsolutePath()));

            String version = config.getReportVersion();

            logger.message(String.format("prepare report generator with version: %s", version));

            AllureReportBuilder builder = new AllureReportBuilder(version, allureReportDirectory);
            builder.setClassLoader(PARENT);

            logger.message(String.format("process tests results to directory [%s]",
                    allureReportDirectory.getAbsolutePath()));
            builder.processResults(allureResultDirectoryList);

            logger.message(String.format("unpack report face to directory [%s]",
                    allureReportDirectory.getAbsolutePath()));
            builder.unpackFace();

            artifactsWatcher.addNewArtifactsPath(allureReportDirectory.getAbsolutePath());
        } catch (Throwable e) {
            build.getBuildLogger().exception(e);
        } finally {
            logger.activityFinished(ALLURE_ACTIVITY_NAME, DefaultMessagesInfo.BLOCK_TYPE_BUILD_STEP);
        }
    }

    private boolean isNeedToBuildReport(ReportBuildPolicy policy, BuildFinishedStatus status) {
        switch (policy) {
            case ALWAYS: {
                return true;
            }

            case WITH_PROBLEMS: {
                return status.equals(BuildFinishedStatus.FINISHED_WITH_PROBLEMS);
            }

            case FAILED: {
                return status.equals(BuildFinishedStatus.FINISHED_FAILED);
            }

            default: {
                return false;
            }
        }
    }

    private AgentBuildFeature getAllureBuildFeature(final AgentRunningBuild runningBuild) {
        for (final AgentBuildFeature buildFeature : runningBuild.getBuildFeatures()) {
            if (AllureReportConfig.BUILD_FEATURE_KEY.equals(buildFeature.getType())) {
                return buildFeature;
            }
        }
        return null;
    }

    private static ClassLoader createParent() {
        return new URLClassLoader(new URL[]{
                AgentBuildEventsProvider.class.getClassLoader().getResource("groovy-all-2.4.3-indy.jar")
        }, null);
    }
}
