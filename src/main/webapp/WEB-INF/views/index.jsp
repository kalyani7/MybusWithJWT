<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale = 1.0, maximum-scale=1.0, user-scalable=no"/>
    <title>SriKrishna Travels</title>

    <link href="client/node_modules/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="client/assets-new/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="client/css/ionicons.min.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/owl.carousel.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/owl.theme.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/flexslider.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/main.css" type="text/css">
    <link rel="stylesheet" href="client/css/app.css" type="text/css">
    <link rel="stylesheet" href="client/node_modules/sweetalert/dist/sweetalert.css">

    <script src="client/node_modules/jquery/dist/jquery.min.js" type="text/javascript"></script>
    <script src="client/node_modules/jquery/dist/jquery.js"></script>
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

    <link rel="stylesheet" href="client/node_modules/ui-select/dist/select.css">
    <script src="client/node_modules/angular-cookies/angular-cookies.min.js"></script>
    <script src="client/node_modules/sweetalert/dist/sweetalert.min.js"></script>
    <script src="client/node_modules/ui-select/dist/select.min.js"></script>
    <script src="client/node_modules/angular-file-upload/dist/angular-file-upload.js" type="text/javascript"></script>

    <script src="client/node_modules/ng-file-upload/dist/ng-file-upload.js" type="text/javascript"></script>

    <script src="client/js/services/appConfigManager.js"></script>
    <script src="client/js/modules/loginModule.js"></script>
    <script src="client/js/modules/homeModule.js"></script>
    <script src="client/js/modules/usersModule.js"></script>
    <script src="client/js/modules/amenitiesModule.js"></script>
    <script src="client/js/modules/cityModule.js"></script>
    <script src="client/js/modules/vehicleModule.js"></script>
    <script src="client/js/modules/serviceReportsModule.js"></script>
    <script src="client/js/modules/paymentModule.js"></script>
    <script src="client/js/modules/routeModule.js"></script>
    <script src="client/js/modules/cancelModule.js"></script>
    <script src="client/js/modules/roleModule.js"></script>
    <script src="client/js/modules/agentModule.js"></script>
    <script src="client/js/modules/dueReportModule.js"></script>
    <script src="client/js/modules/serviceComboModule.js"></script>
    <script src="client/js/modules/tripComboModule.js"></script>
    <script src="client/js/modules/cashTransfersModule.js"></script>
    <script src="client/js/modules/vehicleExpensesModule.js"></script>
    <script src="client/js/modules/expensesIncomesReportsModule.js"></script>
    <script src="client/js/modules/officeExpensesModule.js"></script>
    <script src="client/js/modules/returnTicketsModule.js"></script>
    <script src="client/js/modules/bookingModule.js"></script>
    <script src="client/js/modules/sequenceModule.js"></script>
    <script src="client/js/modules/fuelExpenseReportModule.js"></script>
    <script src="client/js/modules/invoiceModule.js"></script>
    <script src="client/js/modules/verifyInvoiceModule.js"></script>
    <script src="client/js/modules/gstFiltersModule.js"></script>
    <script src="client/js/modules/suppliersModule.js"></script>
    <script src="client/js/modules/tripReportsModule.js"></script>
    <script src="client/js/modules/cargoBookingModule.js"></script>
    <script src="client/js/modules/cargoBookingChartModule.js"></script>
    <script src="client/js/modules/collectionZoneModule.js"></script>
    <script src="client/js/modules/operatorAccountsModule.js"></script>
    <script src="client/js/modules/staffModule.js"></script>
    <script src="client/js/modules/cargoDashboardModule.js"></script>
    <script src="client/js/modules/cargoBrachSummaryModule.js"></script>
    <script src="client/js/modules/fullTripModule.js"></script>
    <script src="client/js/modules/inventoriesModule.js"></script>
    <script src="client/js/modules/jobsModule.js"></script>
    <script src="client/js/modules/staffComplaintsModule.js"></script>
    <script src="client/js/modules/remindersModule.js"></script>
    <script src="client/js/modules/salaryReportModule.js"></script>
    <script src="client/js/modules/dailyTripModule.js"></script>
    <script src="client/js/modules/expenseModule.js"></script>
    <script src="client/js/modules/serviceConfigurationModule.js"></script>
    <script src="client/js/modules/bankModule.js"></script>
    <script src="client/js/modules/tripSheetModule.js"></script>
    <script src="client/js/modules/documentsUploadModule.js"></script>
    <script src="client/js/modules/preferredStaffModule.js"></script>
    <script src="client/js/modules/busLayoutModule.js"></script>
    <script src="client/js/modules/searchServiceModule.js"></script>


</head>
<body ng-cloak>
<div class="loader" ng-show="loading">
    <img src="client/images/Spinner.svg">
</div>
<div ui-view></div>

<script>

    $(".sidebar-dropdown > a").click(function() {
        $(".sidebar-submenu").slideUp(200);
        if (
            $(this)
                .parent()
                .hasClass("active")
        ) {
            $(".sidebar-dropdown").removeClass("active");
            $(this)
                .parent()
                .removeClass("active");
        } else {
            $(".sidebar-dropdown").removeClass("active");
            $(this)
                .next(".sidebar-submenu")
                .slideDown(200);
            $(this)
                .parent()
                .addClass("active");
        }
    });

    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    $('#category-tabs button').click(function () {
        $(this).find('i').toggleClass('fa fa-times fa fa-th-large')
    });
</script>



</body>
</html>
