# Backend
### Die Dokumentation befindet sich im Aufbau. Bis dahin schaut hier vorbei: https://docs.google.com/document/d/1UPu3-m0V8NW_PgF-WlSM_9aRWLjnxN3dYn2T1G6Oucw/edit?usp=sharing


# Endpoints: REST-API

  
[Übermitteln des Bestands an einem Supermarkt](#übermitteln-neuer-bestandsinformationen-an-einen-supermarkt---deployed)
[Supermärkte nach Standort (und Produkt) abfragen](#supermärkte-nach-standort-und-produkt-abfragen)

[Abfrage aller Produktkategorien](#abfrage-aller-produktkategorien)

[Market Details abfragen](#market-details-abfragen)

[Supermarkt Information Abfrage - Google Doc](https://docs.google.com/document/d/1UPu3-m0V8NW_PgF-WlSM_9aRWLjnxN3dYn2T1G6Oucw/edit#heading=h.lc4whgo30cgm)

[Bestandsabfrage(google id wird noch hinzugefügt)  - Google Doc](https://docs.google.com/document/d/1UPu3-m0V8NW_PgF-WlSM_9aRWLjnxN3dYn2T1G6Oucw/edit#heading=h.bgsyhocc8v4x)

[Supermarkt anlegen, ändern, löschen  - Google Doc](https://docs.google.com/document/d/1UPu3-m0V8NW_PgF-WlSM_9aRWLjnxN3dYn2T1G6Oucw/edit#heading=h.txfaibqq7yn2)

[Produktkategorie anlegen, ändern, löschen  - Google Doc](https://docs.google.com/document/d/1UPu3-m0V8NW_PgF-WlSM_9aRWLjnxN3dYn2T1G6Oucw/edit#heading=h.i8no2bq3i5q6)

[EAN anlegen  - Google Doc](https://docs.google.com/document/d/1UPu3-m0V8NW_PgF-WlSM_9aRWLjnxN3dYn2T1G6Oucw/edit#heading=h.fiw7wvq81pfa)

[EAN abfrage  - Google Doc](https://docs.google.com/document/d/1UPu3-m0V8NW_PgF-WlSM_9aRWLjnxN3dYn2T1G6Oucw/edit#heading=h.zccauwllf4zp)







## Übermitteln neuer Bestandsinformationen an einen Supermarkt

`POST /market/transmit`
**[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/transmit](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/transmit)**

**Info:** Das Überreichen einer Liste von Produkten ist zur Zeit noch nicht implementiert.

**Anfrage:**  2 Möglichkeiten
1. JSON mit `market_id`, `product_id`, `quantity` (0 (wenig) - 100 (viel))
2. JSON mit  JSON `bulk` mit attr `market_id`, `product_id`, `quantity`

**Beispiel:**
Json Input 1
```json
{ 
	"market_id": 1, 
	"product_id": 1, 	
	"quantity": 100
} 
```
Json Input 2
```json
{
	“bulk”: [{
		"market_id": 1,
		"product_id": 1,
		"quantity": 100
		}, 
		{...Another Product...}
	]
}
```

Json Output
```json 
{

	"result": "success"

}
```

  

## Supermärkte nach Standort (und Produkt) abfragen

`POST /market/scrape`
**[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/scrape](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/scrape)**

**Anfrage:** JSON mit attr `zip` und/oder `gps_length, gps_width`, `radius` (in Meter, optional), JSON Liste von `product_id` (optional), `details_requested` (*deprecated* durch `/market/details`)

- Falls JSON Liste `product_id` fehlt, wird jeweils der gesamte bekannte Supermarktbestand zurückgeliefert. Ansonsten der gefilterte Bestand.
- Anfrage mit `zip` liefert alle Märkte mit entsprechender PLZ.
- Anfrage mit `gps_length` (longitude) und `gps_width` (latitude) liefert Märkte um die gegebenen Koordinaten mit Radius `radius`.
- Sind `GPS`-Koordinaten übergeben, wird `zip` ignoriert.
- Default `radius` ist 1000m 

**Antwort:** JSON Liste, in der jedes Element einen Supermarkt mit seinem angefragten Sortiment darstellt.

JSON Liste `supermarkt` mit Elementen bestehend aus `market_id`, `name`, `city`, `zip`, `street`, `gps_length`, `gps_width`, `distance`, `google_id`, `open_now` JSON Liste von JSON Liste von `product` mit Elementen bestehend aus `product_id`, `product_name`, `quantity` (optional wenn auch in der Anfrage)

**Beispiel:**
Json Input
```json
{
 "zip": Number (Beispiel: 12345),
 "gps_length": String (Beispiel: 8.878),
 "gps_width": String (Beispel: 45.34),
 "radius": Number (Beispiel: 100),
 "product_id": 
	 {
	 1, 2
	 }
}
```

Json Output
 ```json
[ {
	"id": Number (Beispiel: 2),
	"mapsId": String (Beispiel: "rx59ghdk"),
	"name": String (Beispiel: "Rewe"),
	"city": String (Beispiel: "Berlin"),
	"zip": Number (Beispiel: 12345),
	"street": String (Beispiel: "Frommhagenstraße 10"),
	"lat": String (Beispiel: "52.5221422"),
	"lng": String (Beispiel: "13.4034652"),
	"distance": Number (Beispiel: 500),
	"open": Boolean (Beispiel: true),
	"products": [
		{
		"id": Number (Beispiel: 1),
		"name": String (Beispiel: "Milch"),
		"availability": Number (Beispiel: 43)
		},
		{
		"id": Number (Beispiel: 2),
		"name": String (Beispiel: "Eis"),
		"availability": Number (Beispiel: 74)
		}
	},
	{...weiterer Supermarkt und Bestandsinformationen...}
]
```
  

## Abfrage aller Produktkategorien
`POST /product/scrape`
[https://wvvcrowdmarket.herokuapp.com/ws/rest/product/scrape](https://wvvcrowdmarket.herokuapp.com/ws/rest/product/scrape)
Liefert eine Liste aller verfügbaren Produktkategorien zurück.

**Anfrage:** 

**Antwort:** JSON Liste mit Elementen bestehend aus `product_id`, `name`

 
**Beispiel:**
Json Input
```json
{}
```

Json Output
 ```json
{
	"result": "success",
	"product": [ {
		"product_id": 1,
		"product_name": "Milch",
		}
		]
}
```

 ## Market Details abfragen
`POST /market/details`
[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/details](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/details)

**Anfrage:** JSON mit  `id` (WhatsLeft-MarketID) oder `mapsId` (Google Maps POI-ID)

**Antwort:** JSON mit `result`, Liste `supermarket`mit Marktinformationen, sowie dem erfassten Bestand

 ---
**Beispiel:**
Json Input
```json
{"id": 47}
Oder
{"mapsId": "ChIJiT47naRPqEcRkuiNMlhUlAY"}

{
```
Json Output
 ```json
{
"result": "success",
"supermarket": {
	"id": 47,
	"name": "REWE",
	"city": "Berlin",
	"street": "Karl-Marx-Straße 92-98",
	"lng": "13.4358774",
	"lat": "52.4798766",
	"distance": "",
	"mapsId": "ChIJiT47naRPqEcRkuiNMlhUlAY",
	"open": false,
	"products": [
		{
			"id": 26,
			"name": "Fisch",
			"availability": 100
		},
		{
			"id": 162,
			"name": "Nudeln",
			"availability": 65
		}
		]
	}
}
```  

