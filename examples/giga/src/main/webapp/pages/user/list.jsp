<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Clients</h1>

<c:if test="${not empty message}">
	<p class="notify">${message}</p>
</c:if>

<c:choose>
	<c:when test="${clients.size() > 0}">
			<table>
				<thead>
					<tr>
						<th>Id</th>
						<th>Details</th>
						<th>Sales<br/>Count</th>
						<th>$ Sales</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="client" items="${clients}">
						<tr>
							<td>${client.id}</td>
							<td>
								<c:if test="${client.name != '' && client.name != 'null'}">
									${client.name}<br/>
								</c:if>
								<c:if test="${client.phone != '' && client.phone != 'null'}">
                        			<span class="tiny"><strong>Phone:</strong><br/>
									${client.phone}</span><br/>
								</c:if>
								<span class="tiny"><strong>Email:</strong><br/>
									${client.username}</span>


								<c:if test="${client.shipStreet != '' && client.shipStreet != 'null' &&
											client.shipCity != '' && client.shipCity != null}">
                        			<br/><br/>
									<span class="tiny"><strong>Address:</strong><br/>
											${client.shipStreet}<br/>
											${client.shipStreetDos}<br/>
											${client.shipCity}<br/>
											${client.shipState}<br/>
											${client.shipZip}<br/>
											${client.shipCountry}<br/>
									</span>
								</c:if>
							</td>
							<td>${client.salesCount}</td>
							<td>$${siteService.getPriceTres(client.salesTotal)}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</c:when>
	<c:when test="${clients.size() == 0}">
		<p class="notify">Have no fear, clients are on their way!</p>
	</c:when>
</c:choose>