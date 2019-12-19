angular.module('myBus').factory('cancelManager', function ( $log, $window,$state,$filter) {

    return {
        cancel: function (theForm) {

            if(theForm.$dirty) {
                    var state=$state.$current.name;
                    var subStr = "/";
                    if(!(angular.equals(state.indexOf(subStr),-1)))
                    {
                    state= state.substring(0,state.indexOf('/'));
                        this.swalAlert(state);
                    }  else if((angular.equals(state.indexOf(subStr)), -1)){
                        var state=$state.$current.name;
                        this.swalAlert(state);
                }
            } else {
                $window.history.back();
            }
        },
        swalAlert : function(state){
            swal({
                    title: "You want to stop creating " + state,
                    text: "You'll loose the unsaved data!",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, Stop!",
                    closeOnConfirm: true
                },
                function (isConfirm) {
                    if (isConfirm) {
                        $window.history.back();
                    }
                }
            );
        }
    }
  });