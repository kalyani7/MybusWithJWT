<!DOCTYPE html>
<html lang="en" ng-app="wizzard">
<head>
    <meta charset="UTF-8">
    <base href="/">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Whizzard</title>
    <script src="client/node_modules/angular/angular.js"></script>
    <link rel="shortcut icon" type="image/x-icon" href="client/img/icon.ico"/>
    <link rel="stylesheet" href="client/css/style.css" type="text/css">
    <link rel="stylesheet" href="client/css/select1.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="client/node_modules/sweetalert/dist/sweetalert.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
    <script src="https://cdn.jsdelivr.net/jquery.validation/1.16.0/jquery.validate.min.js"></script>
    <script src="client/node_modules/sweetalert/dist/sweetalert.min.js"></script>
    <link href="https://fonts.googleapis.com/css?family=Arvo" rel="stylesheet">
    <link href="client/node_modules/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- Favicon: Change to whatever you like within the "assets/images" folder. -->
    <style>
        .card-signin {
            border: 0;
            border-radius: 1rem;
            box-shadow: 0 0.5rem 1rem 0 rgba(0, 0, 0, 0.1);
        }

        .card-signin .card-title {
            margin-bottom: 2rem;
            font-weight: 300;
            font-size: 1.5rem;
        }

        .card-signin .card-body {
            padding: 2rem;
        }

        .form-signin {
            width: 100%;
        }

        .form-signin .btn {
            font-size: 80%;
            border-radius: 5rem;
            letter-spacing: .1rem;
            font-weight: bold;
            padding: 1rem;
            transition: all 0.2s;
        }

        .refer-form {
            min-width: 700px !important;
            min-height: 595px !important;
        }

        @media (max-width: 480px) {
            .refer-form {
                min-width: 350px !important;
                min-height: 705px !important;
            }
        }
    </style>
</head>

<body class="app sidebar-mini rtl pace-done sidenav-toggled" data-gr-c-s-loaded="false" ng-app="wizzard"
      ng-controller="referAFriendController">
<div class="loader" ng-if="loading">
    <div id="loader-wrapper">
        <div id="loader1">
            <div id="loader3">
                <div id="loader5">
                    <div id="loader7">
                        <img id="loader-img" type="image/x-icon" ng-src="http://admin.whizzard.in/client/img/icon.ico"
                             width="40px" height="40px"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<section class="material-half-bg">
    <div class="cover"></div>
</section>
<section class="login-content">
    <div class="logo">
        <img class="login-logo" ng-src="http://admin.whizzard.in/client/img/logo.png">
    </div>
    <div class="login-box refer-form">
        <form class="forget-form fields-up" name="referredSignUpForm" novalidate
              ng-submit="submit(referredSignUpForm.$valid)">
            <h4 class="login-head">
                <i class="fa fa-sign-in fa-lg fa-fw"></i>Sign Up
            </h4>
            <input type="hidden" id="referrelId" name="referrelId" value="123">
            <div class="form-group">
                <div class="col-md-12 text-center">
                    <span style="font-size: 17px;"><b>Referred By:</b>&nbsp{{referredBy}}</span>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-sm-6">
                        <div class="has-feedback"
                             ng-class="{ 'has-warning' : referredSignUpForm.fullName.$invalid && !referredSignUpForm.fullName.$pristine, 'has-success' : referredSignUpForm.fullName.$valid && !referredSignUpForm.fullName.$pristine }">
                            <input type="text" id="fullName" name="fullName"
                                   class="form-control form-control-bottom form-control-sm"
                                   ng-model="user.fullName"
                                   placeholder="Enter FullName" ng-minLength="5" autocomplete="off" required>
                            <label for="fullName" class="control-label">FULLNAME <b
                                    class="mandatory-field">*</b></label>
                            <i class="fa form-control-feedback" aria-hidden="true"
                               ng-class="{ 'fa-exclamation-triangle' : referredSignUpForm.fullName.$invalid && !referredSignUpForm.fullName.$pristine, 'fa-check' : referredSignUpForm.fullName.$valid && !referredSignUpForm.fullName.$pristine }"></i>
                            <span class="help-warning"
                                  ng-show="referredSignUpForm.fullName.$invalid && referredSignUpForm.fullName.$dirty">Please Enter at least 5 characters</span>
                            <!--                        <span ng-show="referredSignUpForm.fullName.$dirty && referredSignUpForm.fullName.$error.maxlength">maximum 5 charecters allowed</span>-->
                        </div>
                    </div>
                    <div class="col-sm-6">


                        <div class="has-feedback"
                             ng-class="{ 'has-error' : referredSignUpForm.mobileNo.$invalid && !referredSignUpForm.mobileNo.$pristine, 'has-success' : referredSignUpForm.mobileNo.$valid && !referredSignUpForm.mobileNo.$pristine }">
                            <input type="text" id="phoneNumber" name="mobileNo"
                                   class="form-control form-control-bottom form-control-sm"
                                   ng-minlength="10" ng-maxlength="20" placeholder="Enter Mobile Number"
                                   autocomplete="off"
                                   required ng-model="user.phoneNumber" ng-pattern="/^[0-9]{1,10}$/">
                            <label for="phoneNumber" class="control-label">MOBILE.NO
                                <b class="mandatory-field">*</b>
                            </label>
                            <i class="fa form-control-feedback" aria-hidden="true"
                               ng-class="{ 'fa-times' : referredSignUpForm.mobileNo.$invalid && !referredSignUpForm.mobileNo.$pristine, 'fa-check' : referredSignUpForm.mobileNo.$valid && !referredSignUpForm.mobileNo.$pristine }"></i>
                            <span class="help-block"
                                  ng-show="referredSignUpForm.mobileNo.$error.required && referredSignUpForm.mobileNo.$dirty">Mobile number is a required field</span>
                            <span class="help-block"
                                  ng-show="referredSignUpForm.mobileNo.$error.pattern || referredSignUpForm.mobileNo.$error.maxlength || referredSignUpForm.mobileNo.$error.minlength">Only numbers allowed and Please enter a 10 digit number</span>
                            <!--                            <span class="help-block"-->
                            <!--                                  ng-show="referredSignUpForm.mobileNo.$error.maxlength || referredSignUpForm.mobileNo.$error.minlength">Please enter a 10 digit number</span>-->
                        </div>


                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-sm-6">


                        <div class="has-feedback"
                             ng-class="{ 'has-error' : referredSignUpForm.password.$invalid && !referredSignUpForm.password.$pristine && referredSignUpForm.password.$dirty, 'has-success' : referredSignUpForm.password.$valid && !referredSignUpForm.password.$pristine}">
                            <input type="password" class="form-control form-control-bottom form-control-sm"
                                   id="password" name="password"
                                   ng-model="user.password" placeholder="Enter Password" autocomplete="off"
                                   ng-minLength="6" required
                                   ng-change="confirmPasswordC(user.password,user.confirmPassword)">
                            <label class="control-label">PASSWORD <b class="mandatory-field">*</b></label>
                            <i class="fa form-control-feedback" aria-hidden="true"
                               ng-class="{ 'fa-times' : referredSignUpForm.password.$invalid && !referredSignUpForm.password.$pristine, 'fa-check' : referredSignUpForm.password.$valid && !referredSignUpForm.password.$pristine }"></i>
                            <span class="help-block"
                                  ng-show="referredSignUpForm.password.$invalid && referredSignUpForm.password.$dirty">
                                Please Enter at least 6 characters</span>
                        </div>


                    </div>
                    <div class="col-sm-6">


                        <div class="has-feedback"
                             ng-class="{ 'has-error' : referredSignUpForm.confirmPassword.$invalid && !referredSignUpForm.confirmPassword.$pristine && referredSignUpForm.confirmPassword.$dirty, 'password-error': (passwordErrorValid) ? true : false,
                             'has-success' : referredSignUpForm.confirmPassword.$valid && !referredSignUpForm.confirmPassword.$pristine, 'password-success': ((passwordSuccessValid) ? true : false)}">
                            <input stopccp type="password" class="form-control form-control-bottom form-control-sm"
                                   id="ConfirmPassword"
                                   name="confirmPassword" ng-model="user.confirmPassword"
                                   placeholder="Enter Confirm Password" autocomplete="off" ng-minLength="6"
                                   ng-change="confirmPasswordC(user.password,user.confirmPassword)" required>
                            <label for="ConfirmPassword" class="control-label">CONFIRM PASSWORD <b
                                    class="mandatory-field">*</b></label>
                            <i class="fa form-control-feedback" aria-hidden="true"
                               ng-class="{ 'fa-times' : (referredSignUpForm.confirmPassword.$invalid && !referredSignUpForm.confirmPassword.$pristine) ? true : (passwordErrorValid) ? true : false,
                                           'fa-check' : (referredSignUpForm.confirmPassword.$valid && !referredSignUpForm.confirmPassword.$pristine) ? ((passwordSuccessValid) ? true : false) : false}"></i>
                            <span class="help-block" ng-show="passwordValid">Passwords do not match</span>
                            <!--                            <span class="help-block" ng-if="incorrect">Passwords do not match</span>-->
                        </div>


                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-sm-6 floating-label">


                        <div class="has-feedback"
                             ng-class="{ 'has-error' : referredSignUpForm.cityName.$invalid && !referredSignUpForm.cityName.$pristine, 'has-success' : referredSignUpForm.cityName.$valid && !referredSignUpForm.cityName.$pristine }">
                            <select id="cityName" name="cityName" onclick="this.setAttribute('value', this.value);"
                                    value=""
                                    class="floating-select form-control form-control-bottom form-control-sm"
                                    ng-model="user.cityId"
                                    ng-change="getSites(user.cityId)">
                                <!--                            <option value="" selected>Select City</option>-->
                                <option ng-repeat="citie in cities" ng-value="citie.id">{{citie.name}}</option>
                            </select>
                            <label for="cityName" class="control-label">SELECT CITY</label>
                            <i class="fa form-control-feedback" aria-hidden="true" style="top: 12px; right: 20px"
                               ng-class="{ 'fa-times' : referredSignUpForm.cityName.$invalid && !referredSignUpForm.cityName.$pristine, 'fa-check' : referredSignUpForm.cityName.$valid && !referredSignUpForm.cityName.$pristine }"></i>
                        </div>


                    </div>
                    <div class="col-sm-6 floating-label">

                        <div class="has-feedback"
                             ng-class="{ 'has-error' : referredSignUpForm.siteName.$invalid && !referredSignUpForm.siteName.$pristine, 'has-success' : referredSignUpForm.siteName.$valid && !referredSignUpForm.siteName.$pristine }">
                            <select id='siteName' name="siteName" value=""
                                    onclick="this.setAttribute('value', this.value);"
                                    class="floating-select form-control form-control-bottom form-control-sm"
                                    ng-model="user.siteId">
                                <option ng-repeat="site in sites" ng-value="site.id">{{site.name}}</option>
                            </select>
                            <label for="siteName" class="control-label">SELECT SITE <b
                                    class="mandatory-field">*</b></label>
                            <i class="fa form-control-feedback" aria-hidden="true" style="top: 12px; right: 20px"
                               ng-class="{ 'fa-times' : referredSignUpForm.siteName.$invalid && !referredSignUpForm.siteName.$pristine, 'fa-check' : referredSignUpForm.siteName.$valid && !referredSignUpForm.siteName.$pristine }"></i>
                        </div>
                        <span style="color: #ff0000;" ng-if="sites.length === 0">No Sites for the selected City</span>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-sm-12">


                        <div class="has-feedback"
                             ng-class="{ 'has-error' : referredSignUpForm.email.$invalid && !referredSignUpForm.email.$pristine && referredSignUpForm.email.$dirty, 'has-success' : referredSignUpForm.email.$valid && !referredSignUpForm.email.$pristine}">
                            <input id="email" type="email" class="form-control form-control-bottom form-control-sm"
                                   placeholder="Enter Email"
                                   name="email" ng-model="user.email" required/>
                            <label for="email" class="control-label">EMAIL <b class="mandatory-field">*</b></label>
                            <i class="fa form-control-feedback" aria-hidden="true"
                               ng-class="{ 'fa-times' : referredSignUpForm.email.$invalid && !referredSignUpForm.email.$pristine, 'fa-check' : referredSignUpForm.email.$valid && !referredSignUpForm.email.$pristine }"></i>
                            <span class="help-block"
                                  ng-if="referredSignUpForm.email.$invalid && referredSignUpForm.email.$dirty">Please
                                Enter a valid email address</span>
                        </div>


                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-sm-12">
                        <div class="has-feedback"
                             ng-class="{ 'has-error' : referredSignUpForm.seleRole.$invalid && !referredSignUpForm.seleRole.$pristine && referredSignUpForm.seleRole.$dirty, 'has-success' : referredSignUpForm.seleRole.$valid && !referredSignUpForm.seleRole.$pristine}">
                            <label style="margin-bottom: 5px !important;">Select Role <b
                                    class="mandatory-field">*</b></label>
                            <div class="form-check animated-radio-button" ng-repeat="role in roles">
                                <label class="form-check-label" for="{{role}}">
                                    <input class="form-check-input" type="radio" ng-model="user.userRole"
                                           ng-value="role"
                                           id="{{role}}" name="seleRole">
                                    <span class="label-text">
                                        {{role || '___' }}
                                    </span>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div class="alert alert-danger col-md-12" role="alert" ng-show="allError.length">
                        <ul>
                            <li ng-repeat="error in allError">{{error.message}}</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class=" btn-container">
                <button class="btn btn-sm btn-all-shadow waves-effect waves-light btn-cus btn-block"
                        ng-click="signup()">
                    <i class="fa fa-registered fa-lg fa-fw" aria-hidden="true"></i>Register
                </button>
            </div>
            <div class=" mt-3" style="display: none;">
                <p class="semibold-text mb-0">
                    <a data-toggle="flip" style="color: #007bff; cursor: pointer">
                        <i class="fa fa-angle-left fa-fw"></i> Back to Login
                    </a>
                </p>
            </div>
        </form>
    </div>
</section>
</body>
<script type="text/javascript">
    var wizzard = angular.module('wizzard', []);
    wizzard.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode({
            enabled: true,
            requireBase: false
        });
    }]);

    wizzard.controller('referAFriendController', function ($scope, $http, $location) {
        $scope.user = {}
        $scope.loading = false
        var code = $location.search().code;
        $scope.roles = ['ASSOCIATE', 'DRIVER', 'DRIVER_AND_ASSOCIATE', 'LABOURER', 'SITE_SUPERVISOR'];
        $scope.getUser = function () {
            $http({
                method: 'get',
                url: '/api/noauth/findUser/' + $location.search().code
            }).then(function (response) {
                $scope.referredBy = response.data.fullName;
            }, function (error) {
            })
        };
        $scope.getCities = function () {
            $http({
                method: 'post',
                url: '/api/noauth/getCities'
            }).then(function (response) {
                $scope.cities = response.data;
            }, function (error) {
            })
        };
        $scope.getCities();
        $scope.getSites = function (cityId) {
            $http({
                method: 'post',
                url: '/api/noauth/getSites',
                data: JSON.stringify({'cityId': cityId})
            }).then(function (response) {
                $scope.sites = response.data;
            }, function (error) {
            })
        };

        $scope.confirmPasswordC = function (password, confirmPassword) {
            // $scope.passwordValid = true;
            if (password != confirmPassword) {
                // swal("Error!", "Passwords do not match", "error");
                $scope.passwordErrorValid = true;
                $scope.passwordSuccessValid = false;
                $scope.passwordValid = true;
                $scope.passwordConfirmValid = false
                // return false;
            } else {
                $scope.passwordErrorValid = false;
                $scope.passwordSuccessValid = true;
                $scope.passwordValid = false;
                $scope.passwordConfirmValid = true
            }
            // return true
        };


        $scope.submit = function (isValid) {
            $scope.loading = true;
            var params = $scope.user;

            if ($scope.passwordConfirmValid) {
                if (isValid) {
                    $http({
                        method: 'post',
                        url: '/api/noauth/referAFriend/' + code,
                        data: params
                    }).then(function (response) {
                        if (response) {
                            $scope.user = {};
                            $scope.loading = false;
                            if ($scope.loading === false) {
                                swal("Success!", "Registered successfully!", "success");
                            }
                        }
                        $scope.allError = [];
                    }, function (error) {
                        $scope.allError = [];
                        $scope.allError.push(error.data);
                        $scope.loading = false;
                    })
                } else {
                    swal("Error!", "Please enter all mandatory fields in the form", "error");
                    $scope.loading = false;
                }
            } else {
                swal("Error!", "Please enter all mandatory fields in the form", "error");
                $scope.loading = false;
            }

        };
        $scope.getUser();
    }).directive('stopccp', function() {
        return {
            scope: {},
            link:function(scope,element){
                element.on('cut copy paste', function (event) {
                    event.preventDefault();
                })
            }
        };
    });
</script>
</html>

