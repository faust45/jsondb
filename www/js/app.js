l = console.log;

app = angular.module('PersonalMenu', ['ui.utils', 'ui.bootstrap', 'PersonalMenuServices', 'Files']);

app.run(function($rootScope, $http, $location) {
   $rootScope.log = console.log;
   $rootScope.signout = function() {
       $http.delete('/admin/signout').success(function() {
           location.reload();
       });
   };
});

app.filter('normalize', function() {
  return function(input) {
      var value = input.replace(/\<.*?\>/g, "").replace(/\&.*?\;/g, "");
      return value;
  }
});

app.filter('smallphoto', function() {
  return function(input) {
      var value;

      if(input) {
          value = input["small"]
          if(value) {
              value = "https://s3.amazonaws.com/locudata/"+value;
          }

      }

      return value || 'http://placehold.it/50x50';
  }
});


function MenuCtrl($scope, User, Place) {
    var menuChanged = false;
    $scope.editMode = false;
    var profile = User.profile(function() {
        $scope.menu = Place.get({id: profile.places[0]}, function(place) {
            $scope.$watch('menu', function() {
                 menuChanged = true;
            }, true);
        });
    });
     
    var colors = ['success', 'info', 'warning', 'danger'];

    var colors = ['success', 'info', 'warning', 'danger'];
    $scope.colorOn = function(index) {
        return colors[index % 4];
    }

    var saveMenu = setInterval(function() {
        if (menuChanged) {
            menuChanged = false;
            angular.copy($scope.menu).$save();
        }
    }, 3000);
};

app.directive('contenteditable', function($compile) {
  return {
    restrict: 'A', // only activate on element attribute
    require: '?ngModel', // get a hold of NgModelController
    link: function(scope, element, attrs, ngModel) {

        if(!ngModel) return; // do nothing if no ng-model

        // Specify how UI should be updated
        ngModel.$render = function() {
            element.html(ngModel.$viewValue || '');
        };

        // Listen for change events to enable binding
        element.bind('blur keyup change', function() {
            scope.$apply(read);
        });
        //read(); // initialize

        // Write data to the model
        function read() {
            ngModel.$setViewValue(element.html());
            //ngModel.$ModelValue = element.html();
        }
      }
    };
})
