# 自定义Lint

## 一、代码静态检查
导入 Lint 的版本号必须是 Gradle plugin 的版本号加上23，本项目基于 AGP 4.1.1，那相关 Lint 版本就是 27.1.1。

为什么选用4.1.1，因为之下的版本在我的编译器里自定义lint可以生效，但没有高亮提示，可能和 AS 版本有关。

### 1、lintjar工程（生成 jar）

#### 1.1 自定义规则

eg：<strong>制定规范：</strong>检查 Log 使用，必须使用项目中的 LogUtil；
<strong>Crash 预防：</strong>检查 equals 使用，在equals(value)中，value不能为硬编码或定义在该类中的static final字符串，防止空指针；
在 API 28 及以上的 Android 版本中，不能同时设置 screenOrientation 和 translucent 属性，防止运行时发生 crash。

> Detector ：用来寻找和定位代码中的问题；UastScanner：扫描java/kotlin源文件，XmlScanner：扫描 XML 文件；UAST（统一抽象语法树），即以一种特定的树状结构来描述代码；

> Issue：用来定义和描述问题；分类别、优先级、严重程度，以及对应的 detector；

> IssueRegistry：问题注册器，把自定义的规则注册到Lint检查规则里

#### 1.2 单元测试

继承 LintDetectorTest()；重写 getDetector() 和 getIssues()；编写测试用的代码段；调用 lint() 进行测试

#### 1.3 配置文件支持

参考美团，因新增或修改规则发布过程繁琐，且各个业务需要的规则可能不一致，使用配置文件来解决。
lintConfig.json（定义需要的规则）放在项目根目录下，在 Detector 的 beforeCheckProject 回掉方法中来读取配置文件。

### 2、lintaar工程（生成 aar）

创建一个 android library，在 build.gradle 依赖  <em>lintPublish project(':lintjar')</em>

项目依赖于此库都可以执行自定义 lint 规则检查。

## 二、代码增量检查

### 1、lintPlugin工程（增量检查 gradle 插件）

gradle插件copy git hooks -> git hooks自动执行增量扫描的任务 -> git diff找到增量代码 -> lintjar.jar调用project.addfile() 扫描增量代码，输出问题txt -> git reset回滚代码

### 2、调试

调试 terminal 输入：./gradlew --no-daemon -Dorg.gradle.debug=true lintPlugin

双击 Shift -》选择 Attach Debugger to Process -》进行断点调试

源码断点：
```
cl.run(new LintRegistry(), files)-》driver.analyze()-》checkProject(project, main)-》
check.beforeCheckEachProject(libraryContext)，runFileDetectors(library, main)，check.afterCheckEachProject(libraryContext)
```

### 3、git增量检查

> 每次git commit都会通过git hooks触发Lint检查。检查结果会以TXT格式输出到项目根目录下，如果有问题，则会触发 git reset命令回滚提交。

### post-commit（git hooks脚本）

触发git增量检查功能需要将git hooks脚本复制到项目根目录的.git/hooks目录下。

参考：
https://www.infoq.cn/article/qhjzwpdhbpwyfqc9wwag
https://blog.csdn.net/weixin_33736832/article/details/91471036?spm=1001.2014.3001.5506
https://github.com/lsc1993/AwesomeLint
