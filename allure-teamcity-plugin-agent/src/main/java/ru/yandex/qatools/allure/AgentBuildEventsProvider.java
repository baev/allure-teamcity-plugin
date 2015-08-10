package ru.yandex.qatools.allure;

import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 10.08.15
 */
public class AgentBuildEventsProvider extends AgentLifeCycleAdapter {

    private final ArtifactsWatcher artifactsWatcher;

    public AgentBuildEventsProvider(@NotNull final EventDispatcher<AgentLifeCycleListener> dispatcher,
                                    @NotNull final ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
        dispatcher.addListener(this);
    }

    @Override
    public void runnerFinished(@NotNull BuildRunnerContext runner, @NotNull BuildFinishedStatus status) {
        if (!AllureConstants.RUN_TYPE.equals(runner.getRunType())) {
            return;
        }

        File reportDirectory = new File(runner.getBuild().getBuildTempDirectory(), "allure-report");
        artifactsWatcher.addNewArtifactsPath(reportDirectory.getAbsolutePath());
    }
}
