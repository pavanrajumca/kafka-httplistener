package com.singtel.nsb.mobile.constants;

public enum Routes {

	POST("direct:post", "post"),
	GET("direct:get", "get"),
	PATCH("direct:patch", "patch"),
	DELETE("direct:delete", "delete"),
	;

	private String route;
	private String routeId;

	Routes(String route, String routeId) {
		this.route = route;
		this.routeId = routeId;
	}
	
	public String getRoute() {
		return this.route;
	}
	
	public String getRouteId() {
		return this.routeId;
	}

}
