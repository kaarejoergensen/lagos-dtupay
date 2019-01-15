package com.dtupay.dtupayapi.dtupayapi.rest.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("/v1/manager")
public class ManagerEndpoint {

	private DTUPayEngine engine;

	public ManagerEndpoint(){
		engine = new DTUPayEngine();
	}


	@GET
	public Response getUsers() {
		return null;
	}
}
