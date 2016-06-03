var re =
{
	int:/^[0-9]*[1-9][0-9]*$/,
	legalAccount:/^[\w-]+$/
}
function validate(formid){
    var validate = true;
	$("#"+formid+" :input.required").each(function(){
	    if(this.value=="" || this.value.length < 0){
	       $(this).nextAll(".error-msg").html("请输入必填项.");
	       validate =  false;
           return;
	     }else{
	     	validate = true;
	     }
   });
	if(!validate){
		return false;
	}

	$("#"+formid+" :input.int").each(function(){
		if ( !re.int.test($(this).val()) ) {
			$(this).nextAll(".error-msg").html("请输入正整数.");
			validate =  false;
			return ;
		}else{
			validate = true;
		}
	});

	if(!validate){
		return false;
	}
	$("#"+formid+" :input.legalAccount").each(function(){
		if ( !re.legalAccount.test($(this).val()) ) {
			$(this).nextAll(".error-msg").html("非法渠道账号.");
			validate =  false;
			return ;
		}else{
			validate = true;
		}
	});
	if(!validate){
		return false;
	}
   return validate;
}

function initForm(formid){
 		$("#"+formid+" :input.required").each(function(){
 			$(this).nextAll(".f-not-null").remove();
            var $required = $("<strong class='f-ft-size-12 f-ft-color-e05012 f-not-null'>*</strong>"); //创建元素
            $(this).parent().append($required); //然后将它追加到文档中
        });
        
        $("#"+formid+" :input").each(function(){
           $(this).blur(function(){
			    var className = $(this).attr("class");
			    if(className && className.indexOf("required") != -1){
					if($(this).val()=="" || $(this).val().length < 0){
						$(this).nextAll(".error-msg").html("请输入必填项.");
						return;
					}else{
						$(this).nextAll(".error-msg").html("");
					}
				}
			   if(className && className.indexOf("int") != -1){
				   if( !re.int.test($(this).val())){
					   $(this).nextAll(".error-msg").html("请输入正整数.");
					   return;
				   }else{
					   $(this).nextAll(".error-msg").html("");
				   }
			   }
			   if(className && className.indexOf("legalAccount") != -1){
				   if( !re.legalAccount.test($(this).val())){
					   $(this).nextAll(".error-msg").html("非法渠道账号");
					   return;
				   }else{
					   $(this).nextAll(".error-msg").html("");
				   }
			   }
           });
        });
}

function reset(formid){
		$("#"+formid+" input").val("");
		$("#"+formid+" select").each(function(){
			$(this)[0].options[0].selected=true;
			$(this).change();
		});
}