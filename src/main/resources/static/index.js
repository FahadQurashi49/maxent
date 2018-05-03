


$(document).ready(function () {
    $("#title").val("initialize angularjs controller after append some html into dom");
    $("#body").val("can someone please help me convert the code below to jquery    <pre><code>var xmlhttp;              if (window.xmlhttprequest)             {                 // code for ie7+  firefox  chrome  opera  safari                 xmlhttp = new xmlhttprequest();             }             else             {                 // code for ie5  ie6                 xmlhttp = new activexobject( microsoft.xmlhttp );             }              xmlhttp.open( get    http://www.my.com   true);             xmlhttp.setrequestheader( myheader    hello );             xmlhttp.send();              xmlhttp.onreadystatechange = function()             {                 if (xmlhttp.readystate == 4)                 {                     document.getelementbyid( responsetext ).innerhtml = xmlhttp.responsetext;                 }                }          }  </code></pre>");

    $("#submitBtn").off("click").on("click", function(){
        var data = {
            "title": $("#title").val(),
            "body": $("#body").val()

        };
        $.ajax({
            type: "POST",
            url: "evaluate",
            contentType: "application/json",
            data: JSON.stringify(data),

            success: function (msg) {
                $("#tag").html("Tag: " + msg);
            }
        });

    });

    $("#train").off("click").on("click", function(){
        $.ajax({
            type: "GET",
            url: "train_model",
            success: function (msg) {
                $("#train-status").html("Status: " + msg);
            }
        });

    });

    $("#test").off("click").on("click", function(){
        $.ajax({
            type: "GET",
            url: "eval_test_data",
            success: function (msg) {
                $("#test-status").html("Status: " + msg);
            }
        });

    });

});
