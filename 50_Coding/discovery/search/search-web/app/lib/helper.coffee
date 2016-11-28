Spine = require('spine')
$ = Spine.$

tableToJSON (table) ->
	data = [];
	headers = [];
	
	for name in table.rows[0].cells
		headers.push(name.attr("name"))
			
	for row,i in table.rows when i > 0
		rowData = {}
		for cell,j in row.cells
			type = cell.children().attr("type")
			type = type.toString() unless type
			
			if type is "checkbox"
				v = $("input:checkbox:checked", cell) .val()
				if v
					rowData[ headers[j] ] = true
				else
					rowData[ headers[j] ] = true
					
			else
				rowData[ headers[j] ] = $(cell).children().val();
			
		data.push(rowData)	
	data


