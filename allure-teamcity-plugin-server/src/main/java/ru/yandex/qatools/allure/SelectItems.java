package ru.yandex.qatools.allure;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 11.08.15
 */
public class SelectItems {

    public String name() {
        return "YO";
    }

    public List<String> items() {
        return Arrays.asList(
                "1.4.16",
                "1.4.17",
                "RELEASE"
        );
    }

}
