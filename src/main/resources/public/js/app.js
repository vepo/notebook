var notebookApp = angular.module('notebook', ["ngRoute"]);
function FolderController($http, $scope, $location) {
    var folder = $location.$$path;
    console.log(folder);
    $http.get('/api/folder/list', { params: { folder: folder } }).then((response) => {
        $scope.files = response.data.contents;
    });
    $scope.files = [];
    $scope.open = (file) => {
        console.log(file);
        if (file.folder) {
            $location.path((folder + '/' + file.filename).slice(1));
        } else {
            $location.path((folder + '/' + file.filename).slice(1));
        }
    };
    $scope.canExecute = (file) => file.folder || file.filename.endsWith(".ipynb");
    $scope.newNotebook = () => {
        var notebookName = prompt("Notebook name");
        if (notebookName) {
            $http.post('/api/notebook/create', { name: notebookName, folder: folder })
                .then((response) => {
                    $scope.files = response.data.contents;
                });
        }
    };
    $scope.newFolder = () => {
        var folderName = prompt("Folder name");
        if (folderName) {
            $http.post('/api/folder/create', { name: folderName, folder: folder })
                .then((response) => {
                    $scope.files = response.data.contents;
                });
        }
    };
    $scope.isRoot = () => folder != '/';
    $scope.goUpFolder = () => {
        $location.path(folder.slice(1, folder.lastIndexOf('/')));
    };
}
function EditController() { }
function SwitchController($http, $scope, $location) {
    if ($location.$$path.endsWith(".ipynb")) {
        return EditController()
    } else {
        return FolderController($http, $scope, $location)
    }
}
notebookApp.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
    $routeProvider.otherwise({
        templateUrl: function ResolveTemplate() {
            if (window.location.href.endsWith(".ipynb")) {
                return '/html/edit.html';
            } else {
                return '/html/folder.html';
            }
        },
        controller: "SwitchController"
    });
});
notebookApp.controller('FolderController', FolderController)
    .controller('EditController', EditController).controller('SwitchController', SwitchController);