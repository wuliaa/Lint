package com.example.lintjar.detector;


import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("UnstableApiUsage")
public class LogDetectorTest extends LintDetectorTest {

    private String sdkDir;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Properties properties = new Properties(System.getProperties());
        properties.load(new FileInputStream("../local.properties"));
        sdkDir = properties.getProperty("sdk.dir");
    }

    @Override
    protected Detector getDetector() {
        return new LogDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(LogDetector.issue);
    }

    private String inCorrectMethodCall =
               "package com.example.lint\n" +
               "import android.util.Log\n" +
               "class Test {\n" +
               "    fun test() {\n" +
               "        Log.i(\"123\",\"test\");\n" +
               "    }\n" +
               "}";

    private String correctMethodCall =
               "package com.example.lint;\n" +
               "class Test {\n" +
               "    void test() {\n" +
               "        LogUtil.i(\"123\",\"test\");\n" +
               "    }\n" +
               "}";

    @Test
    public void testInCorrectLogCall() {
        lint().requireCompileSdk()
                .sdkHome(new File(sdkDir))
                .files(kotlin(inCorrectMethodCall))
                .run()
                .expectWarningCount(1);
    }

    @Test
    public void testCorrectLogCall() {
        lint().requireCompileSdk()
                .sdkHome(new File(sdkDir))
                .files(java(correctMethodCall))
                .run()
                .expectClean();
    }

}