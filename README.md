# Backend - Neue Version mit neuem Backend
### Bei Fragen/Unklarheiten einfach melden. Kleine Fehler können sich eingeschlichen haben, wir bitten euch darum uns diese mitzuteilen. :)


# Endpoints: REST-API

### Supermarkt

[Bestandsabfrage eines Marktes](#bestandsabfrage-von-markt)

[Übermitteln neuer Bestandsinformationen an einem Supermarkt](#übermitteln-neuer-bestandsinformationen-an-einen-supermarkt)

[Supermärkte nach Standort (und Produkt) abfragen](#supermärkte-nach-standort-und-produkt-abfragen)

[Markt Details abfragen](#markt-details-abfragen)

[Supermarkt anlegen, ändern, löschen](#supermarkt-anlegen-ändern-löschen)

### Produkte

[Abfrage aller Produktkategorien](#abfrage-aller-produktkategorien)

[Produktkategorie anlegen, ändern, löschen](#produktkategorie-anlegen-ändern-löschen)

### EAN

[EAN abfragen](#ean-abfragen)

[EAN anlegen](#ean-anlegen)

---

# Supermarkt


## Bestandsabfrage von Markt
`POST /market/stock`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/stock](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/stock)

**Anfrage:** `market_id` (prio 1, optional wenn google_id), `maps_id` (prio 2, optional wenn market_id ), JSON Liste von `product_id` (optional)

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
"maps_id": lkfdsJKJD83KJDkdk,
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
      "quantity": 50,
      "emoji": xyz
   }
   ]
}
```

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
      "availability": 100
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

**Anfrage:** JSON mit attr `zip` und/oder `longitude, latitude`, `radius` (in Meter, optional), JSON Liste von `product_id` (optional), `details_requested` (*deprecated* durch `/market/details`)

- Falls JSON Liste `product_id` fehlt, wird jeweils der gesamte bekannte Supermarktbestand zurückgeliefert. Ansonsten der gefilterte Bestand.
- Anfrage mit `zip` liefert alle Märkte mit entsprechender PLZ.
- Anfrage mit `longitude` und `latitude` liefert Märkte um die gegebenen Koordinaten mit Radius `radius`.
- Sind `GPS`-Koordinaten übergeben, wird `zip` ignoriert.
- Default `radius` ist 1000m 
- `details_requested`
	- `false`: Es werden keine Bestandsinformationen zurückgeliefert -> für bessere Performance
	- `true` (default): Es werden zusätzlich zu den Marktinformationen auch der vorhandene Bestand übergeben

**Antwort:** JSON Liste, in der jedes Element einen Supermarkt mit seinem angefragten Sortiment darstellt.

JSON Liste `supermarkt` mit Elementen bestehend aus `market_id`,`maps_id`, `market_name`, `city`, `zip`, `street`, `longitude`, `latitude`, `distance`,  `icon_url`, `distance`(in Meter), `periods`, JSON Liste von JSON Liste von `product` mit Elementen bestehend aus `product_id`, `product_name`, `quantity` (optional je nach `details_requested`)

- `periods`: JSON mit den folgenden Einträgen. Sind keine Öffnungszeiten bekannt, wird eine leere Liste zurück geliefert.
	- `open_day_id` u. `close_day_id`: (INT) ID {0-6}, mit 0 = Sonntag (Bsp: 3 = Mittwoch)
	- `open_time` u. `close_time`: (TEXT) Zeit im Format "hh:mm"
	- `open_day_short` u. `close_day_short`: (TEXT) Tag in Kurzform. (Mo,Di,Mi,Do,Fr,Sa,So)
	- `open_day` u. `close_day`: (TEXT) Ausgeschriebener Tag. (Bsp: "Sonntag")
	

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
	"market_id": Number (Beispiel: 2),
	"maps_id": String (Beispiel: "rx59ghdk"),
	"market_name": String (Beispiel: "Rewe"),
	"city": String (Beispiel: "Berlin"),
	"zip": Number (Beispiel: 12345),
	"street": String (Beispiel: "Frommhagenstraße 10"),
	"lat": String (Beispiel: "52.5221422"),
	"lng": String (Beispiel: "13.4034652"),
	"distance": Number (Beispiel: 500),
	“icon_url”: String (Beispiel: http://www.sampleurl.de),
	“periods”:[
            {
		“open_day_id”: 1,
                “open_time”: “07:00”,
                “open_day”: “Montag”,
                “open_day_short”: “Mo”,
		“close_day_id”: 1,
                “close_time”: "22:00",
                “close_day”: “Montag”,
                “close_day_short”: “Mo”
		},
		{...weitere Öffnungsperiode...}
		],
	"products": [
		{
		"id": Number (Beispiel: 1),
		"name": String (Beispiel: "Milch"),
		"availability": Number (Beispiel: 43),
		"emoji": xyz
	     },
	     {
		"id": Number (Beispiel: 2),
		"name": String (Beispiel: "Eis"),
		"availability": Number (Beispiel: 74),
		"emoji": xyz
	     }]
	

	},
	{...weiterer Supermarkt und Bestandsinformationen...}
]
```

## Markt Details abfragen
`POST /market/details`
[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/details](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/details)

**Anfrage:** JSON mit  `market_id` oder `maps_id` (Google Maps POI-ID)

**Antwort:** JSON mit `result`, Liste `supermarket`mit Marktinformationen, sowie dem erfassten Bestand

- `periods`: JSON mit den folgenden Einträgen. Sind keine Öffnungszeiten bekannt, wird eine leere Liste zurück geliefert.
	- `open_day_id` u. `close_day_id`: (INT) ID {0-6}, mit 0=Sonntag (Bsp: 3 = Mittwoch)
	- `open_time` u. `close_time`: (TEXT) Zeit im Format "hh:mm"
	- `open_day_short` u. `close_day_short`: (TEXT) Tag in Kurzform. (Mo,Di,Mi,Do,Fr,Sa,So)
	- `open_day` u. `close_day`: (TEXT) Ausgeschriebener Tag. (Bsp: "Sonntag")

**Beispiel:**
Json Input
```yaml
{"market_id": 47}
Oder
{"maps_id": "ChIJiT47naRPqEcRkuiNMlhUlAY"}

{
```
Json Output
 ```yaml
{
"result": "success",
"supermarket": {
	"market_id": 47,
	"market_name": "REWE",
	"city": "Berlin",
	"street": "Karl-Marx-Straße 92-98",
	"longitude": "13.4358774",
	"latitude": "52.4798766",
	"maps_id": "ChIJi47naRPqcRkuiNMlhUlAY",
	“icon_url”: String (Beispiel: http://www.sampleurl.de),
	“periods”:[
            {
		“open_day_id”: 1,
                “open_time”: “07:00”,
                “open_day”: “Montag”,
                “open_day_short”: “Mo”,
		“close_day_id”: 1,
                “close_time”: "22:00",
                “close_day”: “Montag”,
                “close_day_short”: “Mo”
		},
		{...weitere Öffnungsperiode...}
		],
	"products": [
		{
			"product_id": 26,
			"product_name": "Fisch",
			"availability": 100,
			"emoji": xyz
	      },
	      {
			"product_id": 162,
			"product_name": "Nudeln",
			"availability": 65,
			"emoji": xyz
	      }
	      ]
	}
}
``` 

## Supermarkt anlegen, ändern, löschen
`POST /market/manage`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/market/manage](https://wvvcrowdmarket.herokuapp.com/ws/rest/market/manage)

**Anfrage:** JSON operation(“create”, “modify”, “delete”), `market_id`, `market_name`, `city`, `zip`, `street`, `longitude`, `latitude`

**Antwort:** `result` (“success” or “error”)

 
**Beispiel:**
Json Input Anlegen
```yaml
{ 
   "operation":"create", 
   "market_name":"REWE", 
   "city":"Bad Nauheim",
   "zip":"61231",
   "street":"Georg-Scheller-Strasse 2-8",
   "longitude":"8.754167",
   "latitude":"50.361944"
}
```
Json Input Ändern
```yaml
{ 
   "operation":"modify", 
   "market_id":7, 
   "market_name":"REWE",
   "city":"Bad Nauheim",
   "zip":"61231", 
   "street":"Georg-Scheller-Strasse 2-10",
   "longitude":"8.754167", 
   "latitude":"50.361944" 
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

---

# Produkte

## Abfrage aller Produktkategorien
`POST /product/scrape`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/product/scrape](https://wvvcrowdmarket.herokuapp.com/ws/rest/product/scrape)

Liefert eine Liste aller verfügbaren Produktkategorien zurück.

**Anfrage:** 

**Antwort:** JSON Liste mit Elementen bestehend aus `product_id`, `prodect_name`, `emoji`

 
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
	   {"product_id": 1, "product_name": "Milch", "emoji": 🥛},
	   {"product_id": 3, "product_name": "Kartoffeln", "emoji": 🥔}
	   ]
}
```

## Produktkategorie anlegen, ändern, löschen
`POST /product/manage`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/product/manage](https://wvvcrowdmarket.herokuapp.com/ws/rest/product/mamage)

**Anfrage:** `operation`, `product_id`, `product_name`

- `operation` : "create", "modify" or "delete"
	- create:  `product_name` ist name des neuen Produktes, `product_id` wird ignoriert
	- modify: `product_id` zu modifizierendes Produkt, `product_name` Neuer name des Produktes
	- delete: `product_id` zu löschendes Produkt, `product_name` wird ignoriert

**Antwort:** `result` 

- `result`:  “success” or “error”

 
**Beispiel:**
Json Input
```yaml
{
   "operation":"create",
   "product_id": 1,
   "product_name":"Milch"
}
```

Json Output
 ```json
{
   "result": "success"
}
```

---

# EAN


## EAN abfragen
`POST /product_ean/scrape`

[https://wvvcrowdmarket.herokuapp.com/ws/rest/product_ean/scrape](https://wvvcrowdmarket.herokuapp.com/ws/rest/product_ean/scrape)

**Anfrage:** JSON mit `ean`

- `ean`: 8- oder 13-stellige [EAN](https://de.wikipedia.org/wiki/European_Article_Number)

**Antwort:** JSON `result`, `product_id`, `product_name`

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
   "product_name": "Milch"
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






