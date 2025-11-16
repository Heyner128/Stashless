# Stashless

Stashless is a lightweight inventory management system designed to help small businesses keep track of their stock levels.

## Features

- **Stock Management**: Monitor stock levels in real-time
- **Variant and Options Management**: Create, manage, and track product variants and options.
- **User Management**: Each user can have unlimited inventories and products

## Self-hosting

### Installation

Create a `.env` file with the following variables:

```ENV
DB_URL=jdbc:postgresql://localhost:5432/stashless
DB_USER=user
DB_PASSWORD=password
FRONTEND_URL=https://stashless.example.com
```


To start with Docker Compose:

```BASH
docker compose up -d
```


