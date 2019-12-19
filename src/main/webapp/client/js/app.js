'use strict';

/* App Module */

var myBus = angular.module('myBus', [
    'ui.router',
    'ngTable',
    'ui.select',
    'angularFileUpload',
    'ngFileUpload'
]);

myBus.config(['$stateProvider', '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('newbooking', {
                level: 1,
                url: '/newbooking',
                templateUrl: 'partials/cargoBooking.tpl.html',
                controller: 'CargoBookingController'
            }).state('viewcargobooking/:id', {
            url: '/viewcargobooking/:id',
            templateUrl: 'partials/cargoBookingDetails.tpl.html',
            controller: 'CargoBookingController'
        }).state('cargobookings', {
            level: 1,
            url: '/cargobookings',
            templateUrl: 'partials/cargoBookingsSearch.tpl.html'
        }).state('cargobookingchart', {
            level: 1,
            url: '/cargobookingchart',
            templateUrl: 'partials/cargoBookingChart.tpl.html',
            controller: 'CargoBookingChartController'
        })
            .state('branchbookingsummary', {
                level: 1,
                url: '/branchbookingsummary',
                templateUrl: 'partials/branchBookingSummary.tpl.html'
            })
            .state('unloadingsheet', {
                level: 1,
                url: '/unloadingsheet',
                templateUrl: 'partials/cargoUnloadingSheet.tpl.html'
            })
            .state('deliverysheet', {
                level: 1,
                url: '/deliverysheet',
                templateUrl: 'partials/cargoDeliverySheet.tpl.html'
            })
            .state('cancellations', {
                level: 1,
                url: '/cancellations',
                templateUrl: 'partials/cargoCancellations.tpl.html'
            })
            .state('collectionzones', {
                url: '/collectionZones',
                templateUrl: 'partials/collectionZones.tpl.html',
                controller: 'CollectionZonesController'
            })
            .state('agents', {
                level: 1,
                url: '/agents',
                templateUrl: 'partials/agents.tpl.html',
                controller: 'AgentController'
            }).state('agentsedit', {
            level: 1,
            url: '/agentsedit/:id',
            templateUrl: 'partials/agentsEdit.tpl.html',
            controller: 'AddAgentController'
        }).state('operatoraccounts', {
            url: '/operatoraccounts',
            templateUrl: 'partials/operatoraccounts.tpl.html',
            controller: 'OperatorAccountsController'
        }).state('addoperatoraccounts', {
            url: '/addoperatoraccounts',
            templateUrl: 'partials/addEditOperatorAccounts.tpl.html',
            controller: 'AddAccountController'
        }).state('editoperatoraccounts', {
            url: '/editoperatoraccounts/:id',
            templateUrl: 'partials/addEditOperatorAccounts.tpl.html',
            controller: 'EditAccountController'
        }).state('invoices', {
            url: '/inovices',
            templateUrl: 'partials/invoice.tpl.html',
            controller: 'InvoiceController'
        }).state('verifyinvoice', {
            url: '/verifyInvoice',
            templateUrl: 'partials/verifyInvoice.tpl.html',
            controller: 'VerifyInvoiceController'
        })
            .state('amenities', {
                level: 1,
                url: '/amenities',
                templateUrl: 'partials/amenities.tpl.html',
                controller: 'AmenitiesController'
            })
            .state('cashbalances', {
                level: 1,
                url: '/cashbalances',
                templateUrl: 'partials/cashBalances.tpl.html',
                controller: 'CashBalancesController'
            })
            .state('bookinganalytics', {
                url: '/bookinganalytics',
                templateUrl: 'partials/bookingtotalsbyphone.tpl.html',
                controller: 'BookingAnalyticsController'
            })
            .state('bookingsbyphone', {
                url: '/bookingsbyphone',
                templateUrl: 'partials/bookingsbyphone.tpl.html',
                controller: 'BookingsByPhoneController',
                params: {phoneNumber: null, totalBookings: 0}
            })
            .state('cashtransfers', {
                level: 1,
                url: '/cashtransfers',
                templateUrl: 'partials/cashTransfers.tpl.html',
                controller: 'cashTransfersController'
            })
            .state('dashboard', {
                level: 1,
                url: '/dashboard',
                templateUrl: 'partials/home.tpl.html',
                controller: 'HomeController'
            })
            .state('cargodashboard', {
                level: 1,
                url: '/cargodashboard',
                templateUrl: 'partials/cargoDashBoard.tpl.html',
                controller: 'CargoDashboardController'
            })
            .state('duereport', {
                level: 1,
                url: '/duereport',
                templateUrl: 'partials/duereport.tpl.html',
                controller: 'DueReportController'
            })
            .state('expensesincomesreports', {
                level: 1,
                url: '/expensesincomesreports',
                templateUrl: 'partials/expensesIncomesReports.tpl.html',
                controller: 'expensesIncomesReportsCtrl'
            })
            .state('expensesincomesreports/:date', {
                url: '/expensesincomesreports/:date',
                templateUrl: 'partials/expensesIncomesReports.tpl.html',
                controller: 'expensesIncomesReportsCtrl'
            })
            .state('fuelexpensereports', {
                level: 1,
                url: '/fuelexpensereports',
                templateUrl: 'partials/fuelExpenseReports.tpl.html',
                controller: 'fuelExpenseReportsCtrl'
            })
            .state('addfuelexpensereports', {
                level: 2,
                url: '/addfuelexpensereports',
                templateUrl: 'partials/addFuelExpense.tpl.html',
                controller: 'editFuelExpenseReportController'
            })
            .state('editfuelexpensereports', {
                level: 2,
                url: '/editfuelexpensereports/:id',
                templateUrl: 'partials/addFuelExpense.tpl.html',
                controller: 'editFuelExpenseReportController'
            })
            .state('fuelexpensereports/:date', {
                level: 2,
                url: '/fuelexpensereports/:date',
                templateUrl: 'partials/fuelExpenseReports.tpl.html',
                controller: 'fuelExpenseReportsCtrl'
            })
            .state('officeduereport/:id', {
                level: 2,
                url: '/officeduereport/:id',
                templateUrl: 'partials/officeduereport.tpl.html',
                controller: 'OfficeDueReportController'
            })
            .state('officeduereport/:id/:date', {
                level: 2,
                url: '/officeduereport/:id/:date',
                templateUrl: 'partials/officeduereportByDate.tpl.html',
                controller: 'OfficeDueByDateReportController'
            })
            .state('officeduereportbyservice/:serviceNumber', {
                level: 2,
                url: '/officeduereportbyservice/:serviceNumber',
                templateUrl: 'partials/officeDueReportByService.tpl.html',
                controller: 'OfficeDueByServiceController'
            })
            .state('officeduereportbyagent/:agentName', {
                level: 2,
                url: '/officeduereportbyagent/:agentName',
                templateUrl: 'partials/officeDueReportByAgent.tpl.html',
            })
            .state('returnTicketsByDate/:date', {
                url: '/returnTicketsByDate/:date',
                templateUrl: 'partials/returnTicketsByDate.tpl.html',
                controller: 'returnTicketsByDateController'
            })
            .state('returnTicketsByAgent/:agent', {
                url: '/returnTicketsByAgent/:agent',
                templateUrl: 'partials/returnTicketsByAgent.tpl.html',
                controller: 'returnTicketsByAgentController'
            })
            .state('returntickets', {
                url: '/returntickets',
                templateUrl: 'partials/returnTickets.tpl.html',
                controller: 'returnTicketsController'
            })
            .state('servicereport', {
                level: 2,
                url: '/servicereport/:id',
                templateUrl: 'partials/serviceReport.tpl.html',
                controller: 'ServiceReportController'
            })
            .state('serviceform', {
                level: 2,
                url: '/serviceform/:id',
                templateUrl: 'partials/serviceform.tpl.html',
                controller: 'ServiceFormController'
            })
            .state('servicereports', {
                level: 1,
                url: '/serviceReports',
                templateUrl: 'partials/serviceReports.tpl.html',
                controller: 'ServiceReportsController'
            })
            .state('serviceReports/:date', {
                level: 2,
                url: '/serviceReports/:date',
                templateUrl: 'partials/serviceReports.tpl.html',
                controller: 'ServiceReportsController'
            })
            .state('shipmentsequence', {
                level: 2,
                url: '/shipmentSequence',
                templateUrl: 'partials/shipmentSequence.tpl.html',
                controller: 'sequenceController'
            })
            .state('pendingreports', {
                level: 1,
                url: '/pendingReports',
                templateUrl: 'partials/pendingReports.tpl.html',
                controller: 'pendingReportController'
            })
            .state('reportstobereviewed', {
                level: 1,
                url: '/reportstobereviewed',
                templateUrl: 'partials/pendingReports.tpl.html',
                controller: 'ReportsToBeReviewedController'
            })
            .state('haltreports', {
                level: 1,
                url: '/haltreports',
                templateUrl: 'partials/haltReports.tpl.html',
                controller: 'HaltReportsController'
            })
            .state('haltReport', {
                level: 2,
                url: '/haltreport/:id',
                templateUrl: 'partials/haltReport.tpl.html',
                controller: 'ServiceReportController'
            })
            .state('servicecombo', {
                level: 1,
                url: '/servicecombo',
                templateUrl: 'partials/serviceCombo.tpl.html',
                controller: 'ServiceComboController'
            })
            .state('tripcombo', {
                level: 1,
                url: '/tripcombo',
                templateUrl: 'partials/tripCombo.tpl.html',
                controller: 'TripComboController'
            })
            .state('cities', {
                url: '/cities',
                level: 1,
                templateUrl: 'partials/cities-list.tpl.html',
                controller: 'CitiesController'
            })
            .state('routes', {
                url: '/routes',
                level: 1,
                templateUrl: 'partials/routes-list.tpl.html',
                controller: 'RoutesController'
            })
            .state('createRoute', {
                url: '/addRoute/:id',
                templateUrl: 'partials/addorUpdateRoute.tpl.html',
            })
            .state('persons', {
                level: 1,
                url: '/persons',
                templateUrl: 'partials/person.html',
                controller: 'PersonController'
            })
            .state('states', {
                url: '/states',
                level: 1,
                templateUrl: 'partials/states.html',
                controller: 'CitiesController'
            })
            .state('payments', {
                level: 1,
                url: '/payments',
                templateUrl: 'partials/payments-list.tpl.html',
                controller: 'PaymentController'
            })
            .state('payment', {
                level: 2,
                url: '/payment',
                templateUrl: 'partials/paymentAdd.tpl.html',
                controller: 'EditPaymentController'
            })
            .state('city/:id', {
                level: 2,
                url: '/city/:id',
                templateUrl: 'partials/boardingpoints-list.tpl.html',
                controller: 'BoardingPointsListController'
            })
            .state('vehicles', {
                level: 1,
                url: '/vehicles',
                templateUrl: 'partials/vehicle-list.tpl.html',
                controller: 'VehicleController'
            }).state('staff', {
            level: 1,
            url: '/staff',
            templateUrl: 'partials/staff-list.tpl.html',
            controller: 'StaffListController'
        }).state('editstaff/:id', {
            level: 2,
            url: '/editstaff/:id',
            templateUrl: 'partials/edit-staff.tpl.html',
            controller: 'EditStaffController'
        })
            .state('createvehicle', {
                level: 2,
                url: '/createvehicle',
                templateUrl: 'partials/vehicle-edit.tpl.html',
                controller: 'EditVehicleController'
            })
            .state('vehicle/:id', {
                level: 2,
                url: '/vehicle/:id',
                templateUrl: 'partials/vehicle-edit.tpl.html',
                controller: 'EditVehicleController'
            })
            .state('officeexpenses', {
                level: 1,
                url: '/officeexpenses',
                templateUrl: 'partials/officeExpenses.tpl.html',
                controller: 'OfficeExpensesController'
            })
            .state('layouts', {
                level: 1,
                url: '/layouts',
                templateUrl: 'partials/buslayout.tpl.html',
                controller: 'BusLayoutController'
            })
            .state('addLayouts', {
                level: 2,
                url: '/addLayouts',
                templateUrl: 'partials/buslayoutedit.tpl.html',
                controller: 'BusLayoutEditController'
            })
            .state('layouts/:id', {
                level: 2,
                url: '/layouts/:id',
                templateUrl: 'partials/buslayoutedit.tpl.html',
                controller: 'BusLayoutEditController'
            })
            .state('services', {
                url: '/services',
                templateUrl: 'partials/busService.tpl.html',
                controller: 'BusServiceController as busServiceCtrl'
            })
            .state('services/:id', {
                level: 3,
                url: '/services/:id',
                templateUrl: 'partials/busServiceEdit.tpl.html',
                controller: 'BusServiceEditController as busServiceEditCtrl',
                resolve: busServiceEditResolver
            })
            .state('roles', {
                level: 1,
                url: '/roles',
                templateUrl: 'partials/roles.tpl.html',
                controller: 'RoleController'
            })
            .state('busdetails', {
                level: 1,
                url: '/busdetails',
                templateUrl: 'partials/busdetails.tpl.html',
                contro2ler: 'BusDetailsController'
            })
            .state('users', {
                level: 1,
                url: '/users',
                templateUrl: 'partials/users.tpl.html',
                controller: 'UsersController'
            })
            .state('user', {
                level: 2,
                url: '/user/',
                templateUrl: 'partials/user-editDetails.tpl.html',
                controller: 'UserAddController'
            })
            .state('useredit', {
                level: 3,
                url: '/user/:id',
                templateUrl: 'partials/user-editDetails.tpl.html',
                controller: 'UpdateUserController'
            })
            .state('plans', {
                level: 2,
                url: '/plans',
                templateUrl: 'partials/agentPlan-details.tpl.html',
                controller: 'AgentPlanController'
            })
            .state('plan', {
                level: 3,
                url: '/plan',
                templateUrl: 'partials/agentPlanEdit-details.tpl.html',
                controller: 'AddAgentPlanTypeController'
            })
            .state('docs', {
                level: 2,
                url: '/docs',
                templateUrl: 'partials/api-docs.tpl.html',
                controller: 'APIDocsController'
            })
            .state('account', {
                level: 2,
                url: '/account',
                templateUrl: 'partials/account.tpl.html',
                controller: 'AccountController'
            })
            .state('trip', {
                level: 2,
                url: '/trip',
                templateUrl: 'partials/trip.tpl.html',
                controller: 'TripController as tripCtrl'
            })
            .state('manageroles', {
                level: 1,
                url: '/manageroles',
                templateUrl: 'partials/managing-roles.tpl.html',
                controller: 'ManagingRolesController'
            })
            .state('booking', {
                level: 2,
                url: '/booking',
                templateUrl: 'partials/booking-info.tpl.html',
                controller: 'BookingController as bookingCtrl'
            })
            .state('inventories', {
                level: 1,
                url: '/inventories',
                templateUrl: 'partials/inventoriesList.tpl.html',
                controller: 'InventoriesController'
            })
            .state('add-editInventory', {
                url: '/add-editInventory/:id',
                templateUrl: 'partials/add-editInventory.tpl.html',
                controller: 'addInventoryController'
            })
            .state('shipments', {
                level: 1,
                url: '/shipments',
                templateUrl: 'partials/shipments.tpl.html',
                controller: 'ShipmentsController'
            })
            .state('shipment', {
                url: '/shipment/:id',
                templateUrl: 'partials/shipmentedit.tpl.html',
                controller: 'EditShipmentController'
            })
            .state('editshipment', {
                url: '/shipment',
                templateUrl: 'partials/shipmentedit.tpl.html',
                controller: 'EditShipmentController'
            })
            .state('branchoffices', {
                level: 1,
                url: '/branchoffices',
                templateUrl: 'partials/branchOffices.tpl.html',
                controller: 'BranchOfficesController'
            })
            .state('branchoffice', {
                url: '/branchoffice/:id',
                templateUrl: 'partials/branchOfficeEdit.tpl.html',
                controller: 'EditBranchOfficeController'
            })
            .state('editbranchoffice', {
                url: '/branchoffice',
                templateUrl: 'partials/branchOfficeEdit.tpl.html',
                controller: 'EditBranchOfficeController'
            })
            .state('gstfilters', {
                url: '/gstfilters',
                templateUrl: 'partials/gstFilters.tpl.html',
                controller: 'GSTFiltersController'
            })
            .state('suppliers', {
                level: 1,
                url: '/suppliers',
                templateUrl: 'partials/suppliers.tpl.html',
                controller: 'SuppliersListController'
            })
            .state('tripreports', {
                level: 1,
                url: '/tripreports',
                templateUrl: 'partials/tripReports.tpl.html',
                controller: 'TripReportsController'
            })
            .state('tripreports/:date', {
                level: 2,
                url: '/tripReports/:date',
                templateUrl: 'partials/tripReports.tpl.html',
                controller: 'TripReportsController'
            })
            .state('updatepassword', {
                level: 1,
                url: '/updatepassword',
                templateUrl: 'partials/updatePassword.tpl.html'
            }).state('serviceincomereport', {
            level: 1,
            url: '/serviceincomereport',
            templateUrl: 'partials/serviceIncomeReport.tpl.html'
        }).state('loadingsheet', {
            level: 1,
            url: '/loadingsheet',
            templateUrl: 'partials/cargoLoadingSheet.tpl.html'
        }).state('fulltrips', {
            level: 1,
            url: '/fulltrips',
            templateUrl: 'partials/fullTrips.tpl.html'
        }).state('addFullTrip', {
            url: '/addFullTrip',
            templateUrl: 'partials/addFullTrip-details.tpl.html'
        }).state('editFullTrip', {
            url: '/editFullTrip/:id',
            templateUrl: 'partials/addFullTrip-details.tpl.html'
        }).state('servcieReportsByService', {
            url: '/servcieReportsByService/:id/:source/:destination',
            templateUrl: 'partials/serviceReports-byService.tpl.html',
            controller: 'ServiceIncomeReportByServiceController'
        }).state('addOfficeExpenses', {
            url: '/addOfficeExpenses',
            templateUrl: 'partials/addOfficeExpenses.tpl.html'
        }).state('updateOfficeExpenses', {
            url: '/updateOfficeExpenses/:id',
            templateUrl: 'partials/addOfficeExpenses.tpl.html'
        }).state('addJob', {
            url: '/addJob/:id',
            templateUrl: 'partials/addorEditJob.tpl.html',
            controller: 'addorEditJobController'
        }).state('jobs', {
            level: 1,
            url: '/jobs',
            templateUrl: 'partials/jobsList.tpl.html',
            controller: 'jobListController'
        }).state('reminders', {
            level: 1,
            url: '/reminders',
            templateUrl: 'partials/reminders.tpl.html',
            controller: 'RemindersController'
        }).state('editreminders', {
            url: '/reminders/:id',
            templateUrl: 'partials/remindersEdit.tpl.html',
            controller: 'EditremindersController'
        }).state('expensestype', {
            level: 1,
            url: '/expense',
            templateUrl: 'partials/expense.tpl.html',
            controller: 'expenseController'
        }).state('staffcomplaints', {
            level: 1,
            url: '/staffComplaints',
            templateUrl: 'partials/staffComplaints.tpl.html',
            controller: 'StaffComplaintsController'
        }).state('addStaffComplaint', {
            url: '/addStaffComplaint',
            templateUrl: 'partials/addEditStaffComplaint.tpl.html',
            controller: 'addEditStaffComplaintController'
        }).state('jobViewByVehicleId', {
            url: '/jobViewByVehicleId/:id',
            templateUrl: 'partials/jobViewByVehicleId.tpl.html'
        }).state('salaryreports', {
            level: 1,
            url: '/salaryReports',
            templateUrl: 'partials/salaryReport.tpl.html',
            controller: 'salaryReportController'
        }).state('dailytrips', {
            level: 1,
            url: '/dailyTrips',
            templateUrl: 'partials/dailyTripsList.tpl.html',
            controller: 'DailyTripsController'
        }).state('addDailyTrip', {
            url: '/add-editDailyTrips/:tripId',
            templateUrl: 'partials/addDailyTrip.tpl.html',
            controller: 'AddDailyTripController'
        }).state('preferredstaff', {
            url: '/preferredstaffList',
            templateUrl: 'partials/PreferredStaffList.tpl.html',
            controller: 'PreferredStaffListController'
        }).state('add-editpreferredStaff', {
            url: '/add-editpreferredStaff/:staffId',
            templateUrl: 'partials/add-editPreferredStaff.tpl.html',
            controller: 'Add-EditPreferredStaffController'
        }).state('searchservice', {
            url: '/searchServices',
            templateUrl: 'partials/searchServices.tpl.html',
            controller: 'SearchServiceController'
        }).state('verificationDetails', {
            url: '/verificationDetails/:verificationId',
            templateUrl: 'partials/verificationDetails.tpl.html',
            controller: 'verificationDetailsController'
        }).state('addservice', {
            level: 1,
            url: '/addService',
            templateUrl: 'partials/addService.tpl.html'
        }).state('banks', {
            level: 1,
            url: '/banks',
            templateUrl: 'partials/banksList.tpl.html',
            controller: 'bankController'
        }).state('bankAdd', {
            url: '/bankAdd',
            templateUrl: 'partials/addEditBank.tpl.html',
            controller: 'addEditBankController'
        }).state('bankEdit', {
            url: '/bankEdit:id',
            templateUrl: 'partials/addEditBank.tpl.html',
            controller: 'addEditBankController'
        }).state('tripsheet', {
            level: 1,
            url: '/tripSheet',
            templateUrl: 'partials/tripSheetList.tpl.html',
            controller: 'tripSheetController'
        }).state('checkList', {
            level: 1,
            url: '/checkList'
        }).state('cargo', {
            level: 1,
            url: '/cargo'
        }).state('analytics', {
            level: 1,
            url: '/analytics'
        }).state('config', {
            level: 1,
            url: '/configuration'
        }).state('addTripSheet', {
            level: 1,
            url: '/addTripSheet',
            templateUrl: 'partials/addEditTripSheet.tpl.html',
            controller: 'addEditTripSheetController'
        }).state('tripSheetReceipts', {
            url: '/tripSheetReceipts:id',
            templateUrl: 'partials/tripSheetReceipts.tpl.html',
            controller: 'receiptsTripSheetController'
        }).state('tripSheetBankCollection', {
            url: '/tripSheetBankCollection:id',
            templateUrl: 'partials/tripSheetBankCollection.tpl.html',
            controller: 'bankTripSheetController'
        }).state('tripSheetPayments', {
            url: '/tripSheetPayments:id',
            templateUrl: 'partials/tripSheetPayments.tpl.html',
            controller: 'paymentsTripSheetController'
        }).state('tripSheetOtherExpenses', {
            url: '/tripSheetOtherExpenses:id',
            templateUrl: 'partials/tripSheetOtherExpenses.tpl.html',
            controller: 'otherTripSheetController'
        }).state('tripSheetTripExpenses', {
            url: '/tripSheetTripExpenses:id',
            templateUrl: 'partials/tripExpenses.tpl.html',
            controller: 'tripExpensesController'
        }).state('editTripSheet', {
            url: '/editTripSheet:id',
            templateUrl: 'partials/addEditTripSheet.tpl.html',
            controller: 'addEditTripSheetController'
        }).state('tripsheetfilters', {
            url: '/tripSheetFilters',
            level: 1,
            templateUrl: 'partials/tripSheetFilters.tpl.html',
            controller: 'addEditTripSheetController'
        }).state('documentsupload', {
            url: '/documentsUploadList',
            level: 1,
            templateUrl: 'partials/documentsUploadList.tpl.html',
            controller: 'DocumentsUploadListController'
        }).state('uploadDocument', {
            url: '/documentsUpload',
            level: 1,
            templateUrl: 'partials/documentsUpload.tpl.html',
            controller: 'DocumentsUploadController'
        }).state('viewJobsByInventory', {
            level: 1,
            url: '/viewJobsByInventory/:inventoryId',
            templateUrl: 'partials/viewJobsByInventory.tpl.html',
            controller: 'viewJobsByInventoryController'
        });
        $urlRouterProvider.otherwise('/');
    }]);

var busServiceEditResolver = {
    layoutNamesPromise: ['busLayoutManager', function (busLayoutManager) {
        return busLayoutManager.getActiveLayoutNames();
    }],
    routeNamesPromise: ['routesManager', function (routesManager) {
        return routesManager.getActiveRouteNames();
    }],
    amenitiesNamesPromise: ['amenitiesManager', function (amenitiesManager) {
        return amenitiesManager.getAmenitiesName();
    }]
};


myBus.run(function ($rootScope, $state, $location, appConfigManager, userManager, opratingAccountsManager) {
    $rootScope.menus = [];
    appConfigManager.fetchAppSettings(function (err, cfg) {
        $rootScope.appConfigManager = appConfigManager;
    }, true);
    userManager.getCurrentUser(function (err, data) {
        if (!err) {
            userManager.getGroupsForCurrentUser();
            myBus.constant('currentuser', data);
            $rootScope.currentuser = data;
            $rootScope.$broadcast("currentuserLoaded");
            opratingAccountsManager.getAccount($rootScope.currentuser.operatorId, function (operatorAccount) {
                $rootScope.operatorAccount = operatorAccount;
            });
        }
    });

});

myBus.config(['$httpProvider', function ($httpProvider) {
    // Interceptor
    $httpProvider.interceptors.push(['$q', '$location', '$rootScope', function ($q, $location, $rootScope) {
        return {
            'request': function (config) {
                $rootScope.loading = true;
                return config;
            },
            'response': function (config) {
                $rootScope.loading = false;
                return config;
            },
            'responseError': function (error) {
                let status = error.status;
                $rootScope.loading = false;
                if ([400, 401, 402, 403, 500].indexOf(status) > -1) {
                    sweetAlert("Error", error.data.message, "error");
                }
            }
        };
    }]);
}]);

myBus.filter('propsFilter', function () {
    return function (items, props) {
        var out = [];

        if (angular.isArray(items)) {
            var keys = Object.keys(props);

            items.forEach(function (item) {
                var itemMatches = false;

                for (var i = 0; i < keys.length; i++) {
                    var prop = keys[i];
                    var text = props[prop].toLowerCase();
                    if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
                        itemMatches = true;
                        break;
                    }
                }

                if (itemMatches) {
                    out.push(item);
                }
            });
        } else {
            // Let the output be the input untouched
            out = items;
        }

        return out;
    };
});
