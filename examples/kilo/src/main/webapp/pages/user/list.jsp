

<h1>Clients</h1>

<plsar:if spec="${message != ''}">
	<p class="notify">${message}</p>
</plsar:if>

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
								<plsar:if spec="${client.name != '' && client.name != 'null'}">
									${client.name}<br/>
								</plsar:if>
								<plsar:if spec="${client.phone != '' && client.phone != 'null'}">
                        			<span class="tiny"><strong>Phone:</strong><br/>
									${client.phone}</span><br/>
								</plsar:if>
								<span class="tiny"><strong>Email:</strong><br/>
									${client.username}</span>


								<plsar:if spec="${client.shipStreet != '' && client.shipStreet != 'null' &&
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
								</plsar:if>
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