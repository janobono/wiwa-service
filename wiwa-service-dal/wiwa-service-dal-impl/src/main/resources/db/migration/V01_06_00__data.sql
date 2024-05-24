INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('TITLE', 'WIWA - Internet store');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('WELCOME_TEXT', e'If you plan to make the furniture yourself, we offer you the opportunity to order from us quality prepared furniture
parts for your kitchen, office or other interior project.

We will provide you with the required materials:

- DTD,
- MDF,
- HDF,
- cutting and gluing the edges exactly according to your wishes.');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('APP_INFO', '["### Cutting and edging","### Online ordering.","### Manufacture and delivery"]');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('COMPANY_INFO', e'{
"name":"WIWA, Ltd.",
"street":"Street 12/4567",
"city":"Zvolen",
"zipCode":"960 01",
"state":"Slovakia",
"phone":"+421 111 111 111",
"mail":"mail@domain.sk",
"businessId":"11 111 111",
"taxId":"1111111111",
"vatRegNo":"SK1111111111",
"commercialRegisterInfo":"The company is registered in the Commercial Register of the District Court in Zvolen, section Sro, insert number 11111/P",
"mapUrl":"https://maps.google.com/maps?q=Zvolen&t=&z=13&ie=UTF8&iwloc=&output=embed"
}');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('UNITS', e'[
{"id":"MILLIMETER","value":"mm"},
{"id":"METER","value":"m"},
{"id":"SQUARE_METER","value":"㎡"},
{"id":"KILOGRAM","value":"kg"},
{"id":"PIECE","value":"p"}
]');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ( 'VAT_RATE', '20');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('BUSINESS_CONDITIONS', e'# H1
## H2
### H3

**bold text**

*italicized text*

> blockquote

1. First item
2. Second item
3. Third item

- First item
- Second item
- Third item

`code`

---
![alt text](image.jpg)

[more...](https://www.markdownguide.org/cheat-sheet/)
');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('COOKIES_INFO', e'# H1
## H2
### H3

**bold text**

*italicized text*

> blockquote

1. First item
2. Second item
3. Third item

- First item
- Second item
- Third item

`code`

---
![alt text](image.jpg)

[more...](https://www.markdownguide.org/cheat-sheet/)
');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('GDPR_INFO', e'# H1
## H2
### H3

**bold text**

*italicized text*

> blockquote

1. First item
2. Second item
3. Third item

- First item
- Second item
- Third item

`code`

---
![alt text](image.jpg)

[more...](https://www.markdownguide.org/cheat-sheet/)
');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('ORDER_INFO', e'# H1
## H2
### H3

**bold text**

*italicized text*

> blockquote

1. First item
2. Second item
3. Third item

- First item
- Second item
- Third item

`code`

---
![alt text](image.jpg)

[more...](https://www.markdownguide.org/cheat-sheet/)
');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('WORKING_HOURS', e'### Opening hours

| Day       | from | to    |
|-----------|------|-------|
| Monday    | 7:00 | 17:00 |
| Tuesday   | 7:00 | 17:00 |
| Wednesday | 7:00 | 17:00 |
| Thursday  | 7:00 | 17:00 |
| Friday    | 7:00 | 15:00 |

_Lunch break from 12:00 to 13:00_
');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('SIGN_UP_MAIL', e'{
"subject":"Account activation",
"title":"Account activation",
"message":"Your account has been created. Please do not reply to this message.",
"link":"Click to activate your account."
}');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('RESET_PASSWORD_MAIL', e'{
"subject":"Password activation",
"title":"Password activation",
"message":"We have generated a new password for you. Please do not reply to this message.",
"passwordMessage":"New password: %s",
"link":"Click to activate the password."
}');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('MANUFACTURE_PROPERTIES', e'{
"minimalSystemDimensions":{"x":50,"y":50},
"minimalEdgedBoardDimensions":{"x":250,"y":60},
"minimalLayeredBoardDimension":{"x":250,"y":80},
"minimalFrameBoardDimensions":{"x":250,"y":80},
"edgeWidthAppend":8,
"edgeLengthAppend":40,
"duplicatedBoardAppend":10
}');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('PRICE_FOR_GLUING_LAYER', e'{
"price":10
}');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('PRICES_FOR_GLUING_EDGE', e'[
{
"width":23.00,
"price":0.700
},
{
"width":33.00,
"price":0.850
},
{
"width":45.00,
"price":0.980
},
{
"width":65.00,
"price":1.680
}
]');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('PRICES_FOR_CUTTING', e'[
{
"thickness":19.00,
"price":0.630
},
{
"thickness":45.00,
"price":0.720
},
{
"thickness":65.00,
"price":2.000
}
]');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('FREE_DAYS', e'[
{
"name":"Day of the Establishment of the Slovak Republic",
"day":1,
"month":1
},
{
"name":"Epiphany",
"day":6,
"month":1
},
{
"name":"Good Friday",
"day":29,
"month":3
},
{
"name":"Easter Monday",
"day":1,
"month":4
},
{
"name":"International Workers Day",
"day":1,
"month":5
},
{
"name":"Day of victory over fascism",
"day":8,
"month":5
},
{
"name":"St. Cyril and Methodius Day",
"day":5,
"month":6
},
{
"name":"Slovak National Uprising Anniversary",
"day":29,
"month":8
},
{
"name":"Day of the Constitution of the Slovak Republic",
"day":1,
"month":9
},
{
"name":"Day of Our Lady of the Seven Sorrows, patron saint of Slovakia",
"day":15,
"month":9
},
{
"name":"All Saints Day",
"day":1,
"month":11
},
{
"name":"Struggle for Freedom and Democracy Day",
"day":17,
"month":11
},
{
"name":"Christmas Eve",
"day":24,
"month":12
},
{
"name":"Christmas Day",
"day":25,
"month":12
},
{
"name":"St. Stephens Day",
"day":26,
"month":12
}
]');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('ORDER_COMMENT_MAIL', e'{
"subject":"Order comment - order No.%03d",
"title":"Order comment - order No.%03d",
"message":"New comment was added. Please do not reply to this message.",
"link":"Click to see order details."
}');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('ORDER_SEND_MAIL', e'{
"subject":"Order send - order No.%03d",
"title":"Order send - order No.%03d",
"message":"Order was send. Please do not reply to this message.",
"link":"Click to see order details.",
"attachment":"detail%03d.pdf"
}');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('ORDER_STATUS_MAIL', e'{
"productionSubject":"Order in production - order No.%03d",
"productionTitle":"Order in production - order No.%03d",
"productionMessage":"Your order is in production. Please do not reply to this message.",
"readySubject":"Order is ready for pickup - order No.%03d",
"readyTitle":"Order is ready for pickup - order No.%03d",
"readyMessage":"Your order is ready for pickup. Please do not reply to this message.",
"finishedSubject":"Thank you - order No.%03d",
"finishedTitle":"Thank you - order No.%03d",
"finishedMessage":"Thank you for your order. Please do not reply to this message.",
"cancelledSubject":"Order cancelled - order No.%03d",
"cancelledTitle":"Order cancelled - order No.%03d",
"cancelledMessage":"We are sorry, but we had to cancel your order. Please do not reply to this message.",
"link":"Click to see order details.",
"attachment":"detail%03d.pdf"
}');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ( 'BOARD_MATERIAL_CATEGORY', '-1');

INSERT INTO wiwa_application_property (property_key, property_value) VALUES ('ORDER_PROPERTIES', e'{
"dimensions":{
"X":"A",
"Y":"B"
},
"boards":{
"TOP":"TOP",
"BOTTOM":"BOTTOM",
"A1":"A1",
"A2":"A2",
"B1":"B1",
"B2":"B2"
},
"edges":{
"A1":"A1",
"A1I":"A1I",
"A2":"A2",
"A2I":"A2I",
"B1":"B1",
"B1I":"B1I",
"B2":"B2",
"B2I":"B2I",
"A1B1":"A1B1",
"A1B2":"A1B2",
"A2B1":"A2B1",
"A2B2":"A2B2"
},
"corners":{
"A1B1":"A1B1",
"A1B2":"A1B2",
"A2B1":"A2B1",
"A2B2":"A2B2"
},
"format":{
"CSV_NUMBER":"%d %s",
"CSV_BASIC":"%s (basic %s - %d x %d mm - %d p)",
"CSV_FRAME":"%s (frame %s - %d x %d mm - %d p)",
"CSV_DUPLICATED_BASIC":"%s (duplicated basic %s - %d x %d mm - %d p)",
"CSV_DUPLICATED_FRAME":"%s (duplicated frame %s - %d x %d mm - %d p)",
"CSV_EDGE":"%s %dx%f",
"CSV_CORNER_STRAIGHT":"%s %dx%d",
"CSV_CORNER_ROUNDED":"%s r%d",
"PDF_TITLE":"Order No.%s",
"PDF_ORDER_NUMBER":"%03d",
"PDF_INTEGER":"%d %s",
"PDF_UNIT":"%.3f %s",
"PDF_PRICE":"%.2f %s",
"PDF_EDGE":"%s %dx%.1f",
"PDF_CORNER_STRAIGHT":"%d %s x %d %s",
"PDF_CORNER_ROUNDED":"r %d %s"
},
"content":{
"MATERIAL_NOT_FOUND":"Material not found",
"BOARD_NOT_FOUND":"Board not found",
"EDGE_NOT_FOUND":"Edge not found"
},
"packageType":{
"NO_PACKAGE":"NO_PACKAGE",
"NO_PACKAGE_WITH_REMAINS":"NO_PACKAGE_WITH_REMAINS",
"PACKAGE":"PACKAGE",
"PACKAGE_WITH_REMAINS":"PACKAGE_WITH_REMAINS"
},
"csvSeparator":";",
"csvReplacements":{
"<.*?>":"",
"\\s+":"_"
},
"csvColumns":{
"NUMBER":"NUMBER",
"NAME":"NAME",
"MATERIAL":"MATERIAL",
"DECOR":"DECOR",
"X_DIMENSION":"DIMENSION A",
"Y_DIMENSION":"DIMENSION B",
"QUANTITY":"QUANTITY",
"ORIENTATION":"FIBER DIRECTION",
"THICKNESS":"THICKNESS",
"EDGE_A1":"EDGE A1",
"EDGE_A2":"EDGE A2",
"EDGE_B1":"EDGE B1",
"EDGE_B2":"EDGE B2",
"CORNER_A1B1":"CORNER A1B1",
"CORNER_A1B2":"CORNER A1B2",
"CORNER_A2B1":"CORNER A2B1",
"CORNER_A2B2":"CORNER A2B2",
"DESCRIPTION":"DESCRIPTION"
}
}');
