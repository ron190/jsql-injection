package com.test.method;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlErrorTestSuite;

public class CustomMethodTestIgnoreSuite extends ConcreteMySqlErrorTestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        // TODO Request params not passed when cutom method => fallback to querystring
        // Need custom method set also for querystring
        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/custom?tenant=mysql-error");
        model.getMediatorUtils().getParameterUtil().setListRequest(Arrays.asList(
            new SimpleEntry<>("name", "")
        ));
        
        model.setIsScanning(true);
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getRequest())
        .withTypeRequest("CUSTOM-JSQL");
        
        model.beginInjection();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
}

/*

[log4j.stdout.properties] 16:56:39,981 [ForkJoinPool-2-worker-2] INFO  Starting new injection: http://localhost:8080/custom?tenant=mysql&name=0'

[log4j.stdout.properties] 16:56:40,464 [ForkJoinPool-2-worker-2] INFO  Using insertion character [-1']

[log4j.stdout.properties] 16:56:40,482 [ForkJoinPool-2-worker-2] WARN  Database unknown, forcing to [MySQL]

[log4j.stdout.properties] 16:56:44,355 [ForkJoinPool-2-worker-1] INFO  Using strategy [Error GROUPBY::floor_rand]

[log4j.stdout.properties] 16:56:44,470 [ForkJoinPool-2-worker-1] INFO  ListDatabases: found [musicstore, information_schema, performance_schema, mysql] to find [musicstore]

[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 7.006 s - in com.test.insertion.EmptyErrorTestSuite

[INFO] Running com.test.insertion.BadValueTestSuite

[log4j.stdout.properties] 16:56:44,562 [ForkJoinPool-2-worker-1] INFO  Starting new injection: http://localhost:8080/normal-insertion-char

[log4j.stdout.properties] 16:56:45,173 [ForkJoinPool-2-worker-1] INFO  Using insertion character [-1"))]

[log4j.stdout.properties] 16:56:45,198 [ForkJoinPool-2-worker-3] INFO  Using strategy [Normal]

[log4j.stdout.properties] 16:56:45,228 [ForkJoinPool-2-worker-3] INFO  ListDatabases: found [musicstore, information_schema, performance_schema, mysql] to find [musicstore]

[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.466 s - in com.test.method.PathParamTestSuite

[INFO] Running com.test.preferences.CheckAllRequestTestSuite

[log4j.stdout.properties] 16:56:45,258 [ForkJoinPool-2-worker-1] INFO  Using database [MySQL]

[log4j.stdout.properties] 16:56:45,290 [ForkJoinPool-2-worker-3] INFO  Starting new injection: http://localhost:8080/normal-post

[log4j.stdout.properties] 16:56:45,341 [ForkJoinPool-2-worker-3] INFO  Checking REQUEST parameter fake=empty

[log4j.stdout.properties] 16:56:46,194 [ForkJoinPool-2-worker-3] WARN  No character insertion activates ORDER BY error, forcing to [empty]

[log4j.stdout.properties] 16:56:46,195 [ForkJoinPool-2-worker-3] INFO  Using insertion character [empty]

[log4j.stdout.properties] 16:56:46,209 [ForkJoinPool-2-worker-2] WARN  Vulnerable to GROUPBY::floor_rand but injectable size is incorrect

[log4j.stdout.properties] 16:56:46,218 [ForkJoinPool-2-worker-3] INFO  Using database [MySQL]

[log4j.stdout.properties] 16:56:46,302 [ForkJoinPool-2-worker-2] WARN  Vulnerable to XML::extractvalue but injectable size is incorrect

[log4j.stdout.properties] 16:56:47,062 [ForkJoinPool-2-worker-2] INFO  Using strategy [Error BIGINT::exp]

[log4j.stdout.properties] 16:56:47,075 [ForkJoinPool-2-worker-2] WARN  Row parsing failed using capacity

com.jsql.model.exception.InjectionFailureException: Row parsing failed using capacity

    at com.jsql.model.suspendable.SuspendableGetRows.parseLeadFound(SuspendableGetRows.java:360)

    at com.jsql.model.suspendable.SuspendableGetRows.run(SuspendableGetRows.java:91)

    at com.jsql.model.accessible.DataAccess.listDatabases(DataAccess.java:215)

    at com.test.AbstractTestSuite.listDatabases(AbstractTestSuite.java:176)

    at com.test.method.CustomMethodTestSuite.listDatabases(CustomMethodTestSuite.java:47)

    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)

    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)

    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)

    at java.lang.reflect.Method.invoke(Method.java:498)

    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:675)

    at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:125)

    at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:132)

    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:124)

    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestTemplateMethod(TimeoutExtension.java:81)

    at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:104)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:62)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:43)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:35)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:202)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:198)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:135)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:69)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)

    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService$ExclusiveTask.compute(ForkJoinPoolHierarchicalTestExecutorService.java:171)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService.submit(ForkJoinPoolHierarchicalTestExecutorService.java:104)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask$DefaultDynamicTestExecutor.execute(NodeTestTask.java:198)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.execute(TestTemplateTestDescriptor.java:138)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.lambda$execute$2(TestTemplateTestDescriptor.java:106)

    at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)

    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)

    at java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:175)

    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)

    at java.util.Iterator.forEachRemaining(Iterator.java:116)

    at java.util.Spliterators$IteratorSpliterator.forEachRemaining(Spliterators.java:1801)

    at java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:647)

    at java.util.stream.ReferencePipeline$7$1.accept(ReferencePipeline.java:272)

    at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)

    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)

    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)

    at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)

    at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)

    at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)

    at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.execute(TestTemplateTestDescriptor.java:106)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.execute(TestTemplateTestDescriptor.java:41)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)

    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService$ExclusiveTask.compute(ForkJoinPoolHierarchicalTestExecutorService.java:171)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService.invokeAll(ForkJoinPoolHierarchicalTestExecutorService.java:115)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)

    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService$ExclusiveTask.compute(ForkJoinPoolHierarchicalTestExecutorService.java:171)

    at java.util.concurrent.RecursiveAction.exec(RecursiveAction.java:189)

    at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)

    at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)

    at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)

    at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)

Caused by: java.util.regex.PatternSyntaxException: Unclosed counted closure near index 17

(?s)SqLi(?i)(.{1,null})

                 ^

    at java.util.regex.Pattern.error(Pattern.java:1969)

    at java.util.regex.Pattern.closure(Pattern.java:3155)

    at java.util.regex.Pattern.sequence(Pattern.java:2148)

    at java.util.regex.Pattern.expr(Pattern.java:2010)

    at java.util.regex.Pattern.group0(Pattern.java:2919)

    at java.util.regex.Pattern.sequence(Pattern.java:2065)

    at java.util.regex.Pattern.expr(Pattern.java:2010)

    at java.util.regex.Pattern.compile(Pattern.java:1702)

    at java.util.regex.Pattern.<init>(Pattern.java:1352)

    at java.util.regex.Pattern.compile(Pattern.java:1028)

    at com.jsql.model.suspendable.SuspendableGetRows.parseLeadFound(SuspendableGetRows.java:354)

    ... 81 more

[log4j.stdout.properties] 16:56:47,142 [ForkJoinPool-2-worker-2] WARN  Row parsing failed using capacity

com.jsql.model.exception.InjectionFailureException: Row parsing failed using capacity

    at com.jsql.model.suspendable.SuspendableGetRows.parseLeadFound(SuspendableGetRows.java:360)

    at com.jsql.model.suspendable.SuspendableGetRows.run(SuspendableGetRows.java:91)

    at com.jsql.model.accessible.DataAccess.listDatabases(DataAccess.java:215)

    at com.test.AbstractTestSuite.listDatabases(AbstractTestSuite.java:176)

    at com.test.method.CustomMethodTestSuite.listDatabases(CustomMethodTestSuite.java:47)

    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)

    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)

    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)

    at java.lang.reflect.Method.invoke(Method.java:498)

    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:675)

    at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:125)

    at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:132)

    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:124)

    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestTemplateMethod(TimeoutExtension.java:81)

    at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:104)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:62)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:43)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:35)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:202)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:198)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:135)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:69)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)

    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService$ExclusiveTask.compute(ForkJoinPoolHierarchicalTestExecutorService.java:171)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService.submit(ForkJoinPoolHierarchicalTestExecutorService.java:104)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask$DefaultDynamicTestExecutor.execute(NodeTestTask.java:198)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.execute(TestTemplateTestDescriptor.java:138)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.lambda$execute$2(TestTemplateTestDescriptor.java:106)

    at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)

    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)

    at java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:175)

    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)

    at java.util.Iterator.forEachRemaining(Iterator.java:116)

    at java.util.Spliterators$IteratorSpliterator.forEachRemaining(Spliterators.java:1801)

    at java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:647)

    at java.util.stream.ReferencePipeline$7$1.accept(ReferencePipeline.java:272)

    at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)

    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)

    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)

    at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)

    at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)

    at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)

    at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.execute(TestTemplateTestDescriptor.java:106)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.execute(TestTemplateTestDescriptor.java:41)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)

    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService$ExclusiveTask.compute(ForkJoinPoolHierarchicalTestExecutorService.java:171)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService.invokeAll(ForkJoinPoolHierarchicalTestExecutorService.java:115)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)

    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService$ExclusiveTask.compute(ForkJoinPoolHierarchicalTestExecutorService.java:171)

    at java.util.concurrent.RecursiveAction.exec(RecursiveAction.java:189)

    at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)

    at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)

    at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)

    at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)

Caused by: java.util.regex.PatternSyntaxException: Unclosed counted closure near index 17

(?s)SqLi(?i)(.{1,null})

                 ^

    at java.util.regex.Pattern.error(Pattern.java:1969)

    at java.util.regex.Pattern.closure(Pattern.java:3155)

    at java.util.regex.Pattern.sequence(Pattern.java:2148)

    at java.util.regex.Pattern.expr(Pattern.java:2010)

    at java.util.regex.Pattern.group0(Pattern.java:2919)

    at java.util.regex.Pattern.sequence(Pattern.java:2065)

    at java.util.regex.Pattern.expr(Pattern.java:2010)

    at java.util.regex.Pattern.compile(Pattern.java:1702)

    at java.util.regex.Pattern.<init>(Pattern.java:1352)

    at java.util.regex.Pattern.compile(Pattern.java:1028)

    at com.jsql.model.suspendable.SuspendableGetRows.parseLeadFound(SuspendableGetRows.java:354)

    ... 81 more

[log4j.stdout.properties] 16:56:47,175 [ForkJoinPool-2-worker-2] WARN  Row parsing failed using capacity

com.jsql.model.exception.InjectionFailureException: Row parsing failed using capacity

    at com.jsql.model.suspendable.SuspendableGetRows.parseLeadFound(SuspendableGetRows.java:360)

    at com.jsql.model.suspendable.SuspendableGetRows.run(SuspendableGetRows.java:91)

    at com.jsql.model.accessible.DataAccess.listDatabases(DataAccess.java:215)

    at com.test.AbstractTestSuite.listDatabases(AbstractTestSuite.java:176)

    at com.test.method.CustomMethodTestSuite.listDatabases(CustomMethodTestSuite.java:47)

    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)

    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)

    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)

    at java.lang.reflect.Method.invoke(Method.java:498)

    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:675)

    at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:125)

    at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:132)

    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:124)

    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestTemplateMethod(TimeoutExtension.java:81)

    at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:104)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:62)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:43)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:35)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:202)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:198)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:135)

    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:69)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)

    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService$ExclusiveTask.compute(ForkJoinPoolHierarchicalTestExecutorService.java:171)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService.submit(ForkJoinPoolHierarchicalTestExecutorService.java:104)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask$DefaultDynamicTestExecutor.execute(NodeTestTask.java:198)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.execute(TestTemplateTestDescriptor.java:138)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.lambda$execute$2(TestTemplateTestDescriptor.java:106)

    at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)

    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)

    at java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:175)

    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)

    at java.util.Iterator.forEachRemaining(Iterator.java:116)

    at java.util.Spliterators$IteratorSpliterator.forEachRemaining(Spliterators.java:1801)

    at java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:647)

    at java.util.stream.ReferencePipeline$7$1.accept(ReferencePipeline.java:272)

    at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)

    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)

    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)

    at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)

    at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)

    at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)

    at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.execute(TestTemplateTestDescriptor.java:106)

    at org.junit.jupiter.engine.descriptor.TestTemplateTestDescriptor.execute(TestTemplateTestDescriptor.java:41)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:135)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)

    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService$ExclusiveTask.compute(ForkJoinPoolHierarchicalTestExecutorService.java:171)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService.invokeAll(ForkJoinPoolHierarchicalTestExecutorService.java:115)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:125)

    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:135)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:123)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:122)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:80)

    at org.junit.platform.engine.support.hierarchical.ForkJoinPoolHierarchicalTestExecutorService$ExclusiveTask.compute(ForkJoinPoolHierarchicalTestExecutorService.java:171)

    at java.util.concurrent.RecursiveAction.exec(RecursiveAction.java:189)

    at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)

    at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)

    at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)

    at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)

Caused by: java.util.regex.PatternSyntaxException: Unclosed counted closure near index 17

(?s)SqLi(?i)(.{1,null})

                 ^

    at java.util.regex.Pattern.error(Pattern.java:1969)

    at java.util.regex.Pattern.closure(Pattern.java:3155)

    at java.util.regex.Pattern.sequence(Pattern.java:2148)

    at java.util.regex.Pattern.expr(Pattern.java:2010)

    at java.util.regex.Pattern.group0(Pattern.java:2919)

    at java.util.regex.Pattern.sequence(Pattern.java:2065)

    at java.util.regex.Pattern.expr(Pattern.java:2010)

    at java.util.regex.Pattern.compile(Pattern.java:1702)

    at java.util.regex.Pattern.<init>(Pattern.java:1352)

    at java.util.regex.Pattern.compile(Pattern.java:1028)

    at com.jsql.model.suspendable.SuspendableGetRows.parseLeadFound(SuspendableGetRows.java:354)

    ... 81 more

[ERROR] Tests run: 3, Failures: 1, Errors: 0, Skipped: 2, Time elapsed: 7.251 s <<< FAILURE! - in com.test.method.CustomMethodTestSuite

[ERROR] com.test.method.CustomMethodTestSuite.listDatabases  Time elapsed: 0.029 s  <<< FAILURE!

java.lang.AssertionError: Test execution #3 (of up to 3) failed ~> test fails
*/