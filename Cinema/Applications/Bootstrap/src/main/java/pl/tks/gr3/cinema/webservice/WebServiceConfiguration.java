package pl.tks.gr3.cinema.webservice;//package pl.tks.gr3.cinema.webservice;
//
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.ws.config.annotation.EnableWs;
//import org.springframework.ws.transport.http.MessageDispatcherServlet;
//import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
//import org.springframework.xml.xsd.SimpleXsdSchema;
//import org.springframework.xml.xsd.XsdSchema;
//
//@Configuration
//@EnableWs
//public class WebServiceConfiguration {
//
//    @Bean
//    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
//        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
//        servlet.setApplicationContext(context);
//        servlet.setTransformWsdlLocations(true);
//        return new ServletRegistrationBean<MessageDispatcherServlet>(servlet, "/ws/*");
//    }
//
//    @Bean(name = "users")
//    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema usersSchema) {
//        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
//        wsdl11Definition.setPortTypeName("Users");
//        wsdl11Definition.setLocationUri("/ws");
//        wsdl11Definition.setTargetNamespace("");
//        wsdl11Definition.setSchema(usersSchema);
//        wsdl11Definition.setRequestSuffix("Request");
//        wsdl11Definition.setResponseSuffix("Response");
//        wsdl11Definition.setFaultSuffix("commonFault");
//        return wsdl11Definition;
//    }
//
//    @Bean
//    public XsdSchema usersSchema() {
//        return new SimpleXsdSchema(new ClassPathResource("users.xsd"));
//    }
//}
