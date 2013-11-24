
function LoginC($scope, $http) {
    $scope.user = {};
    $scope.authFail = false;

    $scope.login = function() {
       $http.post("/login", $scope.user)
           .success(function () {
               console.log("success");
               location.replace("/admin");
           })
           .error(function() {
               $scope.authFail = true;
           })
    }
}

