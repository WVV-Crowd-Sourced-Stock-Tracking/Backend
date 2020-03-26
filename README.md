# Backend
### Bei Fragen/Unklarheiten einfach melden. Kleine Fehler können sich eingeschlichen haben, wir bitten euch darum uns diese mitzuteilen. :)


# Endpoints: REST-API

  
[Übermitteln des Bestands an einem Supermarkt](#übermitteln-neuer-bestandsinformationen-an-einen-supermarkt---deployed)

[Supermärkte nach Standort (und Produkt) abfragen](#supermärkte-nach-standort-und-produkt-abfragen)

[Abfrage aller Produktkategorien](#abfrage-aller-produktkategorien)

[Market Details abfragen](#market-details-abfragen)

[Bestandsabfrage(google id wird noch hinzugefügt)](#bestandsabfrage-von-markt)

[Supermarkt anlegen, ändern, löschen](#supermarkt-anlegen-ändern-löschen)

[Produktkategorie anlegen, ändern, löschen](#produktkategorie-anlegen-ändern-löschen)

[EAN anlegen](#ean-anlegen)

[EAN abfragen](#ean-abfragen)







## Übermitteln neuer Bestandsinformationen an einen Supermarkt

`POST /market/transmit`
[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/transmit](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/transmit)

**Anfrage:**  2 Möglichkeiten
1. JSON mit `market_id`, `product_id`, `quantity` (0 (wenig) - 100 (viel))
2. JSON mit  JSON `bulk` mit attr `market_id`, `product_id`, `quantity`

**Beispiel:**
Json Input 1
```yaml
{ 
"market_id": 1, 
"product_id": 1, 	
"quantity": 100
} 
```
Json Input 2
```yaml
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
```yaml 
{
"result": "success"
}
```

  

## Supermärkte nach Standort (und Produkt) abfragen

`POST /market/scrape`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/scrape](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/scrape)

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
```yaml
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
 ```yaml
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
```yaml
{}
```

Json Output
 ```yaml
{
	"result": "success",
	"product": [ 
	   {"product_id": 1, "product_name": "Milch"},
	   {"product_id": 3, "product_name": "Kartoffeln"}
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
```yaml
{"id": 47}
Oder
{"mapsId": "ChIJiT47naRPqEcRkuiNMlhUlAY"}

{
```
Json Output
 ```yaml
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

  ## Bestandsabfrage von Markt
`POST /market/stock`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/stock](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/stock)

**Anfrage:** `market_id` (prio 1, optional wenn google_id), `google_id` (prio 2, optional wenn market_id ), JSON Liste von `product_id` (optional)

- Falls keine `product_id`-Liste übermittelt wird besteht die Rückgabe aus dem gesamten im Store bekannten Sortimentsbestand.

**Antwort:** JSON Liste `product_id`, `product_name`, `quantity`

 
**Beispiel:**
Json Input 1
```yaml
{
"market_id": 1,
"product_id":  [1, 2]
}
```
Json Input 2
```yaml
{
"google_id": “GOOGLE_DATA”,
"product_id":  [1, 2]
}

```

Json Output
 ```yaml
{
   "result": "success",
   "product": [ {
      "product_id": 1,
      "product_name": "test",
      "quantity": 50
   }
   ]
}
```


 ## Supermarkt anlegen, ändern, löschen
`POST /market/manage`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/manage](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/manage)

**Anfrage:** JSON operation(“create”, “modify”, “delete”), market_id, name, city, zip, street, gps_length, gps_width

**Antwort:** `result` (“success” or “error”)

 
**Beispiel:**
Json Input Anlegen
```yaml
{ 
   "operation":"create", 
   "name":"REWE", 
   "city":"Bad Nauheim",
   "zip":"61231",
   "street":"Georg-Scheller-Strasse 2-8",
   "gps_length":"8.754167",
   "gps_width":"50.361944"
}
```
Json Input Ändern
```yaml
{ 
   "operation":"modify", 
   "market_id":7, 
   "name":"ROWO",
   "city":"Bad Nauheim",
   "zip":"61231", 
   "street":"Georg-Scheller-Strasse 2-10",
   "gps_length":"8.754167", 
   "gps_width":"50.361944" 
}
```
Json Input Löschen
```yaml
{
   "operation":"delete", 
   "market_id":9
}
```
Json Output
 ```yaml
{
   "result": "success"
}
```

## Produktkategorie anlegen, ändern, löschen
`POST /product/manage`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/product/manage](https://wvvcrowdmarket.herokuapp.com/ws/rest/product/mamage)

**Anfrage:** `operation`, `product_id`, `name`

- `operation` : "create", "modify" or "delete"
	- create:  `name` ist name des neuen Produktes, `product_id` wird ignoriert
	- modify: `product_id` zu modifizierendes Produkt, `name` Neuer name des Produktes
	- delete: `product_id` zu löschendes Produkt, `name` wird ignoriert

**Antwort:** `result` 

- `result`:  “success” or “error”

 
**Beispiel:**
Json Input
```yaml
{
   "operation":"create",
   "product_id": 1,
   "name":"Milch"
}
```

Json Output
 ```json
{
   "result": "success"
}
```


  ## EAN anlegen
`POST /product_ean/manage`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/product_ean/manage](https://wvvcrowdmarket.herokuapp.com/ws/rest/product_ean/manage)

**Anfrage:** JSON mit `ean`, `product_id`
- `ean`: 8- oder 13-stellige [EAN](https://de.wikipedia.org/wiki/European_Article_Number)
- `product_id`: interne product_id, der die übergebene EAN zugeordnet werden soll. 
	- *siehe dazu*: [/product/scrape](#abfrage-aller-produktkategorien)


**Antwort:** `result` 

- `result`:  “success” or “error”

 
**Beispiel:**
Json Input
```yaml
{
   "operation":"create",
   "ean": "0401234567890",
   "product_id": 1
}

```

Json Output
 ```yaml
{
   "result": "success"
}
```


  ## EAN abfragen
`POST /product_ean/scrape`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/product_ean/scrape](https://wvvcrowdmarket.herokuapp.com/ws/rest/product_ean/scrape)

**Anfrage:** JSON mit `ean`

- `ean`: 8- oder 13-stellige [EAN](https://de.wikipedia.org/wiki/European_Article_Number)

**Antwort:** JSON `result`, `product_id`, `name`

- `result`:  “success” or “error”
 
**Beispiel:**
Json Input
```yaml
{
   "ean":"0401234567890"
}
```

Json Output
 ```yaml
{
   "result": "success",
   "product_id": 1,
   "name": "Milch"
}
```




