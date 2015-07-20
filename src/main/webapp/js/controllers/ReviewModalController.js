/**
 * Created by xing on 6/30/15.
 */
app.controller('ReviewModalController', function($scope, $modalInstance, action) {

    $scope.action = action;
    $scope.reaction = {
        id: $scope.action.id,
        permalink: $scope.action.permalink,
        replied: $scope.action.replied
    }

    $scope.ok = function () {
        $scope.reaction.replied = true;
        $modalInstance.close($scope.reaction);
    };

    $scope.cancel = function () {
        $scope.reaction.replied = false;
        $modalInstance.dismiss('cancel');
    };
});
