package com.example.lintjar;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.ApiKt;
import com.android.tools.lint.detector.api.Issue;
import com.example.lintjar.detector.EqualsDetector;
import com.example.lintjar.detector.LogDetector;
import com.example.lintjar.detector.TemplateDetector;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 把自定义的规则注册到Lint检查规则里
 */
@SuppressWarnings("UnstableApiUsage")
public class LintRegistry extends IssueRegistry {

    @NotNull
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(
                LogDetector.issue,
                TemplateDetector.issue,
                EqualsDetector.issue);
    }

    /**
     * 返回当前API版本，该规则就能够与当前版本的Android Lint兼容，从而正常运行
     * @return
     */
    @Override
    public int getApi() {
        return ApiKt.CURRENT_API;
    }
}
