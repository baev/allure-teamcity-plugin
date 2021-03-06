package ru.yandex.qatools.allure;

import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.BundledTool;
import jetbrains.buildServer.agent.BundledToolsRegistry;
import jetbrains.buildServer.agent.ToolCannotBeFoundException;
import jetbrains.buildServer.agent.ToolProvider;
import jetbrains.buildServer.agent.ToolProvidersRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 11.08.15
 */
public class AllureToolProvider implements ToolProvider {

    private final BundledToolsRegistry bundledRegistry;

    public AllureToolProvider(@NotNull ToolProvidersRegistry toolProvidersRegistry,
                              @NotNull final BundledToolsRegistry bundledRegistry) {
        toolProvidersRegistry.registerToolProvider(this);
        this.bundledRegistry = bundledRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(@NotNull final String toolName) {
        return AllureConstants.ALLURE_TOOL_NAME.equals(toolName);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getPath(@NotNull final String toolName) {
        BundledTool tool = bundledRegistry.findTool(toolName);
        if (tool == null) {
            throw new ToolCannotBeFoundException("Could not locate Allure installation.");
        }
        return tool.getRootPath().getPath();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getPath(@NotNull final String toolName,
                          @NotNull final AgentRunningBuild build,
                          @NotNull final BuildRunnerContext runner) {
        return getPath(toolName);
    }
}
