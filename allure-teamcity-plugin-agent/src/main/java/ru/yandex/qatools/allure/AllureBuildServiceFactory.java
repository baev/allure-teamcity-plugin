package ru.yandex.qatools.allure;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureBuildServiceFactory implements CommandLineBuildServiceFactory {

    @NotNull
    @Override
    public CommandLineBuildService createService() {
        return new AllureBuildServiceAdapter();
    }

    @NotNull
    @Override
    public AgentBuildRunnerInfo getBuildRunnerInfo() {
        return new AllureBuildRunnerInfo();
    }
}
