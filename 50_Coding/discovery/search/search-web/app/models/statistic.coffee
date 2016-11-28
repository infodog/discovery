Spine = require('spine')
Oajax = require('lib/oajax')

class Statistic extends Spine.Model
  @configure 'Statistic', 'numDocs', 'maxDoc', 'version', 'segmentCount', 'current', 'hasDeletions', 'directory'

  @extend(Spine.Events)

  @fromJSON: (objects) ->
    return unless objects
    if typeof objects is 'string'
      objects = JSON.parse(objects)
    if Spine.isArray(objects)
      (new @(value) for value in objects)
    else
      new @(objects.index)

  @fetchRemote: (name) ->
    url = discovery_search_host + "/" + name + '/admin/luke?wt=json&show=index&numTerms=0'
    $.get(url).success(@responseSuccess)


  @responseSuccess: (data, status, xhr) =>
    if Spine.isBlank(data)
      data = false
    else
      data = @fromJSON(data)
    data.save()


module.exports = Statistic