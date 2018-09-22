<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Reservas</title>
    %{--<asset:stylesheet src="reserva.css"/>--}%
    %{--<asset:javascript src="reserva.js"/>--}%
    %{--<asset:javascript src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"/>--}%
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>
</head>

<body>
    <div id="app">
        {{ message }}
    </div>
</body>

<script>
    var app = new Vue({
        el: '#app',
        data: {
            message: 'Hello Vue!'
        }
    })
</script>


</html>

