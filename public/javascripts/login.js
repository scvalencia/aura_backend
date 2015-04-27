$(document).ready(function(){

var especialidades=["Anesthesia","Cardiovascular Disease","Dermatology","Emergency Medicine","Endocrinology and Metabolism","Family Practice","Gastroenterology","General Practice","Geriatric Medicine","Gynecology","Gynecologic Oncology","Hematology","Infectious Diseases","Internal Medicine","Neonatology"];
$('.errorLogin').hide();
for (var i = 0; i < especialidades.length; i++)
{
    $('#discipline').append('<option value="'+i+'">'+especialidades[i]+'</option>');
}



$(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
  });

    $('.btnLogin').click(function(){
        var idDoctor = parseInt($('.idTxt').val());
        var pswDoctor = $('.passwordTxt').val();
        var toSend = {id : idDoctor, password : pswDoctor};

        var path = '/doctor/authenticate';
        var request;
        request = $.ajax({
            type: 'POST',
            url: path,
            data: JSON.stringify (toSend),
            success: function(data) {
            if (JSON.stringify(data)=="{}")
            {
                $('.errorLogin').show();
            }
            else
            {
                //window.location.href="/search/"+data.id;
                window.location.href="/";
            }
             },
            contentType: "application/json",
            dataType: 'json'
        });

    });

    $('.btnCreate').click(function(){

            var firstNameP = $('#firstName').val();
            var lastNameP = $('#lastName').val();
            var nameP = firstNameP.concat(" ").concat(lastNameP);
            var emailP = $('#email').val();
            var idP = parseInt($('#id').val());
            var pass1P = $('#password1').val();
            var pass2P = $('#password2').val();
            var linkedIn = $('#linkedin').val();
            var dateP = $('.datepicker').val();
            var disciplineP = parseInt($('#discipline').val());
            var genderP = parseInt($('input[name="sex"]:checked').val());

            if (isNaN(parseInt(idP)))
            {
                alert("ID must me a number");
            }
            else if (firstNameP=="")
            {
                alert("First name must not be empty");
            }
            else if (lastNameP=="")
            {
                alert("Last name must not be empty");
            }
            else if (emailP=="")
            {
                alert("Email must not be empty");
            }
            else if (dateP=="")
            {
                alert("Birth date must not be empty");
            }
            else if (pass1P=="")
            {
                alert("Password must not be empty");
            }
            else if (!(pass1P==pass2P))
            {
                alert("Passwords are not equal");
            }
            else
            {
                var toSend = {discipline: disciplineP, name: nameP, gender: genderP, id: idP, date: dateP, password: pass1P, email: emailP, link:linkedIn};
                //alert(JSON.stringify(toSend));
                var path = "/api/doctor";
                $.ajax({
                    type: 'POST',
                    url: path,
                    data: JSON.stringify (toSend),
                    success: function(data) {
                    if (JSON.stringify(data)=="{}")
                    {
                        alert("Sorry, we couldn't process your request, try again later")
                    }
                    else
                    {
                        window.location.href="/";
                    }
                    },
                    contentType: "application/json",
                    dataType: 'json'
                    });
            }

        });

});