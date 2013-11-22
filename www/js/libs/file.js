
l = console.log;
files = angular.module('Files', []);

files.directive('filedrag', function ($parse) {
    return {
        restrict: 'A',
        require: '?ngModel',
        link: function (scope, elem, attrs, ngModel) {
        //    l("debug", attr.ngModel, $scope.$parent.$eval(attr.ngModel));
        function dragOver(e) {
            e.stopPropagation();
            e.preventDefault();
            elem.addClass('drag-over');
        }

        function dragEnd(e) {
            e.stopPropagation();
            e.preventDefault();
            elem.removeClass('drag-over');
        }

        function drop(e){
            dragEnd(e);

            var file = e.dataTransfer.files[0], reader = new FileReader();
            reader.onload = function (e) {
                req = createRequestObject();
                  
                req.upload.onprogress  = onprogress;
                req.onreadystatechange = onreadystatechange;
                req.open("POST", '/admin/upload/dishes', true);
                req.setRequestHeader("Content-Type",  'application/octet-stream');
                req.setRequestHeader("X-File-Name",  file.name);

                req.sendAsBinary(reader.result);
            };
            reader.readAsBinaryString(file);

            return false;
        };

        function onprogress(e) {
            if (e.lengthComputable) {
                var percentage = Math.round((e.loaded * 100) / e.total);
            }
        }

        function onreadystatechange() {
            if (req.readyState == 4) { onComplete(); }
        }

        function onComplete() {
            if (req.status == 201 || req.status == 200) {
                var data = eval("("+req.responseText+")");
                 
                scope.$parent.$apply(function() {
                  ngModel.$setViewValue(data);
                });
            }
        }

        elem.bind('dragleave', dragEnd);
        elem.bind('drop', drop);
        elem.bind('dragover', dragOver);
    }
    };
});

function createRequestObject() {
    return new XMLHttpRequest();
}
