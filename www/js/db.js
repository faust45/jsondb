l = console.log;

var app = angular.module("PersonalMenuServices", ['ngResource']);
app.factory("Place", Place);
app.factory("User", User);


function User($resource) {
    return $resource("/admin/profile",
            {}, 
            {'profile':  {method:'GET', isArray:false}});
}

function Place($resource) {
    var options = {
    };
    var Doc = $resource("/admin/places/:id",
                {id: '@id'}, {});
    
    return Doc;
};
