Spine = require('spine')
Domain = require('models/domain')
$ = Spine.$

Main = require('controllers/searches_main')
Sidebar = require('controllers/searches_sidebar')

class Searches extends Spine.Controller
  className: 'searches'

  constructor: ->
    super

    @sidebar = new Sidebar
    @main = new Main

    divide = $('<div />').addClass('vdivide')

    @append @sidebar, divide, @main

#    Domain.bind "ajaxError", (xhr, statusText, error) =>
#      #TODO how to handle
#      @log 'ajaxError'
#      @log statusText
#      jso_wipe()
#      Domain.fetch()

    Domain.bind "save", (newRecord) ->
      $.ajax({
      type: "GET",
      url: discovery_search_host + "/admin/cores?wt=json&action=RELOAD&core=" + newRecord.name
      })

#      TODO check it out
      $.ajax({
      type: "GET",
      url: discovery_search_host + "/admin/cores?wt=json&action=CREATE&name=" + newRecord.name
      })

    Domain.fetch()


module.exports = Searches