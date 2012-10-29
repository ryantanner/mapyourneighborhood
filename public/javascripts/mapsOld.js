$(document).ready(function ($) {

  // get coords, call initializeMap in callback
  $.getJSON("/city/" + App.cityName + "/coordinates", function(data) {

    App.cityCenter = data.coordinates;

    initializeMap();

  }); 

});

function initializeMap() {

  var map = L.map('map').setView(App.cityCenter, 13);

  // efcec85fc3ea4929a28650f4abceb6f9
  
  L.tileLayer('http://{s}.tile.cloudmade.com/efcec85fc3ea4929a28650f4abceb6f9/997/256/{z}/{x}/{y}.png', {
    attribution: 'Map data &copy; OpenStreetMap',
    maxZoom: 18
  }).addTo(map);

  var drawControl = new L.Control.Draw({
    position: 'topright',
    polyline: false
  });
  map.addControl(drawControl);

  var drawnItems = new L.LayerGroup();
  map.on('draw:poly-created', function (e) {
    drawnItems.addLayer(e.poly);
  });
  map.on('draw:rectangle-created', function (e) {
    drawnItems.addLayer(e.rect);
  });
  map.on('draw:circle-created', function (e) {
    drawnItems.addLayer(e.circ);
  });
  map.on('draw:marker-created', function (e) {
    e.marker.bindPopup('A popup!');
    drawnItems.addLayer(e.marker);
  });
  map.addLayer(drawnItems);

}
