Spine = require('spine')
Domain = require('models/domain')
Synonym = require('models/synonym')
Spelling = require('models/spelling')
Statistic = require('models/statistic')
Field = require('models/field')
Itemlist = require('controllers/itemlist')
SearchesFields = require('controllers/searches_fields')
$ = Spine.$

class Show extends Spine.Controller
  className: 'show'

  events:
    'click .edit': 'edit'
    'click .stat': 'stat'
    'click .build': 'build'
    'click .query': 'query'
    'click .synonyms': 'synonyms'
    'click .optimize': 'optimize'
    'click .spellings': 'spellings'


  constructor: ->
    super

    @active @change

  render: ->
    @html require('views/show')(@item)

  change: (params) =>
    if Domain.exists(params.id)
      @item = Domain.find(params.id)
      @render()
    else
      @navigate('/searches/index')

  edit: ->
    @navigate('/searches', @item.id, 'edit')

  stat: ->
    Statistic.fetchRemote(@item.name)
    @navigate('/searches', @item.id, 'stat')

  build: ->
    @navigate('/searches', @item.id, 'build')

  query: ->
    @navigate('/searches', @item.id, 'query')

  synonyms: ->
    @navigate('/searches', @item.id, 'synonyms')

  spellings: ->
    @navigate('/searches', @item.id, 'spellings')


  optimize: ->
    $.ajax({
    type: "POST",
    url: discovery_search_host + "/" + @item.name + "/update?optimize=true&waitFlush=true&wt=json",
    dataType: 'json',
    contentType: 'application/json'
    })

class Stat extends Spine.Controller
  className: 'stat'

  events:
    'click .show': 'show'

  constructor: ->
    super

    Statistic.bind 'change', (foo) =>
      @render()

  render: ->
    @item = Statistic.last()
    @html require('views/stat')(@item)

  change: (params) =>
    @render()

  show: ->
    @navigate '/searches', @item.id


class Synonyms extends Spine.Controller
  className: 'syn'

  elements:
    'form': 'form'

  events:
    'click .show': 'show'
    'click #add_btn' : 'add'

  constructor: ->
    super
    @active @change

  render: ->
    @html require('views/synonyms')(@item)

  change: (params) =>
    @item = Domain.find(params.id)
    @render()

  show: ->
    @navigate '/searches', @item.id

  add: (e) ->
    e.preventDefault()
    @synonym = Synonym.create(domain: @item)
    @synonym.fromForm(@form)
    @synonym.save()
    @item.save()
    @render()

class Spellings extends Spine.Controller
  className: 'spell'

  elements:
    'form': 'form'

  events:
    'click .show': 'show'
    'click #add_btn' : 'add'

  constructor: ->
    super
    @active @change

  render: ->
    @html require('views/spellings')(@item)

  change: (params) =>
    @item = Domain.find(params.id)
    @render()

  show: ->
    @navigate '/searches', @item.id

  add: (e) ->
    e.preventDefault()
    @spelling = Spelling.create(domain: @item)
    @spelling.fromForm(@form)
    @spelling.save()
    @item.save()
    @render()


class Query extends Spine.Controller
  className: 'query'

  elements:
    '#keyword': 'keyword'
    '#results': 'results'

  events:
    'click .show': 'show'
    'click #search_btn': 'query'

  constructor: ->
    super
    @active @change

  change: (params) =>
    @item = Domain.find(params.id)
    @render()

  render: ->
    @html require('views/query')

  query: ->
    p = {
    "q": @keyword.val(),
    "wt": "json"
    }
    $.ajax({
    type: "GET",
    url: discovery_search_host + "/" + @item.name + "/select",
    data: p
    }).success(@responseSuccess)

  responseSuccess: (data, status, xhr) =>
    @results.html data

  show: ->
    @navigate '/searches', @item.id


class Build extends Spine.Controller
  className: 'build'

  elements:
    '#jsoninput': 'jsoninput'

  events:
    'click .show': 'show'
    'click .buildIndex': 'buildIndex'

  constructor: ->
    super
    @active @change

  change: (params) =>
    @item = Domain.find(params.id)
    @render()

  render: ->
    @html require('views/build')

  buildIndex: ->
    @log @jsoninput.val()

    $.ajax({
    type: "POST",
    url: discovery_search_host + "/" + @item.name + "/update/json?commit=true",
    data: @jsoninput.val(),
    dataType: 'json',
    contentType: 'application/json'
    })

  show: ->
    @navigate '/searches', @item.id


class Edit extends Spine.Controller
  className: 'edit'

  elements:
    'form': 'form'
    '#fields': 'fieldsTable'

  events:
    'submit form': 'save'
    'click .save': 'save'
    'click .delete': 'delete'
    'click #addField': 'addField'

  constructor: ->
    super
    @active @change

    @html require('views/form')

  render: =>
    @html require('views/form')(@item)

  change: (params) =>
    @item = Domain.find(params.id)
    @render()

  save: (e) ->
    e.preventDefault()
    fieldsjson = @tableToJSON(@fieldsTable[0])

    for e,i in @item.fields().all()
      e.load(fieldsjson[i])
      e.save()

    @item.fromForm(@form).save()

    @navigate '/searches', @item.id

  delete: ->
    @log('deleting')
    @item.destroy() if confirm("Are you sure?")

  addField: =>
    field = Field.create(domain: @item)
    searchesField = new SearchesFields(item: field)
    @fieldsTable.append(searchesField.render().children(":first"))

  clicked: (item) =>
    @log('clicked')
    @log(item.id)

  tableToJSON: (table) ->
    data = [];
    headers = [];

    for name in table.rows[0].cells
      headers.push($(name).attr("name"))

    for row,i in table.rows when i > 0
      rowData = {}
      for cell,j in row.cells
        cell = $(cell)
        type = cell.children().attr("type")
        if type
          type = type.toString()

        if type is "checkbox"
          v = $("input:checkbox:checked", cell).val()
          if v
            rowData[ headers[j] ] = true
          else
            rowData[ headers[j] ] = false

        else
          rowData[ headers[j] ] = $(cell).children().val()
          ;

      data.push(rowData)
    data

class Main extends Spine.Stack
  className: 'main stack'
  controllers:
    show: Show
    edit: Edit
    stat: Stat
    build: Build
    query: Query
    synonyms: Synonyms
    spellings: Spellings

module.exports = Main