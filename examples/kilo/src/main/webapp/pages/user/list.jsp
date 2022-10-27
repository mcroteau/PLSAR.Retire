

<h1>Clients</h1>

<ocean:if spec="${message != ''}">
	<p class="notify">${message}</p>
</ocean:if>

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
					<ocean:each var="client" items="${clients}">
						<tr>
							<td>${client.id}</td>
							<td>
								<ocean:if spec="${client.name != '' && client.name != 'null'}">
									${client.name}<br/>
								</ocean:if>
								<ocean:if spec="${client.phone != '' && client.phone != 'null'}">
                        			<span class="tiny"><strong>Phone:</strong><br/>
									${client.phone}</span><br/>
								</ocean:if>
								<span class="tiny"><strong>Email:</strong><br/>
									${client.username}</span>


								<ocean:if spec="${client.shipStreet != '' && client.shipStreet != 'null' &&
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
								</ocean:if>
							</td>
							<td>${client.salesCount}</td>
							<td>$${siteService.getPriceTres(client.salesTotal)}</td>
						</tr>
					</ocean:each>
				</tbody>
			</table>
		</div>
	</c:when>
	<c:when test="${clients.size() == 0}">
		<p class="notify">Have no fear, clients are on their way!</p>
	</c:when>
</c:choose>