Spine = require('spine')

class SearchesFields extends Spine.Controller
	
	
	events:
		"click .item": "click"
		"click i": "remove"
	
	constructor: ->
		super
		throw "@item required" unless @item
		@item.bind("update", @render)
		@item.bind("destroy", @remove)
		
	render: (item) =>
		@item = item if item

		@html(@template(@item))
		    
	template: (items) ->
		require('views/field_edit')(items)

	remove: ->
		@log("remove")
		@log(@item.id)
		@el.remove() if confirm("Are you sure?")

	click: (e) ->
		@log('searchesFields clicked')
		@log(@item.id)
			
			
module.exports = SearchesFields