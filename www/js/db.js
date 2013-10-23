l = console.log;

var app = angular.module("PersonalMenuServices", ['ngResource']);
app.factory("Data", DB);

function DB($resource) {
    var options = {
    };
    var Doc = $resource("/places/:id");

    return Doc;
}
