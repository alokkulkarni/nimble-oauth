<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <title>Nimble Secure Login</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.js"></script>
    <link rel="stylesheet" href="<c:url value="/css/style.css"/>"/>
</head>
<body>
<div class="header"><a href="http://www.nimble.com" target="_blank" class="logo">Nimble</a></div>
<div class="main_body">
    <authz:authorize ifNotGranted="ROLE_USER">
        <div id="step1">
            <div class="welcome_str step1">Sign in to Nimble and authorize to access your account</div>
            <div class="pr_quote">
                <div class="quotation">"Nimble is a game-changer for CRM. It's simple, integrated with
                    social networks and tools like Mailchimp, makes relationships easy."
                </div>
                <div class="quote_author">Mark Cuban</div>
                <div class="quote_title">Entrepreneur and Owner of the Dallas Mavericks</div>
            </div>
            <div class="rightColumn">
                <div class="login_form">
                    <c:if test="${not empty param.authentication_error}">
                        <div class="errorBox">
                            Invalid Login or Password!
                        </div>
                    </c:if>
                    <c:if test="${not empty param.authorization_error}">
                        <h1>Woops!</h1>

                        <p class="error">You are not permitted to access that resource.</p>
                    </c:if>
                    <div class="sign_in">Sign In</div>
                    <form id="loginForm" name="loginForm" action="<c:url value="/login.do"/>" method="post">
                        <div>
                            <input type="text" id="emailAddress" name="j_username" size="34" autocomplete="off"
                                   value="Email"
                                   class="formInput greyText" title="Email">
                        </div>
                        <div class="pass_temp">
                            <input type="text" id="pass_temp" name="pass_temp" value="Password" size="34"
                                   autocomplete="off"
                                   class="pwdField greyText" title="Password" onfocus="switchForm()">
                        </div>
                        <div class="password">
                            <input type="password" id="password" name="j_password" value="" size="34" autocomplete="off"
                                   class="pwdField" title="Password" onblur="restoreForm()">
                        </div>

                        <div>
                            <input type="SUBMIT" name="login" accesskey="l" value="Sign In" class="submitBtn"/>
                        </div>
                    </form>
                    <div class="forgotPwd">
                        <a href="https://reg.nimble.com/index.html#remindPassword" target="_blank"> Forgot Password?</a>
                    </div>
                </div>
                <div class="newAccount">
                    Don't have a Nimble account?
                    <a href="http://www.nimble.com/register/business_trial/?lead_source=thirdparty_app"
                       target="_blank">Create one now!</a>
                </div>
            </div>
        </div>
    </authz:authorize>
    <authz:authorize ifAllGranted="ROLE_USER">
        <div style="text-align: center">
            <form action="<c:url value="/logout.do"/>"><input type="submit" value="Logout"></form>
        </div>
    </authz:authorize>
    <div style="clear:both;"></div>
</div>
<!-- end of main body -->


<script language="javascript">
    $(document).ready(function () {
        $(".formInput").focus(function () {
            if ($(this).val() == $(this)[0].title) {
                $(this).removeClass("greyText");
                $(this).val("");
            }
        });

        $(".formInput").blur(function () {
            if ($(this).val() == "") {
                $(this).addClass("greyText");
                $(this).val($(this)[0].title);
            }
        });

        /*$(".login_form input").keypress(function(e) {
         if (e.which == 13) {
         submitForm();
         return false;
         }

         return true;
         });*/

        var app_name = getURLParameter('app_name') || "default app";
        $(".main_body .welcome_str.step1").text(
                "Sign in to Nimble and authorize " + app_name + " to access your account"
        );
        /*$(".main_body .welcome_str.step2").text(
         "Do you authorize " + app_name + " to access your account, with permission to:"
         );*/

    });

    function getURLParameter(name) {
        var undecoded_param = (RegExp(name + '=' + '(.+?)(&|$)').exec(location.search) || [, null])[1];
        if (undecoded_param != null) {
            return decodeURI(undecoded_param);
        }
    }

    function switchForm() {
        $(".login_form .pass_temp").hide();
        $(".login_form .password").show();
        $("#password").focus();
    }

    function restoreForm() {
        if ($("#password").val() == '') {
            $(".login_form .pass_temp").show();
            $(".login_form .password").hide();
        }
    }

    /*function submitForm() {
     var email = $('#emailAddress').val();
     var session_body = {
     "email" : email,
     "password" : $('#password').val(),
     "is_persistent" : 1
     };
     var client_id = getURLParameter('client_id');
     var access_type = getURLParameter('access_type');
     var response_type = getURLParameter('response_type');
     var scope = getURLParameter('scope');
     //$('.login_form .errorBox').hide();
     $.post("/api/sessions", $.param(session_body)).
     done(function(data) {
     var token = data.token;
     $("#__client_id").val(getURLParameter('client_id'));
     $("#__redirect_uri").val(getURLParameter('redirect_uri'));
     $("#__response_type").val(getURLParameter('response_type'));
     $("#__token").val(token);
     $("#__user_id").val(email);

     $("#step1").hide();
     $("#step2").show();
     }).
     fail(function(xhr) {
     if (xhr.status == 402) {
     var errMsg = "Sorry, your Nimble account has expired or reached its quota of contacts. "
     } else {
     var response = $.parseJSON(xhr.responseText);
     var errMsg = response.errors.password[0] || "Error during authorization";
     }
     $(".login_form .errorBox").show();
     $(".login_form .errorBox").text(errMsg);
     });
     };*/

</script>

</body>
</html>


