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

    <link rel="stylesheet" href="client/assets-new/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="client/css/ionicons.min.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/owl.carousel.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/owl.theme.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/flexslider.css" type="text/css">
    <link rel="stylesheet" href="client/assets-new/css/main.css" type="text/css">

    <script src="client/node_modules/jquery/dist/jquery.min.js" type="text/javascript"></script>
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
    <script src="client/node_modules/ui-select/dist/select.min.js"></script>
    <script src="client/node_modules/angular-file-upload/dist/angular-file-upload.js" type="text/javascript"></script>

    <script src="client/node_modules/ng-file-upload/dist/ng-file-upload.js" type="text/javascript"></script>

    <script src="client/node_modules/angular-moment/angular-moment.js"></script>
    <script src="client/js/services/appConfigManager.js"></script>
    <script src="client/js/modules/usersModule.js"></script>
    <script src="client/js/modules/operatorAccountsModule.js"></script>


</head>

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
            <%--<a class="navbar-brand" href="index.html" title="HOME"><i class="ion-android-bus"></i> Sri Krishna <span>Travels</span></a>--%>
        </div> <!-- /.navbar-header -->

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav navbar-right">
                <li class="active"><a href="#">Home</a></li>
                <li><a href="#">about</a></li>
                <li><a href="#">services</a></li>
                <li><a href="#">contact</a></li>
                <li><div id="loginContainer"><a href="#" id="loginButton" data-toggle="modal" data-target="#loginModal"><span>Login</span></a>


                    <div id="loginModal" class="modal fade hidden-sm hidden-md hidden-lg" role="dialog">
                        <div class="modal-dialog modal-xs">

                            <!-- Modal content-->
                            <div class="modal-content">
                                <h4>Login</h4>
                                <div class="modal-body">
                                    <div id="mob-loginBox">
                                        <form id="mob-loginForm" action="perform_login" method='POST'>
                                            <div class="login-grids">
                                                <div class="login-grid-left">
                                                    <fieldset id="mob-body">
                                                        <fieldset>
                                                            <label for="email">Username</label>
                                                            <input type='text' name='username' value='' >
                                                        </fieldset>
                                                        <fieldset>
                                                            <label for="password">Password</label>
                                                            <input type='password' name='password'>
                                                        </fieldset>
                                                        <input name="submit" type="submit" value="submit" >
                                                        <label for="checkbox"><input type="checkbox" id="checkbox"> <i>Remember me</i></label>
                                                    </fieldset>
                                                    <span style="color:red;">
                                        <c:if test="${not empty error}">
                                            <div class="error">${error}</div>
                                        </c:if>
                                        <c:if test="${not empty msg}">
                                            <div class="msg">${msg}</div>
                                        </c:if>
                                    </span>
                                                </div>
                                            </div>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                        </form>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                </div></li>
            </ul> <!-- /.nav -->
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container -->
</nav>

<!-- Home -->
<div class="header">
    <div id="myCarousel" class="carousel slide" data-ride="carousel">
        <!-- Indicators -->
        <ol class="carousel-indicators">
            <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
            <li data-target="#myCarousel" data-slide-to="1"></li>
            <li data-target="#myCarousel" data-slide-to="2"></li>
            <li data-target="#myCarousel" data-slide-to="3"></li>
        </ol>

        <!-- Wrapper for slides -->
        <div class="carousel-inner">
            <div class="item active">
                <img src="../../client/assets-new/images/banner5.jpg" alt="Banner" class="banner-img">
            </div>
            <div class="item">
                <img src="../../client/assets-new/images/banner10.png" alt="Banner" width="1000" height="900">
                <%--	<div class="intro container">
                        <div class="inner-intro">
                            <h1 class="header-title">
                                Our newly launched service
                            </h1>
                            <p class="header-sub-title">
                                Launching 2+1 AC Sleeper
                            </p>
                            <button class="btn custom-btn">
                                Hyderabad - Rajamundry
                            </button>
                        </div>
                    </div>--%>
            </div>

            <div class="item">
                <img src="../../client/assets-new/images/banner8.jpg" alt="Banner" class="banner-img">
                <%--	<div class="intro">
                        <div class="inner-intro">
                            <h1 class="header-title">
                                Our newly launched service
                            </h1>
                            <p class="header-sub-title">
                                Launching 2+1 AC Sleeper
                            </p>
                            <button class="btn custom-btn">
                                Chennai - Rajamundry
                            </button>
                        </div>
                    </div>--%>
            </div>

            <div class="item">
                <img src="../../client/assets-new/images/banner1.jpg" alt="Banner" class="banner-img">
            </div>
        </div>

        <!-- Left and right controls -->
        <a class="left carousel-control" href="#myCarousel" data-slide="prev">
            <span class="glyphicon glyphicon-chevron-left"></span>
            <span class="sr-only">Previous</span>
        </a>
        <a class="right carousel-control" href="#myCarousel" data-slide="next">
            <span class="glyphicon glyphicon-chevron-right"></span>
            <span class="sr-only">Next</span>
        </a>
    </div>
</div>
<div class="move-text">
    <div class="marquee">Todays's Services: <a href="#"> Nellore To Hyderabad </a>|<a href="#"> Hyderabad To Chennai </a>|<a href="#"> Ongole To Hyderabad </a>|<a href="#"> Hyderabad To Chennai Special </a>| </div>
    <script type="text/javascript" src="client/assets-new/js/jquery.marquee.min.js"></script>
    <script>
        $('.marquee').marquee({ pauseOnHover: true });
        //@ sourceURL=pen.js
    </script>
</div>

<!-- Find a Bus -->
<section class="tour section-wrapper container">
    <h2 class="section-title">
        Find a Bus
    </h2>
    <p class="section-subtitle">
        Where would you like to go?
    </p>
    <div class="row">
        <div class="col-md-3 col-sm-6">
            <form role="form" class="form-dropdown">
                <div class="form-group">
                    <label for="sel1">Select list (select one):</label>
                    <select class="form-control border-radius" id="sel1">
                        <option>Hyderabad</option>
                        <option>Chennai</option>
                        <option>Mumbai</option>
                    </select>
                </div>
            </form>
        </div>

        <div class="col-md-3 col-sm-6">
            <div class="input-group">
                <input type="text" class="form-control border-radius border-right" placeholder="Arrival"/>
                <span class="input-group-addon border-radius custom-addon">
						<i class="ion-ios-calendar"></i>
					</span>
            </div>
        </div>

        <div class="col-md-3 col-sm-6">
            <div class="input-group">
                <input type="text" class="form-control border-radius border-right" placeholder="Departure"/>
                <span class="input-group-addon border-radius custom-addon">
						<i class="ion-ios-calendar"></i>
					</span>
            </div>
        </div>

        <div class="col-md-3 col-sm-6">
            <div class="btn btn-default border-radius custom-button">
                Search
            </div>
        </div>
    </div>
</section> <!-- /.bus -->


<!-- Why Choose Us? -->
<section class="offer section-wrapper">
    <div class="container">
        <h2 class="section-title">
            Why Choose Us?
        </h2>
        <p class="section-subtitle">
            Lorem Ipsum is simply dummy text of the industry.
        </p>
        <div class="row">
            <div class="col-sm-3 col-xs-6">
                <div class="offer-item">
                    <div class="icon">
                        <i class="ion-social-usd"></i>
                    </div>
                    <h3>
                        Affordable Pricing
                    </h3>
                    <p>
                        Class aptent taciti sociosutn tora torquent conub nost reptos himenaeos.
                    </p>
                </div>
            </div> <!-- /.col-md-3 -->

            <div class="col-sm-3 col-xs-6">
                <div class="offer-item">
                    <div class="icon">
                        <i class="ion-ios-home"></i>
                    </div>
                    <h3>
                        High class Service
                    </h3>
                    <p>
                        Class aptent taciti sociosutn tora torquent conub nost reptos himenaeos.
                    </p>
                </div>
            </div> <!-- /.col-md-3 -->

            <div class="col-sm-3 col-xs-6">
                <div class="offer-item">
                    <div class="icon">
                        <i class="ion-android-bus"></i>
                    </div>
                    <h3>
                        Luxury Transport
                    </h3>
                    <p>
                        Class aptent taciti sociosutn tora torquent conub nost reptos himenaeos.
                    </p>
                </div>
            </div> <!-- /.col-md-3 -->

            <div class="col-sm-3 col-xs-6">
                <div class="offer-item">
                    <div class="icon">
                        <i class="ion-ios-locked"></i>
                    </div>
                    <h3>
                        Highest Security
                    </h3>
                    <p>
                        Class aptent taciti sociosutn tora torquent conub nost reptos himenaeos.
                    </p>
                </div>
            </div> <!-- /.col-md-3 -->
        </div> <!-- /.row -->
    </div> <!-- /.container -->
</section>


<!-- Top Destinations -->
<section class="visit section-wrapper">
    <div class="container">
        <h2 class="section-title">
            Top Destinations
        </h2>
        <p class="section-subtitle">
            Lorem Ipsum is simply dummy text of the industry.
        </p>

        <div class="owl-carousel visit-carousel" id="">
            <div class="item">
                <img src="../../client/assets-new/images/visit-1.png" alt="visit-image" class="img-responsive visit-item">
                <span>sample text</span>
            </div>
            <div class="item">
                <img src="../../client/assets-new/images/visit-2.png" alt="visit-image" class="img-responsive visit-item">
            </div>
            <div class="item">
                <img src="../../client/assets-new/images/visit-3.png" alt="visit-image" class="img-responsive visit-item">
            </div>
            <div class="item">
                <img src="../../client/assets-new/images/visit-1.png" alt="visit-image" class="img-responsive visit-item">
            </div>
            <div class="item">
                <img src="../../client/assets-new/images/visit-2.png" alt="visit-image" class="img-responsive visit-item">
            </div>
            <div class="item">
                <img src="../../client/assets-new/images/visit-3.png" alt="visit-image" class="img-responsive visit-item">
            </div>
        </div>
    </div> <!-- /.container -->
</section> <!-- /.destinations -->

<div class="offer-cta">
    <div class="container">
        <div class="offering">
            <div class="percent">
                <span>10%</span> off
            </div>
            <div class="FTour">
                on <strong>Online Booking</strong>
            </div>
            <a class="btn btn-default price-btn" href="#">
                see our price
            </a>
        </div> <!-- /.offering -->
    </div> <!-- /.container -->
</div> <!-- /.offer-cta -->

<section class="additional-services section-wrapper">
    <div class="container">
        <h2 class="section-title">
            Additional services
        </h2>
        <p class="section-subtitle">
            Lorem Ipsum is simply dummy text of the industry.
        </p>
        <div class="row">
            <div class="col-md-4 col-sm-6">
                <div class="custom-table">
                    <img src="../../client/assets-new/images/add-srvc-1.png" alt="" class="add-srvc-img">
                    <div class="add-srvc-detail">
                        <h4 class="add-srvc-heading">
                            CARGO
                        </h4>
                        <p class="add-srvc">
                            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                        </p>
                    </div> <!-- /.add-srvc-detail -->
                </div> <!-- /.custom-table -->
            </div> <!-- /.col-md-4 col-sm-6 -->

            <div class="col-md-4 col-sm-6">
                <div class="custom-table">
                    <img src="../../client/assets-new/images/add-srvc-2.png" alt="" class="add-srvc-img">
                    <div class="add-srvc-detail">
                        <h4 class="add-srvc-heading">
                            TOURISM
                        </h4>
                        <p class="add-srvc">
                            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                        </p>
                    </div> <!-- /.add-srvc-detail -->
                </div> <!-- /.custom-table -->
            </div> <!-- /.col-md-4 col-sm-6 -->

            <div class="col-md-4 col-sm-6">
                <div class="custom-table">
                    <img src="../../client/assets-new/images/add-srvc-3.png" alt="" class="add-srvc-img">
                    <div class="add-srvc-detail">
                        <h4 class="add-srvc-heading">
                            BUS CHARTER
                        </h4>
                        <p class="add-srvc">
                            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                        </p>
                    </div> <!-- /.add-srvc-detail -->
                </div> <!-- /.custom-table -->
            </div> <!-- /.col-md-4 col-sm-6 -->

        </div> <!-- /.row -->
    </div> <!-- /.container -->
</section> <!-- /.Additional-services -->

<h2 class="section-title">
    Our Top Partners
</h2>
<div class="section-wrapper sponsor">
    <div class="container">
        <div class="owl-carousel sponsor-carousel">
            <div class="item">
                <a href="#">
                    <img src="../../client/assets-new/images/sp-1.png" alt="sponsor-brand" class="img-responsive sponsor-item">
                </a>
            </div>
            <div class="item">
                <a href="#">
                    <img src="../../client/assets-new/images/sp-1.png" alt="sponsor-brand" class="img-responsive sponsor-item">
                </a>
            </div>
            <div class="item">
                <a href="#">
                    <img src="../../client/assets-new/images/sp-1.png" alt="sponsor-brand" class="img-responsive sponsor-item">
                </a>
            </div>
            <div class="item">
                <a href="#">
                    <img src="../../client/assets-new/images/sp-1.png" alt="sponsor-brand" class="img-responsive sponsor-item">
                </a>
            </div>
            <div class="item">
                <a href="#">
                    <img src="../../client/assets-new/images/sp-1.png" alt="sponsor-brand" class="img-responsive sponsor-item">
                </a>
            </div>
            <div class="item">
                <a href="#">
                    <img src="../../client/assets-new/images/sp-1.png" alt="sponsor-brand" class="img-responsive sponsor-item">
                </a>
            </div>
        </div> <!-- /.owl-carousel -->
    </div> <!-- /.container -->
</div> <!-- /.sponsor -->

<div class="subscribe section-wrapper">
    <%--<a class="brand-logo" href="index.html" title="HOME"><i class="ion-android-bus"></i> Sri Krishna <span>Travel</span></a>--%>
    <p class="subscribe-now">
        Subscribe To Hear More About Exciting offers
    </p>
    <div class="container">
        <div class="row">
            <div class="col-md-4 col-sm-6 col-md-offset-4 col-sm-offset-3">
                <div class="input-group">
                    <input type="email" class="form-control border-radius" placeholder="Email address">
                    <span class="input-group-btn">
							<button class="btn btn-default border-radius custom-sub-btn" type="button">DONE</button>
						</span>
                </div><!-- /input-group -->
            </div>
        </div>
    </div>



    <ul class="social-icon">
        <li><a href="#"><i class="ion-social-twitter"></i></a></li>
        <li><a href="#"><i class="ion-social-facebook"></i></a></li>
        <li><a href="#"><i class="ion-social-linkedin-outline"></i></a></li>
        <li><a href="#"><i class="ion-social-googleplus"></i></a></li>
    </ul>
</div> <!-- /.subscribe -->


<footer>
    <div class="container">
        <div class="row">
            <div class="col-xs-4">
                <div class="text-left">
                    &copy; Copyright
                </div>
            </div>

            <div class="top">
                <a href="#header">
                    <i class="ion-arrow-up-b"></i>
                </a>
            </div>

        </div>
    </div>
</footer>



</body>
</html>