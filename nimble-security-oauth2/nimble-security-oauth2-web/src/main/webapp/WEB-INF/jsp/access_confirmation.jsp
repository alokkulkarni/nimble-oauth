<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Nimble Secure Login</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.js"></script>
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/style.css"/>"/>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
</head>

<body>
<div class="header"><a href="http://www.nimble.com" target="_blank" class="logo">Nimble</a></div>
<div class="main_body">
    <div id="step2">
        <% if (session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) != null && !(session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) instanceof UnapprovedClientAuthenticationException)) { %>
        <div class="error">
            <h2>Woops!</h2>

            <p>Access could not be granted.
                (<%= ((AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)).getMessage() %>
                )</p>
        </div>
        <% } %>

        <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>


        <authz:authorize ifAllGranted="ROLE_USER">
            <div class="welcome_str step2">Do you authorize "<c:out value="${client.name}"/>" to access your account,
                with permission to:
            </div>
            <ul class="authorizeList">
                <li><span>Retrieve contact and company information</span></li>
                <li><span>Create new contacts</span></li>
            </ul>


            <form id="confirmationForm" name="confirmationForm" action="<%=request.getContextPath()%>/oauth/authorize"
                  method="post">
                <input name="user_oauth_approval" value="true" type="hidden"/>
                <label><input name="authorize" class="authorizeButton" value="Authorize" type="submit"></label>
            </form>
            <form id="denialForm" name="denialForm" action="<%=request.getContextPath()%>/oauth/authorize"
                  method="post">
                <input name="user_oauth_approval" value="false" type="hidden"/>
                <label><input name="deny" class="authorizeButton" value="Decline" type="submit"></label>
            </form>
        </authz:authorize>
    </div>
</div>


</body>
</html>
