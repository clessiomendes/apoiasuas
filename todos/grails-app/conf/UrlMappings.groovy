class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/api/$controller/$id"{
            action = [GET: "show", PUT: "update", DELETE: "delete"]
            parseRequest = false
            constraints {
                // apply constraints here
            }
        }

        "/api/$controller"{
            action = [GET: "list", POST: "create"]
            parseRequest = false
            constraints {
                // apply constraints here
            }
        }

/*
    GET	/books/${id}/edit	edit
    POST	/books	        save
    PUT	/books/${id}	    update
    DELETE	/books/${id}	delete

        delete "/$controller/$id(.$format)?"(action: 'delete')
        get "/$controller(.$format)?"(action: 'index')
        get "/$controller/$id(.$format)?"(action: 'show')
        post "/$controller(.$format)?"(action: 'save')
        put "/$controller/$id(.$format)?"(action: 'update')
*/

/*
//        GET	/books/${id}	    show
        "/$controller/$id"(action: "show", method: "GET")
//        GET	/books/create	    create
        "/$controller/create"(action: "create", method: "GET")

        "/$controller/$id"(action: "show", method: "GET")
        "/$controller/$id"(action: "show", method: "GET")

        "/reserva"(resources:'reserva')
*/
        "403"(view: '/403')
        "/"(controller: "inicio")
        "500"(view:'/error')
	}
}
