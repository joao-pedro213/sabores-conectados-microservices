conn = new Mongo();
db = conn.getDB("sabores-conectados");
db.createUser(
  {
    user: process.env.MONGO_USER,
    pwd: process.env.MONGO_PASSWORD,
    roles: [{ role: "readWrite", db: "sabores-conectados" }],
  },
);

db.createCollection("clients");
db.clients.insertMany([
    {
        "_id": UUID("cfe99f3c-0268-4252-83b6-dbac239be7fc"),
        "name": "postman",
        "clientId": "0e05c5dd-7b41-497e-b5e9-b97c6bdd65e1",
        "clientSecret": null,
        "isPublicClient": true,
        "redirectUris": [ "http://localhost:8000/login/callback" ],
         "scopes": [ "account:read", "account:write", "restaurant:write", "item:write", "order:read", "order:write", "reservation:read", "reservation:write" ]
    },
    {
        "_id": UUID("be9d62b6-2f5c-484b-a18a-5cd10ed5d3d5"),
        "name": "account-service",
        "clientId": "c3043c9e-2457-4e8f-9e8d-8715b1f411c4",
        "clientSecret": "$2a$12$61OoS31AsURxIQpen8LPB.g4pajjOh8z5yUM7lywpMT3YKtMY78NO",
        "isPublicClient": false,
        "redirectUris": [],
        "scopes": [ "identity:write" ]
    },
    {
        "_id": UUID("18515733-ec35-4f9e-94fb-76393b272ad9"),
        "name": "order-service",
        "clientId": "1eeece49-9a52-46cc-9ea7-b8501453b431",
        "clientSecret": "$2a$12$HQdktAtOkhSUbgDhfVCmfehaLQ4CtJd5r9j3YSAb57x3di1MnSxMq",
        "isPublicClient": false,
        "redirectUris": [],
        "scopes": [ "restaurant:api:read" ]
    },
    {
        "_id": UUID("2856771c-af8f-4db5-9e80-6addb924d520"),
        "name": "reservation-service",
        "clientId": "f3caa194-ad49-4344-9d2b-f64929cd6ef3",
        "clientSecret": "$2a$12$TdGC7Qy/nMKNPK6YlJ3QaevxW6IP6wjNgUJuoFQHwwCCsTlim0R9q",
        "isPublicClient": false,
        "redirectUris": [],
        "scopes": [ "restaurant:api:read" ]
    },
    {
        "_id": UUID("afe26191-876c-4658-8f9d-9c1a3e5afa37"),
        "name": "aggregator-service",
        "clientId": "355ee293-2306-4a87-8335-06354553bb75",
        "clientSecret": "$2a$12$/Oge5jmUY.SO1zZg5i/.1eR3HnAfHGJ2Dn0.pYX7EY4ED8w8w1f3u",
        "isPublicClient": false,
        "redirectUris": [],
        "scopes": [ "order:api:read", "reservation:api:read" ]
    }
]);

db.createCollection("identities");
db.identities.insertMany([
    {
        "_id": UUID("1b1ea002-5d0f-49e0-89db-c95d27108f67"),
        "username": "test.customer",
        "password": "$2a$12$lFW11Nn0xUhkIWvsmsOUkemxgMIRSrGCHeR9SGme2sxRMbqZ0rDi2",
        "role": "CUSTOMER",
        "authorities": [],
        "createdAt":ISODate("2025-09-27T00:00:00Z"),
        "updatedAt": ISODate("2025-09-27T00:00:00Z")
    },
    {
        "_id": UUID("b169e43e-597e-4012-90d9-a65bea99544e"),
        "username": "test.owner",
        "password": "$2a$12$lFW11Nn0xUhkIWvsmsOUkemxgMIRSrGCHeR9SGme2sxRMbqZ0rDi2",
        "role": "RESTAURANT_OWNER",
        "authorities": [],
        "createdAt": ISODate("2025-09-27T00:00:00Z"),
        "updatedAt": ISODate("2025-09-27T00:00:00Z")
    },
]);

db.createCollection("accounts");
db.accounts.insertMany([
    {
        "_id": UUID("360d500e-3489-44a7-babf-fc0dd1611973"),
        "identityId": UUID("1b1ea002-5d0f-49e0-89db-c95d27108f67"),
        "name": "walter",
        "email": "walter@example.com",
        "address": "test address 321",
        "createdAt": ISODate("2025-09-27T00:00:00Z"),
        "updatedAt": ISODate("2025-09-27T00:00:00Z")
    },
    {
        "_id": UUID("6c49765d-f495-4339-bc59-3138254e30e8"),
        "identityId": UUID("b169e43e-597e-4012-90d9-a65bea99544e"),
        "name": "gus",
        "email": "gus@example.com",
        "address": "test address 123",
        "createdAt": ISODate("2025-09-27T00:00:00Z"),
        "updatedAt": ISODate("2025-09-27T00:00:00Z")
    }
]);

db.createCollection("restaurants");
db.restaurants.insertMany([
    {
        "_id": UUID("18732aaa-c9db-47b1-a814-006d3ea3ffce"),
        "ownerId": UUID("b169e43e-597e-4012-90d9-a65bea99544e"),
        "name": "Los Pollos Hermanos",
        "address": "Rua Araraquara 201",
        "cuisineType": "MEXICAN",
        "businessHours": {
            "THURSDAY": {
                "openingTime": ISODate("2025-09-27T10:00:00Z"),
                "closingTime": ISODate("2025-09-27T19:00:00Z")
            },
            "FRIDAY": {
                "openingTime": ISODate("2025-09-27T10:00:00Z"),
                "closingTime": ISODate("2025-09-27T22:00:00Z")
            },
            "SATURDAY": {
                "openingTime": ISODate("2025-09-27T10:00:00Z"),
                "closingTime": ISODate("2025-09-27T22:00:00Z")
            }
        },
        "lastUpdated": ISODate("2025-09-27T00:00:00Z")
    }
]);

db.createCollection("items");
db.items.insertMany([
    {
        "_id": UUID("f42ba4fd-54a0-47e6-bbb7-9b90e7528c16"),
        "restaurantId": UUID("18732aaa-c9db-47b1-a814-006d3ea3ffce"),
        "name": "Kids Chicken Nuggets",
        "description": "Six tender chicken nuggets with a mild flavor, perfect for kids.",
        "price": 6.75,
        "availableOnlyAtRestaurant": true,
        "photoPath": "/photos/los-pollos/nuggets.jpg",
        "lastUpdated": ISODate("2025-09-27T00:00:00Z")
    },
    {
        "_id": UUID("c71a9e0f-96a2-4a1b-8d0e-2f5a6b8c9d1e"),
        "restaurantId": UUID("18732aaa-c9db-47b1-a814-006d3ea3ffce"),
        "name": "Spicy Chicken Burrito",
        "description": "Grilled chicken, rice, beans, and extra spicy salsa wrapped in a warm tortilla.",
        "price": 12.50,
        "availableOnlyAtRestaurant": false,
        "photoPath": "/photos/los-pollos/burrito.jpg",
        "lastUpdated": ISODate("2025-09-27T00:00:00Z")
    },
    {
        "_id": UUID("e30c4d2b-1f87-43c9-9a70-8b6a5c4d3e2f"),
        "restaurantId": UUID("18732aaa-c9db-47b1-a814-006d3ea3ffce"),
        "name": "Tres Leches Cake",
        "description": "Sponge cake soaked in three kinds of milk: evaporated, condensed, and heavy cream.",
        "price": 7.99,
        "availableOnlyAtRestaurant": false,
        "photoPath": "/photos/los-pollos/tres-leches.jpg",
        "lastUpdated": ISODate("2025-09-27T00:00:00Z")
    }
]);

db.createCollection("orders");
db.orders.insertMany([
    {
        "_id": UUID("6e673e67-f0df-40ff-b693-a1ef34791b04"),
        "restaurantId": UUID("18732aaa-c9db-47b1-a814-006d3ea3ffce"),
        "customerId": UUID("1b1ea002-5d0f-49e0-89db-c95d27108f67"),
        "status": "PENDING",
        "items": [
            {
                "id": UUID("f42ba4fd-54a0-47e6-bbb7-9b90e7528c16"),
                "price": 6.75,
                "quantity": 1
            }
        ],
        "createdAt": ISODate("2025-09-28T00:00:00Z")
    },
    {
        "_id": UUID("a2b4c6d8-e0f1-42a3-84b5-c6d7e8f90a1b"),
        "restaurantId": UUID("18732aaa-c9db-47b1-a814-006d3ea3ffce"),
        "customerId": UUID("1b1ea002-5d0f-49e0-89db-c95d27108f67"),
        "status": "PENDING",
        "items": [
            {
                "id": UUID("c71a9e0f-96a2-4a1b-8d0e-2f5a6b8c9d1e"),
                "price": 12.50,
                "quantity": 2
            },
            {
                "id": UUID("e30c4d2b-1f87-43c9-9a70-8b6a5c4d3e2f"),
                "price": 7.99,
                "quantity": 1
            }
        ],
        "createdAt": ISODate("2025-09-29T00:00:00Z")
    }
]);