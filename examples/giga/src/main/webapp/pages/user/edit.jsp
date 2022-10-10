
<%@ page import="chico.Chico" %>
<%@ page import="giga.Giga" %>


<plsar:if spec="${message != ''}">
	<p class="notify">${message}</p>
</plsar:if>

<h1>Edit Account</h1>

<p>You personal information is kept private.</p>

<plsar:if spec="${user.freshPassword != 'null' && user.freshPassword != ''}">
	<p class="">Old Password : <strong>${user.freshPassword}</strong></p>
	<p>This should be changed!</p>
</plsar:if>

<form action="/users/update/${business.id}/${user.id}" method="post">

	<input type="hidden" name="id" value="${user.id}"/>
	<input type="hidden" name="username" value="${user.username}"/>
	<input type="hidden" name="password" value="${user.password}"/>
	<input type="hidden" name="shipStreet" value="${user.shipStreet}"/>
	<input type="hidden" name="shipStreetDos" value="${user.shipStreetDos}"/>
	<input type="hidden" name="shipCity" value="${user.shipCity}"/>
	<input type="hidden" name="shipState" value="${user.shipState}"/>
	<input type="hidden" name="shipZip" value="${user.shipZip}"/>
	<input type="hidden" name="shipCountry" value="${user.shipCountry}"/>

	<label>Name</label>
	<input type="text" name="name" value="${user.name}"/>

	<label>Phone</label>
	<input type="text" name="phone" value="${user.phone}"/>


	<div class="align-right" style="margin-top:20px;">
		<input type="submit" value="Update!" class="button green"/>
	</div>
</form>



<%if(Chico.hasRole(Giga.SUPER_ROLE)){%>
	<form action="/users/delete/${user.id}" method="post">
		<div id="delete-user-container" style="width:100%;">
			<input type="submit" class="button remove" value="Delete Account" style="width:100% !important;"/>
		</div>
	</form>
<%}%>


