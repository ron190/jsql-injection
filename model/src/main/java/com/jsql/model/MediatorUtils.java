package com.jsql.model;

import com.jsql.util.*;

public record MediatorUtils(
    PropertiesUtil propertiesUtil,
    ConnectionUtil connectionUtil,
    AuthenticationUtil authenticationUtil,
    GitUtil gitUtil,
    HeaderUtil headerUtil,
    ParameterUtil parameterUtil,
    ExceptionUtil exceptionUtil,
    SoapUtil soapUtil,
    MultipartUtil multipartUtil,
    CookiesUtil cookiesUtil,
    JsonUtil jsonUtil,
    PreferencesUtil preferencesUtil,
    ProxyUtil proxyUtil,
    ThreadUtil threadUtil,
    TamperingUtil tamperingUtil,
    UserAgentUtil userAgentUtil,
    CsrfUtil csrfUtil,
    DigestUtil digestUtil,
    FormUtil formUtil,
    CertificateUtil certificateUtil
) {}