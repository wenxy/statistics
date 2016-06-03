/**
 * Created by fish on 2016/3/28.
 */
$(document).ready(function(){
    $.ajax({
        type:"POST",
        url:"/Ajax/listGames",
        success:function(result){
            console.log(result);
            $('#autocomplete').autocomplete({
                lookup: result,
                minChars:0,
                onSelect: function (s) {
                    $("#autoGameHidden").val(s.data);
                }
            });
        }
    });
})