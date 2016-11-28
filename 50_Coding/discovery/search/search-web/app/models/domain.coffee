Spine = require('spine')
Relation = require('spine/lib/relation')
Ajax = require('spine/lib/ajax')

class Domain extends Spine.Model
  @configure 'Domain', 'name', 'indexEndpoint', 'queryEndpoint'

  @extend Spine.Model.Ajax

  @hasMany 'fields', 'models/field'
  @hasMany 'synonyms', 'models/synonym'
  @hasMany 'spellings', 'models/spelling'


  toJSON: (objects) ->
    data = {}
    data = @attributes()
    data['fields'] = @fields().all()
    data['synonyms'] = @synonyms().all()
    data['spellings'] = @spellings().all()
    data

  @fromJSON: (objects) ->
    return unless objects
    if typeof objects is 'string'
      objects = JSON.parse(objects)
    if Spine.isArray(objects)
      (new @(value) for value in objects)
    else
      new @(objects)

  @filter: (query) ->
    return @all() unless query
    query = query.toLowerCase()
    @select (item) ->
      item.name?.toLowerCase().indexOf(query) isnt -1


module.exports = Domain