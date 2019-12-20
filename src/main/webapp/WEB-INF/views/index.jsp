<!DOCTYPE html>
<html lang="en" ng-app="myBus">

<html lang="en-US">
<!--<![endif]-->
<head>
    <!-- meta -->
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
    <script src="client/node_modules/angular-ui-router/release/angular-ui-router.min.js"></script>
    <script src="client/assets-new/js/menu_jquery.js" type="text/javascript"></script>
    <script src="client/assets-new/js/bootstrap.min.js"></script>
    <script src="client/assets-new/js/owl.carousel.min.js"></script>
    <script src="client/assets-new/js/jquery.flexslider.js"></script>
    <script src="client/assets-new/js/script.js"></script>
    <script src="client/node_modules/ng-table/bundles/ng-table.js"></script>
    <script src="client/js/app.js"></script>

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
