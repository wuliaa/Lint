package com.example.lintjar.detector;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.example.lintjar.LintConfig;
import com.example.lintjar.bean.LintRule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.util.UastExpressionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 模版检测类
 */
@SuppressWarnings("UnstableApiUsage")
public class TemplateDetector extends Detector implements Detector.UastScanner {

    private LintConfig lintConfig;

    public static final Issue issue = Issue.create(
            "TemplateDetector",
            "Template Detector",
            "This is a template detector",
            Category.CORRECTNESS,
            6,
            Severity.WARNING,
            new Implementation(TemplateDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    @Override
    public void beforeCheckProject(@NotNull Context context) {
        super.beforeCheckProject(context);
        // 配置文件
        lintConfig = new LintConfig(context);
    }

    @Nullable
    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Arrays.asList(UCallExpression.class, UClass.class);
    }

    @Nullable
    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        return new TemplateHandler(context);
    }

    class TemplateHandler extends UElementHandler {

        private JavaContext context;

        TemplateHandler(JavaContext context) {
            this.context = context;
        }

        @Override
        public void visitCallExpression(@NotNull UCallExpression node) {
            if (!UastExpressionUtils.isMethodCall(node)) return;
            // 扫描方法
            lintConfig.getConfig("use_method").forEach(rule -> {
                if (null != node.resolve() && context.getEvaluator().isMemberInClass(node.resolve(), rule.method)) {
                    reportError(context, node, rule);
                }
            });
            // 扫描构造函数
            lintConfig.getConfig("use_construction").forEach(rule -> {
                if (null != node.resolve() && rule.construction.equals(node.resolve().getContainingClass().getQualifiedName())) {
                    reportError(context, node, rule);
                }
            });
        }

        @Override
        public void visitClass(@NotNull UClass node) {
            // 扫描父类
            lintConfig.getConfig("use_class").forEach(rule -> {
                if (null != node.getSuperClass() && rule.superClass.equals(node.getSuperClass().getQualifiedName())) {
                    reportError(context, node, rule);
                }
            });
        }
    }


    private void reportError(JavaContext context, UExpression node, LintRule.Rule rule) {
        context.report(issue,
                node,
                context.getLocation(node),
                rule.message
        );
    }

    private void reportError(JavaContext context, UElement node, LintRule.Rule rule) {
        context.report(issue,
                node,
                context.getLocation(node),
                rule.message
        );
    }

}
