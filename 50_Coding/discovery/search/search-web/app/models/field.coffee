Spine = require('spine')
Relation = require('spine/lib/relation')


class Field extends Spine.Model
	@configure 'Field', 'fieldName', 'type', 'search', 'facet', 'result', 'default', 'source', 'remove'
	
	@belongsTo 'domain', 'models/domain'
	
  
module.exports = Field