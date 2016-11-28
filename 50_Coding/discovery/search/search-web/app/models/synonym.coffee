Spine = require('spine')
Relation = require('spine/lib/relation')

class Synonym extends Spine.Model
  @configure 'Synonym', 'term', 'synonyms'

  @belongsTo 'domain', 'models/domain'

module.exports = Synonym