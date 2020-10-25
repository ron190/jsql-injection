package com.test.vendor.db2;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junitpioneer.jupiter.RepeatFailedTest;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;

public class Db2NormalTestIgnoreSuite extends ConcreteDb2TestSuite {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://localhost:8080/greeting");
        model.getMediatorUtils().getParameterUtil().setListQueryString(Arrays.asList(
            new SimpleEntry<>("tenant", "db2"),
            new SimpleEntry<>("name", "0'")
        ));
        
        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.setIsScanning(true);
        model.getMediatorStrategy().setStrategy(model.getMediatorStrategy().getNormal());
        model.getMediatorVendor().setVendorByUser(model.getMediatorVendor().getDb2());
        model.beginInjection();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listDatabases() throws JSqlException {
        super.listDatabases();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listTables() throws JSqlException {
        super.listTables();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listColumns() throws JSqlException {
        super.listColumns();
    }
    
    @Override
    @RepeatFailedTest(3)
    public void listValues() throws JSqlException {
        super.listValues();
    }
}

/*
[log4j.stdout.properties] 17:10:45,126 [ForkJoinPool-1-worker-2] ERROR com.ibm.db2.jcc.am.SqlException: DB2 SQL Error: SQLCODE=-1035, SQLSTATE=57019, SQLERRMC=null, DRIVER=4.26.14

com.ibm.db2.jcc.am.SqlException: DB2 SQL Error: SQLCODE=-1035, SQLSTATE=57019, SQLERRMC=null, DRIVER=4.26.14

    at com.ibm.db2.jcc.am.b7.a(b7.java:815)

    at com.ibm.db2.jcc.am.b7.a(b7.java:66)

    at com.ibm.db2.jcc.am.b7.a(b7.java:140)

    at com.ibm.db2.jcc.am.Connection.completeSqlca(Connection.java:5269)

    at com.ibm.db2.jcc.t4.z.q(z.java:861)

    at com.ibm.db2.jcc.t4.z.p(z.java:717)

    at com.ibm.db2.jcc.t4.z.l(z.java:538)

    at com.ibm.db2.jcc.t4.z.d(z.java:153)

    at com.ibm.db2.jcc.t4.b.k(b.java:1458)

    at com.ibm.db2.jcc.t4.b.b(b.java:1370)

    at com.ibm.db2.jcc.t4.b.a(b.java:6712)

    at com.ibm.db2.jcc.t4.b.b(b.java:904)

    at com.ibm.db2.jcc.t4.b.a(b.java:820)

    at com.ibm.db2.jcc.t4.b.a(b.java:441)

    at com.ibm.db2.jcc.t4.b.a(b.java:414)

    at com.ibm.db2.jcc.t4.b.<init>(b.java:352)

    at com.ibm.db2.jcc.DB2SimpleDataSource.getConnection(DB2SimpleDataSource.java:233)

    at com.ibm.db2.jcc.DB2SimpleDataSource.getConnection(DB2SimpleDataSource.java:200)

    at com.ibm.db2.jcc.DB2Driver.connect(DB2Driver.java:471)

    at com.ibm.db2.jcc.DB2Driver.connect(DB2Driver.java:113)

    at java.sql.DriverManager.getConnection(DriverManager.java:664)

    at java.sql.DriverManager.getConnection(DriverManager.java:247)

    at com.test.AbstractTestSuite.requestJdbc(AbstractTestSuite.java:121)

    at com.test.AbstractTestSuite.initializeBackend(AbstractTestSuite.java:107)

    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)

    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)

    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)

    at java.lang.reflect.Method.invoke(Method.java:498)

    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:675)

    at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:125)

    at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:132)

    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:111)

    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeAllMethod(TimeoutExtension.java:60)

    at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:104)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:62)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:43)

    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:35)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)

    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)

    at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$invokeBeforeAllMethods$8(ClassBasedTestDescriptor.java:371)

    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)

    at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeBeforeAllMethods(ClassBasedTestDescriptor.java:369)

    at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.before(ClassBasedTestDescriptor.java:193)

    at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.before(ClassBasedTestDescriptor.java:77)

    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:132)

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

[log4j.stdout.properties] 17:10:45,743 [ForkJoinPool-1-worker-2] INFO  Starting new injection: http://localhost:8080/greeting

[logback.xml] 2020-05-01 17:10:45 [http-nio-8080-exec-1] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring DispatcherServlet 'dispatcherServlet'

[logback.xml] 2020-05-01 17:10:45 [http-nio-8080-exec-1] INFO  o.s.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'

[logback.xml] 2020-05-01 17:10:46 [http-nio-8080-exec-1] INFO  o.s.web.servlet.DispatcherServlet - Completed initialization in 42 ms

[log4j.stdout.properties] 17:10:58,168 [ForkJoinPool-1-worker-2] WARN  No character insertion activates ORDER BY error, forcing to [0']

[log4j.stdout.properties] 17:10:58,169 [ForkJoinPool-1-worker-2] INFO  Using insertion character [0']

[log4j.stdout.properties] 17:10:58,172 [ForkJoinPool-1-worker-2] INFO  Database forced by user to [DB2]

[log4j.stdout.properties] 17:10:58,195 [ForkJoinPool-1-worker-2] INFO  No Time strategy known for DB2

[log4j.stdout.properties] 17:10:59,131 [ForkJoinPool-1-worker-2] INFO  Using strategy [Normal]

[log4j.stdout.properties] 17:10:59,317 [ForkJoinPool-1-worker-2] INFO  ListDatabases: found [SYSCAT, SYSIBM, NULLID, SYSIBMINTERNAL, DB2INST1, SYSIBMTS, SYSIBMADM, SYSPROC, SYSPUBLIC, SQLJ, SYSSTAT, SYSFUN, SYSTOOLS] to find []

[log4j.stdout.properties] 17:10:59,407 [ForkJoinPool-1-worker-2] INFO  ListDatabases: found [SYSCAT, SYSIBM, NULLID, SYSIBMINTERNAL, DB2INST1, SYSIBMTS, SYSIBMADM, SYSPROC, SYSPUBLIC, SQLJ, SYSSTAT, SYSFUN, SYSTOOLS] to find []

[log4j.stdout.properties] 17:10:59,470 [ForkJoinPool-1-worker-2] INFO  ListDatabases: found [SYSCAT, SYSIBM, NULLID, SYSIBMINTERNAL, DB2INST1, SYSIBMTS, SYSIBMADM, SYSPROC, SYSPUBLIC, SQLJ, SYSSTAT, SYSFUN, SYSTOOLS] to find []

[log4j.stdout.properties] 17:10:59,572 [ForkJoinPool-1-worker-2] INFO  Tables: found [STUDENT] to find []

[log4j.stdout.properties] 17:10:59,630 [ForkJoinPool-1-worker-2] INFO  Tables: found [STUDENT] to find []

[log4j.stdout.properties] 17:10:59,706 [ForkJoinPool-1-worker-2] INFO  Tables: found [STUDENT] to find []

[log4j.stdout.properties] 17:10:59,801 [ForkJoinPool-1-worker-2] INFO  Values: found [1] to find []

[log4j.stdout.properties] 17:11:01,926 [ForkJoinPool-1-worker-2] INFO  Values: found [1] to find []

[log4j.stdout.properties] 17:11:06,450 [ForkJoinPool-1-worker-2] INFO  Values: found [1] to find []

[log4j.stdout.properties] 17:11:11,035 [ForkJoinPool-1-worker-2] INFO  listColumns: found [ROLL_NO, LAST_NAME, CLASS_NAME, FIRST_NAME, AGE, STUDENT_ID] to find []

[log4j.stdout.properties] 17:11:15,634 [ForkJoinPool-1-worker-2] INFO  listColumns: found [ROLL_NO, LAST_NAME, CLASS_NAME, FIRST_NAME, AGE, STUDENT_ID] to find []

[log4j.stdout.properties] 17:11:20,178 [ForkJoinPool-1-worker-2] INFO  listColumns: found [ROLL_NO, LAST_NAME, CLASS_NAME, FIRST_NAME, AGE, STUDENT_ID] to find []

[ERROR] Tests run: 12, Failures: 4, Errors: 0, Skipped: 8, Time elapsed: 134.816 s <<< FAILURE! - in com.test.vendor.db2.Db2NormalTestSuite

[ERROR] com.test.vendor.db2.Db2NormalTestSuite.listDatabases  Time elapsed: 0.06 s  <<< FAILURE!

java.lang.AssertionError: Test execution #3 (of up to 3) failed ~> test fails

[ERROR] com.test.vendor.db2.Db2NormalTestSuite.listTables  Time elapsed: 0.076 s  <<< FAILURE!

java.lang.AssertionError: Test execution #3 (of up to 3) failed ~> test fails

[ERROR] com.test.vendor.db2.Db2NormalTestSuite.listValues  Time elapsed: 4.528 s  <<< FAILURE!

java.lang.AssertionError: Test execution #3 (of up to 3) failed ~> test fails

[ERROR] com.test.vendor.db2.Db2NormalTestSuite.listColumns  Time elapsed: 4.542 s  <<< FAILURE!

java.lang.AssertionError: Test execution #3 (of up to 3) failed ~> test fails
*/