<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale = 1.0, maximum-scale=1.0, user-scalable=no"/>
    <title>SriKrishna Travels</title>

    <link rel="stylesheet" href="client/assets-new/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="client/css/ionicons.min.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/owl.carousel.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/owl.theme.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/flexslider.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/main.css" type="text/css">

    <script src="client/node_modules/jquery/dist/jquery.min.js" type="text/javascript"></script>
    <script src="client/node_modules/angular/angular.js"></script>
    <script src="client/assets-new/js/menu_jquery.js" type="text/javascript"></script>
    <script src="client/assets-new/js/bootstrap.min.js"></script>
    <script src="client/assets-new/js/owl.carousel.min.js"></script>
    <script src="client/assets-new/js/jquery.flexslider.js"></script>
    <script src="client/assets-new/js/script.js"></script>
    <script src="client/js/app.js"></script>

    <style>
        .modal-login {
            width: 320px;
        }
        .modal-login .modal-content {
            border-radius: 1px;
            border: none;
        }
        .modal-login .modal-header {
            position: relative;
            justify-content: center;
            background: #f2f2f2;
        }
        .modal-login .modal-body {
            padding: 30px;
        }
        .modal-login .modal-footer {
            background: #f2f2f2;
        }
        .modal-login h4 {
            text-align: center;
            font-size: 26px;
        }
        .modal-login label {
            font-weight: normal;
            font-size: 13px;
        }
        .modal-login .form-control, .modal-login .btn {
            min-height: 38px;
            border-radius: 2px;
        }
        .modal-login .hint-text {
            text-align: center;
        }
        .modal-login .close {
            position: absolute;
            top: 15px;
            right: 15px;
        }
        .modal-login .checkbox-inline {
            margin-top: 12px;
        }
        .modal-login input[type="checkbox"]{
            margin-top: 2px;
        }
        .modal-login .btn {
            min-width: 100px;
            background: #3498db;
            border: none;
            line-height: normal;
        }
        .modal-login .btn:hover, .modal-login .btn:focus {
            background: #248bd0;
        }
        .modal-login .hint-text a {
            color: #999;
        }
        .trigger-btn {
            display: inline-block;
            margin: 100px auto;
        }
    </style>

</head>
<body style="background-image: url('../../client/assets-new/images/banner1.jpg');">
<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav navbar-right">
                <li class="active"><a href="#">Home</a></li>
                <li><a href="#">about</a></li>
                <li><a href="#">services</a></li>
                <li><a href="#">contact</a></li>
                <li><a href="#" data-toggle="modal" data-target="#loginModal">LOGIN</a></li>

                <!-- Modal -->
                <!-- Modal HTML -->
                <div id="loginModal" class="modal fade">
                    <div class="modal-dialog modal-login">
                        <div class="modal-content" style="    width: 407px;
    margin-top: 75px;">
                            <form action="/examples/actions/confirmation.php" method="post">
                                <div class="modal-header">
                                    <h3 class="modal-title" style="color: black;">Login</h3>
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                </div>
                                <div class="modal-body">
                                    <div class="form-group">
                                        <label style="color: black;">Username</label>
                                        <input type="text" class="form-control" required="required">
                                    </div>
                                    <div class="form-group">
                                        <div class="clearfix">
                                            <label style="color: black;">Password</label>
                                            <a href="#" class="pull-right text-muted"><small>Forgot?</small></a>
                                        </div>

                                        <input type="password" class="form-control" required="required">
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <label class="checkbox-inline pull-left"><input type="checkbox" ><span style="color: black;"> Remember me</span></label>
                                    <input type="submit" class="btn btn-primary pull-right" value="Login">
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </ul>
        </div>
    </div>
</nav>

</body>
</html>
