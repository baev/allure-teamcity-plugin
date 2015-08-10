package ru.yandex.qatools.allure;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureBuildRunnerInfo implements AgentBuildRunnerInfo {

    @NotNull
    @Override
    public String getType() {
        return AllureConstants.RUN_TYPE;
    }

    @Override
    public boolean canRun(@NotNull BuildAgentConfiguration agentConfiguration) {
        return new File(agentConfiguration.getAgentToolsDirectory(), "allure").exists();
    }
}
