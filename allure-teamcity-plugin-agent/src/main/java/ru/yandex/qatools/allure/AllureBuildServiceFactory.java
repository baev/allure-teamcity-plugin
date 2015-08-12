package ru.yandex.qatools.allure;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureBuildServiceFactory implements CommandLineBuildServiceFactory {

    private final ArtifactsWatcher artifactsWatcher;

    public AllureBuildServiceFactory(@NotNull final ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

    @NotNull
    @Override
    public CommandLineBuildService createService() {
        return new AllureBuildServiceAdapter(artifactsWatcher);
    }

    @NotNull
    @Override
    public AgentBuildRunnerInfo getBuildRunnerInfo() {
        return new AllureBuildRunnerInfo();
    }
}
