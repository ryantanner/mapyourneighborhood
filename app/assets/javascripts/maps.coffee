###

jQuery ($) ->

  $map = $('#map')
  cityName = $map.data("cityName")
  cityId = $map.data("cityId")
  cityInfoUrl = $map.data('url')
  neighborhoodsUrl = $table.data('list')

  loadNeighborhoods = ->
    $.get neighborhoodsUrl + "/" + cityId, (neighborhoods) ->
      $.each neighborhoods, (index, neighborhoodId) ->


  loadProductTable = ->
    $.get productListUrl, (products) ->
      $.each products, (index,eanCode) ->
        row = $('<tr/>').append $('<td/>').text(eanCode)
        row.attr 'contenteditable', true
        $tbody.append row
        loadProductDetails row

  productDetailsUrl = (eanCode) ->
    $table.data('details').replace '0', eanCode

  loadProductDetails = (tableRow) ->
    eanCode = tableRow.text()

    $.get productDetailsUrl(eanCode), (product) ->
      tableRow.append $('<td/>').text(product.name)
      tableRow.append $('<td/>').text(product.description)

  loadProductTable()

  saveRow = ($row) ->

    [ean, name, description] = $row.children().map -> $(this).text()
    product =
      ean: parseInt(ean)
      name: name
      description: description
    jqxhr = $.ajax
      type: "PUT"
      url: productDetailsUrl(ean)
      contentType: "application/json"
      data: JSON.stringify product
    jqxhr.done (response) ->
      $label = $('<span/>').addClass('label label-success')
      $row.children().last().append $label.text(response)
      $label.delay(3000).fadeOut()
    jqxhr.fail (data) ->
      $label = $('<span/>').addClass('label label-important')
      message = data.responseText || data.statusText
      $row.children().last().append $label.text(message)

  $('[contenteditable]').live 'blur', ->
    saveRow $(this)

###
