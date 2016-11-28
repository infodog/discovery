Spine = require('spine')
Relation = require('spine/lib/relation')

class Spelling extends Spine.Model
  @configure 'Spelling', 'term'

  @belongsTo 'domain', 'models/domain'

module.exports = Spelling