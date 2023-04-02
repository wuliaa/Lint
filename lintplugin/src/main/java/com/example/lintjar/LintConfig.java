package com.example.lintjar;

import com.android.tools.lint.detector.api.Context;
import com.example.lintjar.bean.LintRule;
import com.google.gson.Gson;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * lint 配置文件
 */
@SuppressWarnings("UnstableApiUsage")
public class LintConfig {

    private Map<String, List<LintRule.Rule>> rules;

    public LintConfig(Context context) {
        File projectDir = context.getProject().getDir().getAbsoluteFile();
        File configFile = new File("/Users/zhangxiaohong/AndroidStudioProjects/Lint", "lintConfig.json");
        try {
            if (configFile.exists() && configFile.isFile()) {
                String config = new String(Files.readAllBytes(Paths.get("/Users/zhangxiaohong/AndroidStudioProjects/Lint/lintConfig.json")), StandardCharsets.UTF_8);
                //System.out.println(Paths.get("/Users/zhangxiaohong/AndroidStudioProjects/Lint/lintConfig.json"));
                Gson gson = new Gson();
                rules = gson.fromJson(config, LintRule.class).getLintRules();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<LintRule.Rule> getConfig(String key) {
        if (null == rules) {
            return new ArrayList<>();
        }
        return rules.get(key);
    }
}
