How to setup project.

You need spring boot ide in my case i used IntelliJ IDEA 2022.3.1 (Community Edition) , postman.

clone the project in your local setup.

(Used firebase storage as backend which is remote so you DONT require any extra configuration or data transfer for setup which generally reuired in mysql).

done.

Authentication Api Link
1. https://galactic-star-507568.postman.co/workspace/6e9fb0d3-54f0-4537-82fa-9dc0d3632c19/request/25493828-396cbb59-aea1-406b-8492-357cda09e11e
2. https://galactic-star-507568.postman.co/workspace/6e9fb0d3-54f0-4537-82fa-9dc0d3632c19/request/25493828-d0795899-f55f-497c-96c1-764ebe7956f3

OR MANUALLY AT POSTMAN (MUST CHECK YOUR LOCALHOST : 8080)
1. POST : http://localhost:8080/api/v1/auth/register
payload
{
    "name":"harsh ping",
    "email":"harsh6@gmail.com",
    "password":"Harsh6@2161"
}

2. POST : http://localhost:8080/api/v1/auth/authenticate
payload
{
    "email":"harsh5@gmail.com",
    "password":"Harsh5@2161"
}

Category Api Link
1. https://galactic-star-507568.postman.co/workspace/6e9fb0d3-54f0-4537-82fa-9dc0d3632c19/request/25493828-cbd7ab41-3db3-4413-867a-f276226b7f22
2. https://galactic-star-507568.postman.co/workspace/6e9fb0d3-54f0-4537-82fa-9dc0d3632c19/request/25493828-ed2641fd-4904-4947-b786-1721271980a8

OR MANUALLY AT POSTMAN (MUST CHECK YOUR LOCALHOST : 8080)
1. GET : http://localhost:8080/api/v1/task/categories/save
headers
Authorization : Bearer than pass token

2. POST : http://localhost:8080/api/v1/task/categories
headers
Authorization : Bearer than pass token
payload
{
    "limit":76,
    "page":1
}

Product Api Link
1. https://galactic-star-507568.postman.co/workspace/6e9fb0d3-54f0-4537-82fa-9dc0d3632c19/request/25493828-f2297240-e41f-4bcc-8959-7f70ae35caa2
2. https://galactic-star-507568.postman.co/workspace/6e9fb0d3-54f0-4537-82fa-9dc0d3632c19/request/25493828-e87c6bc5-d043-4bed-8442-83a728fb478f

OR MANUALLY AT POSTMAN (MUST CHECK YOUR LOCALHOST : 8080)
1. GET : http://localhost:8080/api/v1/task/products/save/categoryId=abcat0100000
headers
Authorization : Bearer than pass token

2. POST : http://localhost:8080/api/v1/task/products
headers
Authorization : Bearer than pass token
payload
{
    "categoryId":"abcat0100000",
    "limit":105,
    "page":0
}

Please view the project screen shot folder too their i had uploaded the project api responses.

LinkedIn : https://www.linkedin.com/in/harsh-pandey-2161hp
Skype : https://join.skype.com/invite/w7Fv2iEpdwlS
ShowCase : https://www.showwcase.com/harshpandey9079939
Twitter : https://twitter.com/harshpandey2121
Github : https://github.com/harsh2161
Gmail : harshpandey9079@gmail.com
Resume Link : https://github.com/harsh2161/resume

Thankyou
Harsh Pandey
+91-7891676144
