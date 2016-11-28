Spine = require('spine')

Navigators = require('controllers/navigators')
Searches = require('controllers/searches')

class Containers extends Spine.Controller
	constructor: ->
		super
		@nav = new Navigators
		
		@searches = new Searches

		@append @nav, @searches

    
module.exports = Containers