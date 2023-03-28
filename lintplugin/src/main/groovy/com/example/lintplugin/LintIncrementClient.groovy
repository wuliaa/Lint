package com.example.lintplugin

import com.android.tools.lint.LintCliClient
import com.android.tools.lint.Warning
import com.android.tools.lint.client.api.LintRequest
import com.android.tools.lint.detector.api.Project

/**
 * 在LintRequest中加入了git提交的增量文件
 */

class LintIncrementClient extends LintCliClient {

    @Override
    protected LintRequest createLintRequest(List<File> files) {
        LintRequest request = super.createLintRequest(files)
        for (Project project : request.getProjects()) {
            for (File file : files) {
                project.addFile(file)
            }
        }
        return new LintRequest(this, files)
    }

    /**
     * 获取扫描文件得到的结果
     * Warning类包含了文件路径、问题描述、问题所在文件的行号等信息
     *
     * @return Warnings
     */
    List<Warning> getIssueWarnings() {
        return warnings
    }
}