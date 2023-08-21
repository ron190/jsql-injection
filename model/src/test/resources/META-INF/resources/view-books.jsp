<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>View Items</title>
        <link href="/static/css/style.css" rel="stylesheet">
    </head>
    <body>
        <table>
            <thead>
                <tr>
                    <th>Ida <--- zaa</th>
                    <th>Name</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${items}" var="item">
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.name}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>