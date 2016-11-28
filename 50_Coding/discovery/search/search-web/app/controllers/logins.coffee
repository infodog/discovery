Spine = require('spine')
$      = Spine.$

class Logins extends Spine.Controller
	elements:
		'#userName': 'userName'
		'#password': 'password'
		
	events:
		'click .btn': 'submit'	
	
	constructor: ->
		super
		@html require('views/login')()
		@log('logins')
		
		
	submit: (e) ->
		e.preventDefault()
		@log(@userName.val())
		@log(@password.val())
		@log('login')
		
		@navigate('/searches/index', true)
    
module.exports = Logins