package com.dtupay.dtupayapi.dtupayapi.rest.endpoints;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("/v1/manager")
public class ManagerEndpoint {
	@GET
	public Response doGet() {
		return Response.ok("manager").build();
	}
}
