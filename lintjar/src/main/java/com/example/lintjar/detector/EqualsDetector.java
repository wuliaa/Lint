package com.example.lintjar.detector;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintFix;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.UReferenceExpression;

import java.util.Collections;
import java.util.List;

/**
 * 在equals(value)中，value不能为硬编码或定义在该类中的static final字符串
 * 因为当为指定字符串的时候，需要value.equals()，防止空指针
 */
@SuppressWarnings("UnstableApiUsage")
public class EqualsDetector extends Detector implements Detector.UastScanner {

    public static final Issue issue = Issue.create(
            "EqualsUsage",
            "Equals Usage",
            "Do not hardcode parameters in function equals()",
            Category.CORRECTNESS,
            6,
            Severity.WARNING,
            new Implementation(EqualsDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    @Nullable
    @Override
    public List<String> getApplicableMethodNames() {
        return Collections.singletonList("equals");
    }

    @Override
    public void visitMethodCall(@NotNull JavaContext context, @NotNull UCallExpression node, @NotNull PsiMethod method) {
        if (node.getValueArguments().get(0).asSourceString().startsWith("\"") &&
                node.getValueArguments().get(1).asSourceString().endsWith("\"")
                || isStaticFinalField(node)) {
            reportError(context, node);
        }
    }

    private boolean isStaticFinalField(UCallExpression node) {
        UExpression arg = node.getValueArguments().get(0);
        if (arg instanceof UReferenceExpression) {
            PsiElement resolved = ((UReferenceExpression) arg).resolve();
            if (resolved instanceof PsiField) {
                PsiField field = (PsiField) resolved;
                return field.hasModifierProperty(PsiModifier.STATIC) &&
                        field.hasModifierProperty(PsiModifier.FINAL) &&
                        "java.lang.String".equals(field.getType().getCanonicalText());
            }
        }
        return false;
    }

    private void reportError(JavaContext context, UCallExpression node) {
        LintFix fix = LintFix.create()
                .name("replace the places")
                .replace() // 替换为下面的代码
                .with(node.getValueArguments().get(0) + ".equals(" +
                        node.getReceiver().asSourceString() + ")")
                .build();
        context.report(issue,
                node,
                context.getLocation(node),
                "在equals(value)中，value不能为硬编码或static final字符串，防止空指针",
                fix
        );
    }
}
