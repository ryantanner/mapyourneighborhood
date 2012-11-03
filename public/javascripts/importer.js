$(function () {

  var CensusItem = Backbone.Model.extend({

    defaults: function() {
      return {
        id: 0,
        uace: 99999,
        name: "DefaultCityName",
        population: 0,
        populationDensity: 0.0,
        latitude: 0.0,
        longitude: 0.0,
        done: false
      };
    },

    initialize: function() {
      if (!this.get("name")) {
        this.set({"name": this.defaults.name});
      }
    },

    toggle: function () {
      this.save({done: !this.get("done")});
    }

  });

  var CensusItemList = Backbone.Collection.extend({

    model: CensusItem,

    localStorage: new Store("censusItems"),

    numProcessing: 0,

    numProcessed: 0,

    // Sort by UACE
    comparator: function(item) {
      return todo.get('uace');
    }

  });

  var CensusItems = new CensusItemList;


  var CensusItemView = Backbone.View.extend({

    tagName: "tr",

    template: _.template($('#tmplCensusItem').html()),

    events: {},

    initialize: function () {
      this.model.bind('change', this.render, this);
    },

    render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      this.$el.toggleClass('done', this.model.get('done'));
      return this;
    },

    toggleDone: function () {
      this.model.toggle();
    }

  });

  var AppView = Backbone.View.extend({

    el: $("#tblProgress"),

    statsTemplate: _.template($('#tmplStats').html()),

    events: {},

    render: function () {
      var done = Todos.numProcessed;
      var remaining = Todos.numProcessing;
