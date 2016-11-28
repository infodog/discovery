Spine = require('spine')

class Navigators extends Spine.Controller
	events:
		'click #signout': 'signout'
		'click #search_menu' : 'seachIndex'
	
	constructor: ->
		super
		
		@html require('views/navigator')
		
	signout: ->
		@navigate('/login', true)	
	
	seachIndex: ->
		@navigate('/searches/index', true)	
    
module.exports = Navigators