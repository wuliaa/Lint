package com.example.lintjar.detector;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintFix;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.TextFormat;
import com.example.lintjar.LintConfig;
import com.intellij.psi.PsiMethod;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.util.UastExpressionUtils;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class LogDetector extends Detector implements Detector.UastScanner {

    // 定义 Issue
    public static final Issue issue = Issue.create(
            "LogUsage",
            "Log Usage",
            "Please use the unified LogUtil class!",
            Category.CORRECTNESS,
            6,
            Severity.WARNING,
            new Implementation(LogDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    /**
     * 指定要扫描的UAST节点类型
     * UCallExpression 仅检查Java代码中的方法调用表达式
     *
     * @return
     */
    @Nullable
    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(UCallExpression.class);
    }

    /**
     * UastHandler 用于分析和处理源代码中的抽象语法树（AST）节点，实现特定代码分析逻辑
     * @param context
     * @return
     */
    @Nullable
    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        return new LogHandler(context);
    }

    /**
     * UElementHandler 是用于处理特定类型的AST节点的接口，例如类、方法、变量等
     */
    // 调试 terminal 输入：./gradlew --no-daemon -Dorg.gradle.debug=true lintDebug
    // 双击 Shift -》选择 Attach Debugger to Process -》进行断点调试
    class LogHandler extends UElementHandler {

        private JavaContext context;

        LogHandler(JavaContext context) {
            this.context = context;
        }

        @Override
        public void visitCallExpression(@NotNull UCallExpression node) {
            if (!UastExpressionUtils.isMethodCall(node)) return;
            if (node.getReceiver() != null
                    && node.getMethodName() != null) {
                String methodName = node.getMethodName();
                if (methodName.equals("i") || methodName.equals("d") || methodName.equals("e")
                        || methodName.equals("v") || methodName.equals("w") || methodName.equals("wtf")) {
                    PsiMethod method = node.resolve();
                    if (context.getEvaluator().isMemberInClass(method, "android.util.Log")) {
                        reportError(context, node);
                    }
                }
            }
        }
    }

    /**
     * getCallLocation()用于获取方法调用位置信息
     * @param context
     * @param node
     */
    private void reportError(JavaContext context, UCallExpression node) {
        LintFix fix = LintFix.create()
                .name("replace Log to LogUtil")
                .replace() // 替换为下面的代码
                .with("LogUtil." + node.getMethodName() +
                        "(" +
                        node.getValueArguments().get(0) + ", " +
                        node.getValueArguments().get(1) + ")")
                .build();
        context.report(issue,
                node,
                context.getLocation(node),
                "请勿直接调用 android.util.Log 类方法",
                fix
        );
    }
}
