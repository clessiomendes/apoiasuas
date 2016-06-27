<%
    List<Link> linkInstanceListDTO = linkInstanceList
%>

<%@ page import="org.apoiasuas.Link" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'link.label', default: 'Link')}" />
		<title>Atalhos Externos</title>
        <style type="text/css" media="screen">
        #status {
            background-color: #eee;
            border: .2em solid #fff;
            margin: 2em 2em 1em;
            padding: 1em;
            width: 12em;
            float: left;
            -moz-box-shadow: 0px 0px 1.25em #ccc;
            -webkit-box-shadow: 0px 0px 1.25em #ccc;
            box-shadow: 0px 0px 1.25em #ccc;
            -moz-border-radius: 0.6em;
            -webkit-border-radius: 0.6em;
            border-radius: 0.6em;
        }

        .ie6 #status {
            display: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
        }

        #status ul {
            font-size: 0.9em;
            list-style-type: none;
            margin-bottom: 0.6em;
            padding: 0;
        }

        #status li {
            line-height: 1.3;
        }

        #status h1 {
            text-transform: uppercase;
            font-size: 1.1em;
            margin: 0 0 0.3em;
        }

        #page-body {
            margin: 2em 1em 1.25em 18em;
        }

        h2 {
            margin-top: 1em;
            margin-bottom: 0.3em;
            font-size: 1em;
        }

        p {
            line-height: 1.5;
            margin: 0.25em 0;
        }

        #controller-list ul {
            list-style-position: inside;
        }

        #controller-list li {
            line-height: 1.3;
            list-style-position: inside;
            margin: 0.25em 2em;
        }

        @media screen and (max-width: 480px) {
            #status {
                display: none;
            }

            #page-body {
                margin: 0 1em 1em;
            }

            #page-body h1 {
                margin-top: 0;
            }
        }
        </style>
	</head>
	<body>
    %{--<div id="page-body" role="main">--}%

    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="list" action="list">Configurar</g:link></li>
        </ul>
    </div>

    <div id="controller-list" role="navigation">
		<a href="#list-link" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <h1><g:message code="link.label" default="Skip to content&hellip;"/></h1>
		<div id="list-link" class="content scaffold-list" role="main">
            <ul>
				<g:each in="${linkInstanceListDTO}" status="i" var="linkInstance">
                    <g:if test="${linkInstance?.tipo.isUrl()}">
                        <li><g:link target="new" title="${linkInstance.instrucoes}" url="${linkInstance.urlCompleta}" id="${linkInstance.id}">${fieldValue(bean: linkInstance, field: "descricao")}</g:link></li>
                    </g:if>
                    <g:if test="${linkInstance?.tipo.isFile()}">
                        <li><g:link action="downloadFile" title="${linkInstance.instrucoes}" id="${linkInstance.id}"><g:fieldValue bean="${linkInstance}" field="descricao"/></g:link></li>
                    </g:if>
				</g:each>
            </ul>
		</div>
    </div>
    %{--</div>--}%
	</body>
</html>
