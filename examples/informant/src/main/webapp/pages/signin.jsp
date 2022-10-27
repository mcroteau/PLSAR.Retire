
<kakai:if spec="${message != ''}">
    ${message}
</kakai:if>

<h1>Signin</h1>
<form action="/authenticate" method="post">
    <label>Phone or Email</label>
    <input type="text" name="credential"/>

    <label>Password</label>
    <input type="text" name="password"/>

    <input type="submit" value="Signin"/>
</form>