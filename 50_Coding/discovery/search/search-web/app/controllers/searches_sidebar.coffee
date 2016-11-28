Spine = require('spine')
Domain = require('models/domain')
Itemlist = require('controllers/itemlist')
$ = Spine.$

class Sidebar extends Spine.Controller
  className: 'sidebar'

  elements:
    '.items': 'items'
    'input[type=search]': 'search'

  events:
    'keyup input[type=search]': 'filter'
    'click footer button': 'create'

  constructor: ->
    super

    @html require('views/sidebar')()

    @list = new Itemlist
      el: @items,
      template: require('views/item'),
      selectFirst: true

    @list.bind 'change', @change

    @active (params) ->
      if Domain.exists(params.id)
        @list.change(Domain.find(params.id))

    Domain.bind('refresh change', @render)

    @log('initialized sidebar')

  render: =>
    domains = Domain.filter(@query)
    @list.render(domains)

  change: (item) =>
    if item
      @navigate '/searches', item.id

  filter: ->
    @query = @search.val()
    @render()

  create: ->
    item = Domain.create()
    @log(item.id)
    @navigate('/searches', item.id, 'edit')

module.exports = Sidebar