require('lib/setup')
Spine = require('spine')
Logins = require('controllers/logins')
Containers = require('controllers/containers')

class Platform extends Spine.Stack
  className: 'stack'
  controllers:
    login: Logins
    container: Containers


class App extends Spine.Controller
  constructor: ->
    super
    #Spine.Model.host = "http://localhost:8080/console"
    Spine.Model.host = discovery_console_api_host

    @log("Initialized")

    @platform = new Platform
    @append @platform

    @platform.container.active()

    @routes
      '/searches/index': ->
        @platform.container.active()
      '/login': ->
        @platform.login.active()
      '/searches/:id/edit': (params) ->
        @platform.container.searches.sidebar.active(params)
        @platform.container.searches.main.edit.active(params)
      '/searches/:id': (params) ->
        @platform.container.searches.sidebar.active(params)
        @platform.container.searches.main.show.active(params)
      '/searches/:id/stat': (params) ->
        @platform.container.searches.sidebar.active(params)
        @platform.container.searches.main.stat.active(params)
      '/searches/:id/build': (params) ->
        @platform.container.searches.sidebar.active(params)
        @platform.container.searches.main.build.active(params)
      '/searches/:id/query': (params) ->
        @platform.container.searches.sidebar.active(params)
        @platform.container.searches.main.query.active(params)
      '/searches/:id/synonyms': (params) ->
        @platform.container.searches.sidebar.active(params)
        @platform.container.searches.main.synonyms.active(params)
      '/searches/:id/spellings': (params) ->
        @platform.container.searches.sidebar.active(params)
        @platform.container.searches.main.spellings.active(params)


    Spine.Route.setup()

module.exports = App
