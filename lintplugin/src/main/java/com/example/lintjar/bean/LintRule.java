package com.example.lintjar.bean;

import java.util.List;
import java.util.Map;

public class LintRule {

    private Map<String, List<Rule>> lint_rules;

    public Map<String, List<Rule>> getLintRules() {
        return lint_rules;
    }

    public void setLintRules(Map<String, List<Rule>> lint_rules) {
        this.lint_rules = lint_rules;
    }

    public LintRule(){}

    public static class Rule {
        public String method;
        public String superClass;
        public String construction;
        public String message;

        public Rule(){}

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getSuperClass() {
            return superClass;
        }

        public void setSuperClass(String superClass) {
            this.superClass = superClass;
        }

        public String getConstruction() {
            return construction;
        }

        public void setConstruction(String construction) {
            this.construction = construction;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}