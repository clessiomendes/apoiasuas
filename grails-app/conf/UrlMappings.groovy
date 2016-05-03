class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "403"(view: '/403')
        "/"(controller: "inicio")
        "500"(view:'/error')
	}
}
