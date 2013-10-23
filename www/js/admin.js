l = console.log;

app = {};

//var db = $.couch.db("data");
//db.urlPrefix = "http://localhost:4958/data";
//db.view("myapp/by_type", function(doc) {
//});


function DocsCtrl($scope, DB) {
    l(DB);

    return;
    db.view("myapp/by_type", {include_docs: true, success: function(docs) {
        $scope.$apply(function() {
          $scope.docs = $.map(docs.rows, function(d) { return d.doc });
        });

        app.docs = $scope.docs;
    }});
    $scope.updateKey = function(e, doc, key, val) {
        delete(doc[key]);
        var newKey = $(e.target).html();
        doc[newKey] = val;
        l("in update", $(e.target).html());
    }
}


angular.module('Admin', ['ui.utils']).
  directive('editjson', function($compile) {
      l("init");

      return {
        replace: true,
        restrict: 'A',
        transclude: true,
        scope: { doc: '=' },
        template: '<div>Hello, {{doc}}!</div>'
    };
  });

