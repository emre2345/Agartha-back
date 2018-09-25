# Create circle and associate it with practitioner

Create a circle and associate it with a practitioner.

**URL** : `/v2/circles/:practitioner_id`

**Method** : `POST`

**Auth required** : No

**Permissions required** : None

**Data constraints**

practitioner_id must be a practitioner with a sufficient number of
available spirit points to create the circle.

**Data example** All fields must be sent.

```json
{
  "name":"MyCircle Name",
  "description":"MyCircle Desc",
  "startTime":"2020-03-15T12:00:00.000Z",
  "endTime":"2020-03-15T14:00:00.000Z",
  "disciplines":[],
  "intentions":[],
  "minimumSpiritContribution":14,
  "language": "Swedish",
  "virtualRegistered": 0
}
```

## Success Response

**Condition** : If everything is OK and the Circle is created.

**Code** : `200 OK`

**Content example**

```json
{
    "_id": "5baa83ebcb18095134ad24a8"
}
```

## Error Responses

### Insufficient spirit points

**Condition** : If practitioner_id does not have sufficient spirit points to create circle.

**Code** : `400 Bad Request`

**Content example** : `{"error":"Practitioner cannot create circle with less than 50 contribution points", "errorCode": 10001 }`

### Invalid practitioner id

**Condition** : If practitioner_id does not match an existing practitioner

**Code** : `400 Bad Request`

**Content example** : `{"error":"Practitioner Id missing or incorrect" }`

**Comment** : An error code will be added to the content.

## Insufficient circle data

**Condition** : If the provided circle data is insufficient to creat a circle

**Code** : `400 Bad Request`

**Content example** : `{"error":"Insufficient Circle data", "errorCode": 10003 }`

