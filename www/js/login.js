
function LoginC($scope, $http) {
    $scope.user = {};
    $scope.authFail = false;

    $scope.login = function() {
       $http.post("/login", $scope.user)
           .success(function () {
               document.location = "/admin";
           })
           .error(function() {
               $scope.authFail = true;
           })
    }
}

