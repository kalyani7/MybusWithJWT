
angular.module('myBus.paymentModule', ['ngTable', 'ui.bootstrap'])
.factory('FormSubmitter', FormSubmitter)
.factory('EBSFormSubmitter', EBSFormSubmitter)
.directive('formSubmitter', formSubmitterDirective)
.directive('ebsFormSubmitter', ebsFormSubmitterDirective)

.controller("PaymentController", function($rootScope, $http,$scope,$modal, paymentManager,FormSubmitter,EBSFormSubmitter){
	
	console.log("In PaymentController");
	
	$scope.headline = "Proceed to pay";
	
	$scope.paymentsDetails = "Payments and Refund Details "; 
	
	$scope.payment = {};
	
	$scope.payments =[];
	
	$scope.paymentToBeRefund = {};
	
    $scope.paymentButtonClicked = function(){
	 	paymentManager.proceedToPay($scope.payment,function(data){
	 		console.log('Payment response',data);
	 		if(data.paymentType == "EBS")
	 			EBSFormSubmitter.ebsSubmit(data)
	 		else
	 			FormSubmitter.payuSubmit(data)
	 	});
    };
    
    $scope.paymentButtonResetFields= function(){
    	$scope.payment = {};
    };
    
    $scope.getAllPaymentDetails = function(){
    	paymentManager.getAllPayments(function(data){
    		$scope.payments=data;
    	});
    };
    
    $scope.getAllPaymentDetails();
    
    $scope.paymentButtonForRefund= function(paymentid){
    	 var modalInstance = $modal.open({
             templateUrl : 'payment-refund-amount.html',
             controller : 'PaymentRefuntController',
             resolve : {
            	 pID : function(){
                     return paymentid;
                 }
             }
         });
    	
    };
    isInputValid = function(paymentStatus,refundStatus){
     	return false;
    };
    
}).controller("PaymentRefuntController",function($rootScope, $http,$scope,$modal,$modalInstance, paymentManager,pID){
	
	$scope.pID="";
	
	$scope.refundResponse = {
			pID:pID,
			refundAmount:"",
			disc:""
	};
	
	$scope.getRefundAmount = function(){
		paymentManager.getRefundAmount($scope.refundResponse.pID,function(data){
			$scope.refundResponse.refundAmount = data
		})
		
	}
	
	$scope.getRefundAmount()
	
	 $scope.getAllPaymentDetails = function(){
	    paymentManager.getAllPayments(function(data){
	    	$scope.payments=data;
	    });
	 };
	    
	$scope.proceedToRefund= function(refundResponse){
    	
    	paymentManager.processToRefund(refundResponse,function(data){
    		$modalInstance.close(data);
    		$scope.getAllPaymentDetails();
    	})
    };
    
    
    $scope.setpID = function(pID){
    	$scope.pID=pID;
    };
    $scope.setpID(pID);
    
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
        return ($scope.refundResponse.refundAmount || '') !== '' &&
            ($scope.refundResponse.disc || '') !== '';
    };
});

function FormSubmitter($rootScope, $sce) {
	
	return {
		payuSubmit: payuSubmit
	}

	function payuSubmit(params) {
		var url = $sce.trustAsResourceUrl(params.paymentGateways.pgRequestUrl);
		console.log('params',params,url);
		$rootScope.$broadcast('form.payuSubmit', {
			params: params,
			url : url 	
		});
	}
}

function EBSFormSubmitter($rootScope, $sce) {
	
	return {
		ebsSubmit: ebsSubmit
	}

	function ebsSubmit(params) {
		var url = $sce.trustAsResourceUrl(params.paymentGateways.pgRequestUrl);
		console.log('params',params,url);
		$rootScope.$broadcast('form.ebsSubmit', {
			params: params,
			url : url 	
		});
	}
}

function formSubmitterDirective($timeout) {
    return {
        restrict: 'E',
        replace: true,
        template:'<form name="payForm" class = "payForm" action="{{ url }}" method="POST">' +
    	    	'<input type="hidden" name="firstname" value="{{ paymentForm.firstName }}" />'+
    	    	'<input type="hidden" name="lastname" value="{{ paymentForm.lastName }}" />'+
    	    	'<input type="hidden" name="surl" value="{{ paymentForm.paymentGateways.pgCallbackUrl }}" />'+
    	    	'<input type="hidden" name="phone" value="{{ paymentForm.phoneNo }}" />'+
    	    	'<input type="hidden" name="key" value="{{ paymentForm.paymentGateways.pgAccountID }}" />'+
    	    	'<input type="hidden" name="hash" value ="{{ paymentForm.hashCode }}" />'+
    	    	'<input type="hidden" name="curl" value="{{ paymentForm.paymentGateways.pgCallbackUrl }}" />'+
    	    	'<input type="hidden" name="furl" value="{{ paymentForm.paymentGateways.pgCallbackUrl }}" />'+
    	    	'<input type="hidden" name="txnid" value="{{ paymentForm.merchantRefNo }}" />'+
    	    	'<input type="hidden" name="productinfo" value="bus" size="64"/>'+
    	    	'<input type="hidden" name="amount" value="{{ paymentForm.amount }}" />'+
    	    	'<input type="hidden" name="pg" value="" />'+
    	    	'<input type="hidden" name="email" value="{{ paymentForm.email }}" />'+
    	    	'<input type="hidden" name="Bankcode" value="" />'+
    	    	'<input type="hidden" name="enforce_paymethod" value="creditcard|debitcard|netbanking|cashcard|Emi" />'+
    	    	'</form>',
	    	
    	    	
        link: function($scope, $elementType, $attrs) {
            $scope.$on('form.payuSubmit', function(event, data) {
                $scope.paymentForm = data.params;
                $scope.url = data.url;

                console.log('auto submitting form...',data,$elementType);

                $timeout(function() {
                	var submitEvent = $(payForm)
                	submitEvent.submit();
                })
            })
        }
    }
}
function ebsFormSubmitterDirective($timeout) {
	return {
        restrict: 'E',
        replace: true,
        template:'<form name="order" action="{{ url }}" method="post"  name="formToEBS" id="formToEBS">'+
	        '<input type="hidden" name="account_id" value="{{paymentEbsForm.paymentGateways.pgAccountID}}"/>'+
	        '<input type="hidden" name="address" value="{{paymentEbsForm.address}}"/>'+
	        '<input type="hidden" name="algo" value="{{paymentEbsForm.algo}}"/>'+
	        '<input type="hidden" name="amount" value="{{paymentEbsForm.amount}}"/>'+
	        '<input type="hidden" name="channel" value="{{paymentEbsForm.channel}}"/>'+
	        '<input type="hidden" name="city" value="{{paymentEbsForm.city}}"/>'+
	        '<input type="hidden" name="country" value="{{paymentEbsForm.country}}"/>'+
	        '<input type="hidden" name="currency" value="{{paymentEbsForm.currency}}"/>'+
	        '<input type="hidden" name="description" value="{{paymentEbsForm.description}}"/>'+
	        '<input type="hidden" name="email" value="{{paymentEbsForm.email}}"/>'+
	        '<input type="hidden" name="mode" value="{{paymentEbsForm.mode}}"/>'+
	        '<input type="hidden" name="name" value="{{paymentEbsForm.firstName}}"/>'+
	        '<input type="hidden" name="phone" value="{{paymentEbsForm.phoneNo}}"/>'+
	        '<input type="hidden" name="postal_code" value="{{paymentEbsForm.postalCode}}"/>'+
	        '<input type="hidden" name="reference_no" value="{{paymentEbsForm.merchantRefNo}}"/>'+
	        '<input type="hidden" name="return_url" value="{{paymentEbsForm.paymentGateways.pgCallbackUrl}}"/>'+
	        '<input type="hidden" name="ship_address" value="{{paymentEbsForm.address}}"/>'+
	        '<input type="hidden" name="ship_city" value="{{paymentEbsForm.city}}"/>'+
	        '<input type="hidden" name="ship_country" value="{{paymentEbsForm.country}}"/>'+
	        '<input type="hidden" name="ship_name" value="{{paymentEbsForm.firstName}}"/>'+
	        '<input type="hidden" name="ship_phone" value="{{paymentEbsForm.phoneNo}}"/>'+
	        '<input type="hidden" name="ship_postal_code" value="{{paymentEbsForm.postalCode}}"/>'+
	        '<input type="hidden" name="ship_state" value="{{paymentEbsForm.state}}"/>'+
	        '<input type="hidden" name="state" value="{{paymentEbsForm.state}}"/>'+
	        '<input type="hidden" name="secure_hash" value="{{paymentEbsForm.hashCode}}"/>'+
	        '</form>',
        	
			 link: function($scope, $elementType, $attrs) {
		            $scope.$on('form.ebsSubmit', function(event, data) {
		                $scope.paymentEbsForm = data.params;
		                $scope.url = data.url;

		                console.log('auto submitting form...',data,$elementType);

		                $timeout(function() {
		                	var submitEvent = $(formToEBS)
		                	submitEvent.submit();
		                })
		            })
		        }
	}
}