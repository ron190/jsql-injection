package spring;
import java.sql.SQLException;
import java.util.HashMap;

import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;



public class MasterService3 {
    
    //@Autowired
    //private SessionFactory sessionFactoryab; 
    
    public static HashMap<String, ConnectionProvider> getDataSourceHashMap() {

        //DriverManagerDataSource dataSource = new DriverManagerDataSource();
        ////dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        ////dataSource.setUrl("jdbc:mysql://localhost:3306/demo_database");
        ////dataSource.setUsername("root");
        ////dataSource.setPassword("");
        //dataSource.setDriverClassName("org.h2.Driver");
        //dataSource.setUrl("jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1");
        //dataSource.setUsername("sa");
        //dataSource.setPassword("sa");
        //
        //DriverManagerDataSource dataSource1 = new DriverManagerDataSource();
        ////dataSource1.setDriverClassName("com.mysql.jdbc.Driver");
        ////dataSource1.setUrl("jdbc:mysql://localhost:3306/demo_database_1");
        ////dataSource1.setUsername("root");
        ////dataSource1.setPassword("");
        //dataSource.setDriverClassName("org.h2.Driver");
        //dataSource.setUrl("jdbc:h2:mem:db2;DB_CLOSE_DELAY=-1");
        //dataSource.setUsername("sa1");
        //dataSource.setPassword("sa1");
        
//        Properties props = new Properties();
//        Properties props2 = new Properties();
//        try {
//            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//            
//            props.loadFromXML(classloader.getResourceAsStream("hibernate.cfg.xml"));
//            props2.loadFromXML(classloader.getResourceAsStream("hibernate.cfg2.xml"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setConnectionProperties(Application.prop);
        

        
////        Properties properties = new Properties();
////        properties.put("hibernate.connection.driver_class", "org.h2.Driver");
////        //properties.put("hibernate.connection.url", "jdbc:h2:mem:tenantId3;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS MYDB1\\;CREATE TABLE IF NOT EXISTS MYDB1.TBL1 (COL1 INTEGER NOT NULL, COL2 CHAR(25)) \\;INSERT INTO MYDB1.TBL1 VALUES (1, '')\\;");
////        properties.put("hibernate.connection.url", "jdbc:h2:mem:tenantId3;DB_CLOSE_DELAY=-1;");
////        properties.put("hibernate.connection.username", "sa");
////        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//        //properties.put("hibernate.hbm2ddl.auto", "create");
//        DriverManagerConnectionProviderImpl a = new DriverManagerConnectionProviderImpl();
////        a.configure(properties);
////        a.configure(props);
//        a.configure(Application.prop);
//        
////        Properties propertiesa = new Properties();
////        propertiesa.put("hibernate.connection.driver_class", "org.h2.Driver");
//////        propertiesa.put("hibernate.connection.url", "jdbc:h2:mem:tenantId4;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS MYDB1\\;CREATE TABLE IF NOT EXISTS MYDB1.TBL1 (COL1 INTEGER NOT NULL, COL2 CHAR(25)) \\;INSERT INTO MYDB1.TBL1 VALUES (2, '')\\;");
////        propertiesa.put("hibernate.connection.url", "jdbc:h2:mem:tenantId4;DB_CLOSE_DELAY=-1;");
////        propertiesa.put("hibernate.connection.username", "sa");
////        propertiesa.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//        //propertiesa.put("hibernate.hbm2ddl.auto", "create");
//        DriverManagerConnectionProviderImpl b = new DriverManagerConnectionProviderImpl();
////        b.configure(propertiesa);
////        b.configure(props2);
//        b.configure(Application.prop2);
//        
////        Properties propertiesab = new Properties();
////        propertiesab.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
//////        propertiesa.put("hibernate.connection.url", "jdbc:h2:mem:tenantId4;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS MYDB1\\;CREATE TABLE IF NOT EXISTS MYDB1.TBL1 (COL1 INTEGER NOT NULL, COL2 CHAR(25)) \\;INSERT INTO MYDB1.TBL1 VALUES (2, '')\\;");
////        propertiesab.put("hibernate.connection.url", "jdbc:mysql://127.0.0.1/musicstore?createDatabaseIfNotExist=true");
////        propertiesab.put("hibernate.connection.username", "root");
////        propertiesab.put("hibernate.connection.password", "my-secret-pw");
////        propertiesab.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
//        //propertiesa.put("hibernate.hbm2ddl.auto", "create");
//        DriverManagerConnectionProviderImpl bb = new DriverManagerConnectionProviderImpl();
////        bb.configure(propertiesab);
////        b.configure(props2);
//        bb.configure(Application.prop3);
//        
//        try {
//            org.h2.tools.Server server = org.h2.tools.Server.createTcpServer().start();
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
        

        HashMap<String, ConnectionProvider> hashMap = new HashMap<>();
        //hashMap.put("tenantId", dataSource);
        //hashMap.put("tenantId2", dataSource1);
//        hashMap.put("tenantId", a);
//        hashMap.put("tenantId2", b);
//        hashMap.put("tenantId3", bb);
        
//        DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setConnectionProperties(Application.prop);
//        dataSource.setUrl(Application.prop.getProperty("hibernate.connection.url"));
//        connectionProvider.setDataSource(dataSource);
//        connectionProvider.configure(Application.prop);
//        
//        DatasourceConnectionProviderImpl connectionProvider2 = new DatasourceConnectionProviderImpl();
//        DriverManagerDataSource dataSource2 = new DriverManagerDataSource();
//        dataSource2.setConnectionProperties(Application.prop2);
//        dataSource2.setUrl(Application.prop2.getProperty("hibernate.connection.url"));
//        connectionProvider2.setDataSource(dataSource2);
//        connectionProvider2.configure(Application.prop2);
        
        DatasourceConnectionProviderImpl connectionProvider3 = new DatasourceConnectionProviderImpl();
        DriverManagerDataSource dataSource3 = new DriverManagerDataSource();
        dataSource3.setConnectionProperties(Application.prop3);
        dataSource3.setUrl(Application.prop3.getProperty("hibernate.connection.url"));
        dataSource3.setUsername(Application.prop3.getProperty("hibernate.connection.username"));
        dataSource3.setPassword(Application.prop3.getProperty("hibernate.connection.password"));
        connectionProvider3.setDataSource(dataSource3);
        connectionProvider3.configure(Application.prop3);
         
//        hashMap.put("tenantId", connectionProvider);
//        hashMap.put("tenantId2", connectionProvider2);
        hashMap.put("tenantId3", connectionProvider3);
        
        return hashMap;
    }
}